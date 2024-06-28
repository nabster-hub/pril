package com.example.tateknew

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject

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

    fun getTasks(token: String, dbHelper: DatabaseHelper): Boolean {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url + "tasks")
                .header("Authorization", "Bearer $token")
                .build()
            val response = client.newCall(request).execute()
            val tasks = response.body?.string()
            if (!tasks.isNullOrEmpty()) {
                val validJsonString = validateAndFixJson(tasks)
                val jsonObject = JSONObject(validJsonString)
                Log.d("Parsed JSON", jsonObject.toString())
                parseTasks(jsonObject, dbHelper)
                true
            } else {
                println("Received empty response")
                false
            }
        } catch (error: Throwable) {
            println("Error: $error")
            false
        }
    }

//    fun getTasks(token: String, dbHelper: DatabaseHelper, callback: (Boolean) -> Unit){
//        var thread = Thread{
//            val client = OkHttpClient()
//            val request = Request.Builder()
//                .url(url + "tasks")
//                .header("Authorization", "Bearer $token")
//                .build()
//            try {
//                val response = client.newCall(request).execute()
//                val tasks = response.body?.string()
//                if(!tasks.isNullOrEmpty()){
//                    val validJsonString = validateAndFixJson(tasks)
//                    val jsonObject = JSONObject(validJsonString)
//                    Log.d("Parsed JSON", jsonObject.toString())
//                    parseTasks(jsonObject, dbHelper)
//                    callback(true)
//                }else{
//                    println("Received empty response")
//                    callback(false)
//                }
//
//            } catch (error: Throwable) {
//                println("Error: $error")
//                callback(false)
//            }
//        }
//        thread.start()
//    }

    private fun parseTasks(tasksObject: JSONObject, dbHelper: DatabaseHelper) {
        tasksObject.keys().forEach { key ->
            val objectsArray = tasksObject.getJSONArray(key)
            for (i in 0 until objectsArray.length()) {
                val obj = objectsArray.getJSONObject(i)
                dbHelper.insertObject(obj)
            }
        }
    }

    fun validateAndFixJson(jsonString: String): String {
        val string = jsonString.replace("\\\\", "\\")

        try{
            val jsonObject = JSONObject(jsonString)
        }catch (e: JSONException){
            Log.e("JSONParsing", "Error parsing JSON at character: ${e.message}")
            Log.e("JSONParsing", "JSON string: $jsonString")
        }
        return string
    }

}