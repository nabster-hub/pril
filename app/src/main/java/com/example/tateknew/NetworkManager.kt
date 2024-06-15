package com.example.tateknew

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class NetworkManager {

    private val url = "http://nabster.tplinkdns.com:8000/api/"
    interface TokenCallback {
        fun onTokenReceived(token: String?)
        fun onError(error: Throwable)
    }

    fun getToken(email: String, password: String, callback: TokenCallback) {

        val thread = Thread {
            val client = OkHttpClient()
            val requestBody = "{\"email\":\"$email\",\"password\":\"$password\"}"

            val body = requestBody.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url + "auth/login")
                .post(body)
                .build()

            try {
                val response = client.newCall(request).execute()
                val token = response.body?.string()
                callback.onTokenReceived(token)
            } catch (error: Throwable) {
                callback.onError(error)
            }
        }
        thread.start()
    }

    fun getUserInfo(token: String, url: String){
        var thread = Thread{
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .header("Authorization", "Bearer $token")
                .build()

            try {
                val response = client.newCall(request).execute()
                val userInfo = response.body?.string()
                println(userInfo)
            } catch (error: Throwable) {
                println("Error: $error")
            }
        }
        thread.start()

    }

    fun getTasks(token: String){
        var thread = Thread{
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url + "tasks")
                .header("Authorization", "Bearer $token")
                .build()
            try {
                val response = client.newCall(request).execute()
                val tasks = response.body?.string()
                println(tasks)
            } catch (error: Throwable) {
                println("Error: $error")
            }
        }
        thread.start()
    }
}