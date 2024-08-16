package com.example.tateknew.utils

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun shouldShowRequestPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    fun showExplanationDialog(context: Context, onPositiveClick: () -> Unit) {
        android.app.AlertDialog.Builder(context)
            .setMessage("Это приложение нуждается в доступе к камере для фотографирования.")
            .setPositiveButton("Разрешить") { dialog, which ->
                onPositiveClick()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    fun checkAndRequestPermissions(activity: Activity, permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(permission), requestCode)
        }
    }
}
