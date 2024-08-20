package com.example.tateknew.ui.MTR

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.example.tateknew.databinding.FragmentMtrDetailBinding
import com.example.tateknew.utils.FileUtils
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraHandler(private val fragment: Fragment, private val binding: FragmentMtrDetailBinding) {
    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null

    companion object {
        const val REQUEST_TAKE_PHOTO = 1
    }

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(fragment.requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("CameraHandler", "Error creating image file", ex)
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        fragment.requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    photoUri = photoURI
                    currentPhotoPath = it.absolutePath
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    fragment.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    fun handlePhotoResult() {
        photoUri?.let { uri ->
            val originalFile = FileUtils.getFileFromUri(fragment.requireContext(), uri)
            val compressedFile = FileUtils.compressImage(fragment.requireContext(), originalFile, binding)
            if (compressedFile != null) {
                // Обновляем путь к фото на новый после сжатия
                currentPhotoPath = compressedFile.absolutePath
            }
            FileUtils.deleteFileFromUri(fragment.requireContext(), uri)
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = fragment.requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
    fun getCurrentPhotoPath(): String? {
        return currentPhotoPath
    }
}
