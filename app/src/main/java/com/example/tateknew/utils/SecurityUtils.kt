package com.example.tateknew.utils

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.tateknew.R
import com.example.tateknew.TokenManager

class SecurityUtils(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun getSavedPinCode(): String? {
        return sharedPreferences.getString("user_pin_code", null)
    }

    fun savePinCode(pinCode: String) {
        sharedPreferences.edit().putString("user_pin_code", pinCode).apply()
    }

    fun clearPinCode() {
        sharedPreferences.edit().remove("user_pin_code").apply()
    }

    fun showCreatePinCodeDialog(onPinCreated: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_create_pin_code, null)
        val pinCodeEditText = dialogView.findViewById<EditText>(R.id.pinCodeEditText)
        val confirmPinCodeEditText = dialogView.findViewById<EditText>(R.id.confirmPinCodeEditText)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btnSubmitPin).setOnClickListener {
            val pin = pinCodeEditText.text.toString()
            val confirmPin = confirmPinCodeEditText.text.toString()

            if (pin.isEmpty() || confirmPin.isEmpty()) {
                Toast.makeText(context, "Please enter and confirm your PIN code", Toast.LENGTH_SHORT).show()
            } else if (pin == confirmPin) {
                savePinCode(pin)
                Toast.makeText(context, "PIN code created successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                onPinCreated()
            } else {
                Toast.makeText(context, "PIN codes do not match", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    fun showPinCodeDialog(onPinValidated: () -> Unit) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_pin_code, null)
        val pinCodeEditText = dialogView.findViewById<EditText>(R.id.pinCodeEditText)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btnSubmitPin).setOnClickListener {
            val inputPin = pinCodeEditText.text.toString()
            if (checkPinCode(inputPin)) {
                dialog.dismiss()
                onPinValidated()
            } else {
                Toast.makeText(context, "Invalid PIN code", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun checkPinCode(inputPin: String): Boolean {
        val savedPin = getSavedPinCode()
        return savedPin != null && savedPin == inputPin
    }
}
