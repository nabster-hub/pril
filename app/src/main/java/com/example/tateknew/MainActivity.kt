package com.example.tateknew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
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
import androidx.lifecycle.lifecycleScope
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.example.tateknew.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.launch

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
            if (checkBiometricSupport() && !isBiometricAuthenticated) {
                showBiometricPrompt()
            } else {
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
}
