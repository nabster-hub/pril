package com.example.tateknew

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.tateknew.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private var _binding: ActivityLoginBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("ActivityMainBinding is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        var tokenManager = TokenManager(this)

        enableEdgeToEdge()

        with(binding){
            buttonSubmit.setOnClickListener {
                var email = emailValue.text.toString();
                var password = passwordValue.text.toString();
                if(!isValidEmail(email)){
                    showError(ContextCompat.getString(this@LoginActivity, R.string.invalid_email))
                    return@setOnClickListener
                }

                val networkManager = NetworkManager()

                networkManager.getToken(
                    email,
                    password,
                    object : NetworkManager.TokenCallback {
                        override fun onTokenReceived(token: String?) {
                            runOnUiThread {
                                if (token != null) {
                                    try{
                                        if(isJson(token)){
                                            tokenManager.saveToken(token)
//                                            val newToken = tokenManager.getToken();
//                                            println(newToken)
                                            navigateToMain()
                                        }else{
                                            showError(ContextCompat.getString(this@LoginActivity, R.string.problem_login))
                                        }

                                    } catch (e: Exception){
                                        println("Error saving token: ${e.message}")
                                        showError(ContextCompat.getString(this@LoginActivity, R.string.problem_login))
                                    }

                                } else {
                                    showError("Failed to retrieve token")
                                }
                            }
                        }

                        override fun onError(error: Throwable) {
                            println("Error: ${error.message}")
                            runOnUiThread {
                                Toast.makeText(this@LoginActivity, "Error: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                )

            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun isJson(token: String): Boolean {
        return try {
            JSONObject(token)
            true
        } catch (e: JSONException) {
            false
        }
    }

    private fun showError(message: String) {
        binding.tvErrors.text = message
        binding.tvErrors.setTextColor(ContextCompat.getColor(this@LoginActivity, R.color.red))
        binding.tvErrors.isVisible = true
    }
}