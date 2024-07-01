package com.example.tateknew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        val token = tokenManager.getToken();


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(token != null) {
                 token.let {
                    try {
                        val jsonObject = Gson().fromJson<JsonObject>(it, JsonObject::class.java)
                        val accessToken = jsonObject.get("access_token").asString

                        if (tokenManager.isTokenValid(accessToken)) {
                            val jwt = JWT(accessToken)
                            val name = jwt.getClaim("name").asString();
                            val email = jwt.getClaim("email").asString();
                            updateNavHeader(name?:"", email?:"")
                        }else{
                            tokenManager.clearToken()
                            navigateToLogin()
                        }
                    }catch (e: JsonSyntaxException){
                        Log.e("MainActivity", "Invalid JSON format", e)
                    }catch (e: DecodeException) {
                        Log.e("MainActivity", "Invalid token", e)
                    }
                }

        }else{
            navigateToLogin()
        }

        setSupportActionBar(binding.appBarMain.toolbar)

        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.getData, R.id.objectListFragment, R.id.nav_gallery, R.id.nav_slideshow
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)




        navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.nav_home -> {
                    navController.navigate(R.id.nav_home)
                }
                R.id.getData -> {
                    navController.navigate(R.id.getData)

                    println("home")
                }
                R.id.objectListFragment ->{
                    navController.navigate(R.id.objectListFragment)
                }
                R.id.nav_gallery -> {
                    // Ваш код для обработки нажатия на Gallery
                    navController.navigate(R.id.nav_gallery)
                    println("gallery")
                }
                R.id.nav_slideshow -> {
                    // Ваш код для обработки нажатия на Slideshow
                    navController.navigate(R.id.nav_slideshow)
                    println("Slideshow")
                }
            }
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun getUserInfo(accessToken: String, url: String) {
        val networkManager = NetworkManager()
        networkManager.getUserInfo(accessToken, url)

    }

//    private fun confirmBiomentrics(): Boolean {
//
//    }

    private fun updateNavHeader(name: String, email: String) {
        val navigationView: NavigationView = binding.navView
        val headerView = navigationView.getHeaderView(0)
        val navUsername: TextView = headerView.findViewById(R.id.tvName)
        val navEmail: TextView = headerView.findViewById(R.id.tvEmail)
        navUsername.text = name
        navEmail.text = email
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}