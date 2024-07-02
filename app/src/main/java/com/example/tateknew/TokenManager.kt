package com.example.tateknew

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import org.json.JSONObject
import java.nio.charset.Charset
import java.util.Date

class TokenManager(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        "token_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveToken(token: String) {
        try {
            val editor = sharedPreferences.edit()
            editor.putString("jwt_token", token)
            editor.apply()
            Log.d("TokenManager", "Token saved successfully")
        } catch (e: Exception) {
            Log.e("TokenManager", "Error saving token: ${e.message}")
        }
    }

    fun getToken(): String? {
        return try {
            val token = sharedPreferences.getString("jwt_token", null)
            Log.d("TokenManager", "Retrieved token: $token")
            token?.trim('"')
        } catch (e: Exception) {
            Log.e("TokenManager", "Error retrieving token: ${e.message}")
            null
        }
    }

    fun clearToken() {
        try {
            val editor = sharedPreferences.edit()
            editor.remove("jwt_token")
            editor.apply()
            Log.d("TokenManager", "Token cleared successfully")
        } catch (e: Exception) {
            Log.e("TokenManager", "Error clearing token: ${e.message}")
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) {
                Log.e("TokenManager", "Invalid JWT token format")
                return true // Invalid JWT
            }

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charset.forName("UTF-8"))
            val jsonPayload = JSONObject(payload)

            val exp = jsonPayload.getLong("exp")
            val now = Date().time / 1000

            now > exp
        } catch (e: Exception) {
            Log.e("TokenManager", "Error checking token expiration: ${e.message}")
            true
        }
    }

    fun isTokenValid(token: String): Boolean {
        return !isTokenExpired(token);
    }
}
