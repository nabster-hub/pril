package com.example.tateknew.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.example.tateknew.databinding.FragmentMtrDetailBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {

    fun getFileFromUri(context: Context, uri: Uri): File {
        val contentResolver = context.contentResolver
        val file = File(context.cacheDir, "temp_image")
        contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return file
    }

    fun compressImage(context: Context, originalFile: File, binding: FragmentMtrDetailBinding) {
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
        val resizedBitmap = resizeBitmap(bitmap, 1024, 768)
        val compressedFile = createCompressedFile(context)

        FileOutputStream(compressedFile).use { out ->
            resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 80, out)
        }

        resizedBitmap.recycle()
        bitmap.recycle()

        if (compressedFile.exists()) {
            originalFile.delete()
        }

        binding.imageView.setImageURI(Uri.fromFile(compressedFile))
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val ratio = minOf(maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height)
        val newWidth = (bitmap.width * ratio).toInt()
        val newHeight = (bitmap.height * ratio).toInt()
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    private fun createCompressedFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File(storageDir, "JPEG_${timeStamp}.webp")
    }

    fun deleteFileFromUri(context: Context, uri: Uri) {
        try {
            val deleted = context.contentResolver.delete(uri, null, null) > 0
            if (deleted) {
                Log.d("FileDeletion", "Original file deleted successfully")
            } else {
                Log.d("FileDeletion", "Failed to delete original file.")
            }
        } catch (e: Exception) {
            Log.d("FileDeletion", "Error deleting file: ${e.message}")
        }
    }
}
