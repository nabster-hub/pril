package com.example.tateknew

import android.content.Context
import android.util.Log
import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonSyntaxException

class AuthManager(private val context: Context, private val tokenManager: TokenManager) {

    fun isUserAuthenticated(): Boolean {
        val token = tokenManager.getToken()
        return if(token != null){
            try{
                val jsonObject = Gson().fromJson<JsonObject>(token, JsonObject::class.java)
                val accessToken = jsonObject.get("access_token").asString

                if(tokenManager.isTokenExpired(accessToken)){
                    true
                }else{
                    tokenManager.clearToken()
                    false
                }
            } catch (e: JsonSyntaxException){
               Log.e("AuthManager", "Invalid JSON format", e)
               false
            } catch (e: DecodeException){
                Log.e("AuthManager", "Invalid JWT token", e)
                false
            }
        }else{
            false
        }
    }

    fun getUserDetails(): Pair<String?, String?>? {
        val token = tokenManager.getToken()
        return if (token != null) {
            try {
                val jsonObject = Gson().fromJson<JsonObject>(token, JsonObject::class.java)
                val accessToken = jsonObject.get("access_token").asString
                val jwt = JWT(accessToken)
                val name = jwt.getClaim("name").asString()
                val email = jwt.getClaim("email").asString()
                Pair(name, email)
            } catch (e: Exception) {
                Log.e("AuthManager", "Error parsing token", e)
                null
            }
        } else {
            null
        }
    }

}