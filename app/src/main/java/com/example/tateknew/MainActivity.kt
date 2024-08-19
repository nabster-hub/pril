package com.example.tateknew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import android.widget.EditText
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.example.tateknew.databinding.ActivityMainBinding
import android.app.AlertDialog
import android.view.LayoutInflater

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private var isBiometricAuthenticated = false  // Флаг для отслеживания аутентификации

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        val token = tokenManager.getToken()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (token != null && tokenManager.isTokenValid(token)) {
            val savedPin = getSavedPinCode()
            if(savedPin != null){
                authenticateUser()
            }else{
                processToken(token)
            }

        } else {
            navigateToLogin()
        }

        // Настройка панели инструментов и навигации только после проверки токена и аутентификации
        setSupportActionBar(binding.appBarMain.toolbar)
        setupNavigationComponents()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun showBiometricPrompt() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isBiometricAuthenticated = true  // Устанавливаем флаг, что аутентификация прошла успешно
                    val token = TokenManager(this@MainActivity).getToken()
                    if (token != null && TokenManager(this@MainActivity).isTokenValid(token)) {
                        processToken(token)
                    } else {
                        navigateToLogin()
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Биометрический вход")
            .setSubtitle("Войдите в систему, используя свои биометрические данные")
            .setNegativeButtonText("Использовать пароль учетной записи")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun processToken(token: String) {
        try {
            val jwt = JWT(token)
            val name = jwt.getClaim("name").asString()
            val email = jwt.getClaim("email").asString()
            updateNavHeader(name ?: "", email ?: "")
        } catch (e: DecodeException) {
            Log.e("MainActivity", "Invalid token", e)
            navigateToLogin()
        }
    }

    private fun updateNavHeader(name: String, email: String) {
        val navigationView: NavigationView = binding.navView
        val headerView = navigationView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.tvName)
        val navEmail: TextView = headerView.findViewById(R.id.tvEmail)
        navUsername.text = name
        navEmail.text = email
    }

    private fun setupNavigationComponents() {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.getData, R.id.TPsFragment, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.nav_home)
                R.id.getData -> navController.navigate(R.id.getData)
                R.id.TPsFragment -> navController.navigate(R.id.TPsFragment)
                R.id.nav_gallery -> navController.navigate(R.id.nav_gallery)
                R.id.nav_slideshow -> navController.navigate(R.id.nav_slideshow)
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun checkBiometricSupport(): Boolean {
        val biometricManager = BiometricManager.from(this)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE,
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE,
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> false
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkPinCode(inputPin: String): Boolean {
        val savedPin = getSavedPinCode()
        return savedPin != null && savedPin == inputPin
    }

    private fun getSavedPinCode(): String? {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("user_pin_code", null)
    }

    private fun savePinCode(pinCode: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        sharedPreferences.edit().putString("user_pin_code", pinCode).apply()
    }

    private fun authenticateUser() {
        if (checkBiometricSupport()) {
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login")
                .setSubtitle("Log in using your biometric credential")
                .setNegativeButtonText("Use PIN code")
                .build()

            val biometricPromptCallback = BiometricPrompt(this, ContextCompat.getMainExecutor(this),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                            handlePinCode() // Обрабатываем PIN-код
                        } else {
                            navigateToLogin()
                        }
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        isBiometricAuthenticated = true  // Устанавливаем флаг, что аутентификация прошла успешно
                        val token = TokenManager(this@MainActivity).getToken()
                        if (token != null && TokenManager(this@MainActivity).isTokenValid(token)) {
                            processToken(token)
                        } else {
                            navigateToLogin()
                        }
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                })

            biometricPromptCallback.authenticate(promptInfo)
        } else {
            handlePinCode() // Если биометрия недоступна, сразу обрабатываем PIN-код
        }
    }

    private fun handlePinCode() {
        val savedPin = getSavedPinCode()
        if (savedPin == null) {
            // PIN-код еще не установлен, показываем диалог для создания нового PIN-кода
            showCreatePinCodeDialog()
        } else {
            // PIN-код уже существует, показываем диалог для ввода PIN-кода
            showPinCodeDialog()
        }
    }

    private fun showCreatePinCodeDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_create_pin_code, null)
        val pinCodeEditText = dialogView.findViewById<EditText>(R.id.pinCodeEditText)
        val confirmPinCodeEditText = dialogView.findViewById<EditText>(R.id.confirmPinCodeEditText)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btnSubmitPin).setOnClickListener {
            val pin = pinCodeEditText.text.toString()
            val confirmPin = confirmPinCodeEditText.text.toString()

            if (pin.isEmpty() || confirmPin.isEmpty()) {
                Toast.makeText(this, "Please enter and confirm your PIN code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pin == confirmPin) {
                savePinCode(pin)
                Toast.makeText(this, "PIN code created successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                // Продолжаем аутентификацию
                handlePinCode()
            } else {
                Toast.makeText(this, "PIN codes do not match", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showPinCodeDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_pin_code, null)
        val pinCodeEditText = dialogView.findViewById<EditText>(R.id.pinCodeEditText)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btnSubmitPin).setOnClickListener {
            val inputPin = pinCodeEditText.text.toString()
            if (checkPinCode(inputPin)) {
                dialog.dismiss()
                val token = TokenManager(this).getToken()
                if (token != null && TokenManager(this).isTokenValid(token)) {
                    processToken(token)
                } else {
                    navigateToLogin()
                }
            } else {
                Toast.makeText(this, "Invalid PIN code", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }



}
