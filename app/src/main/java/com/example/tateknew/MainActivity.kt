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
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.example.tateknew.databinding.ActivityMainBinding
import com.example.tateknew.utils.SecurityUtils

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var tokenManager: TokenManager
    private lateinit var securityUtils: SecurityUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        tokenManager = TokenManager(this)
        securityUtils = SecurityUtils(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val token = tokenManager.getToken()
        if (token != null && tokenManager.isTokenValid(token)) {
            authenticateUserOrCreatePin(token)
        } else {
            navigateToLogin()
        }

        setupUI()
        setupNavigationComponents()
    }

    private fun setupUI() {
        setSupportActionBar(binding.appBarMain.toolbar)
        binding.logOutButton.setOnClickListener {
            logOut()
        }
    }

    private fun logOut() {
        tokenManager.clearToken()
        securityUtils.clearPinCode()
        navigateToLogin()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun authenticateUserOrCreatePin(token: String) {
        val savedPin = securityUtils.getSavedPinCode()
        if (savedPin == null) {
            securityUtils.showCreatePinCodeDialog {
                processToken(token)
            }
        } else {
            securityUtils.showPinCodeDialog {
                processToken(token)
            }
        }
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
        val headerView = binding.navView.getHeaderView(0)
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
            setOf(R.id.nav_home, R.id.getData, R.id.TPsFragment),
            drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.nav_home)
                R.id.getData -> navController.navigate(R.id.getData)
                R.id.TPsFragment -> navController.navigate(R.id.TPsFragment)
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    override fun onPause() {
        super.onPause()
        securityUtils.showPinCodeDialog {
            // Do nothing, just revalidate PIN on resume
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}
