package com.example.tateknew.ui.MTR

import MtrDetailViewModelFactory
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.example.tateknew.R
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.databinding.FragmentMtrDetailBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MtrDetailFragment : Fragment() {

    private val args: MtrDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentMtrDetailBinding
    private lateinit var viewModel: MtrDetailViewModel
    private var photoUri: Uri? = null
    private var mtrVl: Double = 0.0
    private var sredRashod = 0

    companion object {
        private const val REQUEST_TAKE_PHOTO = 1
        private const val REQUEST_CAMERA_PERMISSION = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("MtrDetailFragment", "onCreateView called")
        binding = FragmentMtrDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, MtrDetailViewModelFactory(requireContext())).get(MtrDetailViewModel::class.java)

        // Get mtrId from arguments
        val mtrId = args.mtrId


        viewModel.getMtrDetail(mtrId).observe(viewLifecycleOwner) { mtrDetails ->
            // Assuming mtrDetails is a list of MtrWithAbonent, handle the case accordingly
            if (mtrDetails.isNotEmpty()) {
                val mtr = mtrDetails.first()
                binding.mtrType.text = mtr.mtr.name
                binding.mtrNumber.text = mtr.mtr.itemNo
                mtrVl = mtr.mtr.vl.toDoubleOrNull()!!
                sredRashod = mtr.mtr.sredrashod!!
                // Other UI updates here
            }
        }
        // Set up the spinner
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.situation_array,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.situationSpinner.adapter = spinnerAdapter

        // Set default selection
        binding.situationSpinner.setSelection(0)

        // Example of setting some data (replace with real data)
//        binding.mtrType.text = "Пример типа счетчика"
//        binding.mtrNumber.text = "123456"

        // Set up button to take photo
        binding.takePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                requestCameraPermission()
            }
        }

        binding.Save.setOnClickListener{
            validAndSave()
        }
    }

    private fun validAndSave() {
        val currentReading = binding.currentReading.text.toString().toDoubleOrNull()

        if(currentReading != null){
            val difference = currentReading - mtrVl
            val percentageDifference = difference / sredRashod * 100
            if(percentageDifference > 20 || percentageDifference < -20){
                showConfirmationDialo()
            }else{
                saveData()
            }
        }
    }

    private fun showConfirmationDialo(){
        AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение")
            .setMessage("Расход электроэнергии отличается более чем на 20%. Если вы уверены в результате, нажмите 'Да', иначе нажмите 'Нет' и измените данные.")
            .setPositiveButton("Да"){ dialog, which ->
                saveData()
            }
            .setNegativeButton("Нет"){dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
    private fun saveData() {
        Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Сохранение URI для использования с камерой
            photoUri = FileProvider.getUriForFile(requireContext(), "com.example.android.fileprovider", this)
        }
    }
    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireContext(),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            photoUri?.let { uri ->
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                //val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                val originalFile = getFileFromUri(uri)

                compressImage(originalFile)
                deleteFileFromUri(uri)
            }
        }
    }
    private fun requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            // Здесь можно объяснить пользователю, зачем нужно это разрешение, например, через диалоговое окно.
            showExplanationDialog()
        } else {
            // Запрос разрешения
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun showExplanationDialog() {
        AlertDialog.Builder(context)
            .setMessage("Это приложение нуждается в доступе к камере для фотографирования.")
            .setPositiveButton("Разрешить") { dialog, which ->
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Разрешение было предоставлено, продолжить работу с камерой
                    dispatchTakePictureIntent()
                } else {
                    // Разрешение не предоставлено, показать сообщение о необходимости разрешения
                    Toast.makeText(context, "Разрешение на использование камеры не предоставлено", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    fun compressImage(originalFile: File) {
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath)
        // Получение размеров изображения
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        // Целевые размеры
        val maxWidth = 1024
        val maxHeight = 768

        // Рассчет масштаба
        val widthRatio = maxWidth.toFloat() / originalWidth
        val heightRatio = maxHeight.toFloat() / originalHeight
        val ratio = minOf(widthRatio, heightRatio) // Используем minOf для сравнения двух значений

        // Новые размеры
        val newWidth = (originalWidth * ratio).toInt()
        val newHeight = (originalHeight * ratio).toInt()

        // Масштабирование Bitmap
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        // Сохранение и сжатие изображения
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        val imageFile = File(storageDir, "JPEG_${timeStamp}.webp")

        FileOutputStream(imageFile).use { out ->
            resizedBitmap.compress(Bitmap.CompressFormat.WEBP, 80, out) // Сжатие с качеством 80
        }

        // Очищаем память
        resizedBitmap.recycle()
        bitmap.recycle()

        if (imageFile.exists()) {
            originalFile.delete()  // Delete the original file if the compressed one is successfully created
        }

        // Обновление ImageView с новым сжатым и масштабированным изображением
        binding.imageView.setImageURI(Uri.fromFile(imageFile))
    }

    private fun getFileFromUri(uri: Uri): File {
        val contentResolver = requireContext().contentResolver
        val file = File(requireContext().cacheDir, "temp_image") // временный файл
        contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return file
    }

    fun deleteFileFromUri(uri: Uri) {
        try {
            val deleted = requireContext().contentResolver.delete(uri, null, null) > 0
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
