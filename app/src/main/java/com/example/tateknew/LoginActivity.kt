package com.example.tateknew

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.tateknew.databinding.ActivityLoginBinding

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
                    tvErrors.text = ContextCompat.getString(
                        this@LoginActivity,
                        R.string.invalid_email
                    );
                    tvErrors.setTextColor(
                        ContextCompat.getColor(
                            this@LoginActivity,
                            R.color.red
                        )
                    )
                    tvErrors.isVisible = true
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
                                    tokenManager.saveToken(token)
                                    var newToken = tokenManager.getToken();
                                    println(newToken)
                                    navigateToMain()
                                } else {
                                    Toast.makeText(this@LoginActivity, "Failed to retrieve token", Toast.LENGTH_LONG).show()
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
}