package com.example.tateknew

import android.content.Context
import android.util.Base64
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
        val editor = sharedPreferences.edit()
        editor.putString("jwt_token", token)
        editor.apply()
    }

    fun getToken(): String? {
        val token = sharedPreferences.getString("jwt_token", null)
        println("Retrieved token: $token")
        return token?.let{
            it.trim('"')
        }
    }

    fun clearToken() {
        val editor = sharedPreferences.edit()
        editor.remove("jwt_token")
        editor.apply()
    }

    fun isTokenExpired(token: String): Boolean {
        try {
            val parts = token.split(".")
           // print(parts)
            if (parts.size != 3) {
                return true // Invalid JWT
            }

            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE), Charset.forName("UTF-8"))
            val jsonPayload = JSONObject(payload)

            val exp = jsonPayload.getLong("exp")
            val now = Date().time / 1000

            return now > exp
            return false
        } catch (e: Exception) {
            e.printStackTrace()
            return true
        }
    }

    fun isTokenValid(token: String): Boolean {
        return !isTokenExpired(token);
    }
}
