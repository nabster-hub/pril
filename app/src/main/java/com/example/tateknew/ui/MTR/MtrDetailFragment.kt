package com.example.tateknew.ui.MTR

import MtrDetailViewModelFactory
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.tateknew.R
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.MeterReading
import com.example.tateknew.databinding.FragmentMtrDetailBinding
import com.example.tateknew.utils.LocationHandler
import com.example.tateknew.utils.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.Date

class MtrDetailFragment : Fragment() {

    private val args: MtrDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentMtrDetailBinding
    private lateinit var viewModel: MtrDetailViewModel
    private lateinit var cameraHandler: CameraHandler
    private lateinit var locationHandler: LocationHandler
    private var mtrVl: Double = 0.0
    private var sredRashod = 0
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var isPhotoTaken = false

    companion object {
        private const val REQUEST_CAMERA_PERMISSION = 101
        private val LOCATION_PERMISSION_REQUEST_CODE = 102
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMtrDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this, MtrDetailViewModelFactory(requireContext())).get(MtrDetailViewModel::class.java)
        cameraHandler = CameraHandler(this, binding)

        loadMeterReadingData()

        locationHandler = LocationHandler(requireContext())

        val mtrId = args.mtrId
        viewModel.getMtrDetail(mtrId).observe(viewLifecycleOwner) { mtrDetails ->
            if (mtrDetails.isNotEmpty()) {
                val mtr = mtrDetails.first()
                binding.mtrType.text = mtr.mtr.name
                binding.mtrNumber.text = mtr.mtr.itemNo
                mtrVl = mtr.mtr.vl.toDoubleOrNull()!!
                sredRashod = mtr.mtr.sredrashod!!
            }
        }

        setupUI()
    }

    private fun setupUI() {
        val spinnerAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.situation_array,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.situationSpinner.adapter = spinnerAdapter
        binding.situationSpinner.setSelection(0)

        binding.takePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissions()
            } else {
                requestCameraPermission()
            }
        }

        binding.Save.setOnClickListener {
            validateAndSave()
        }
    }

    private fun requestCameraPermission() {
        if (PermissionUtils.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.CAMERA)) {
            PermissionUtils.showExplanationDialog(requireContext()) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            }
        } else {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        }
    }

    private fun validateAndSave() {
        val currentReading = binding.currentReading.text.toString().toDoubleOrNull()

        if(!isPhotoTaken){
            AlertDialog.Builder(requireContext())
                .setTitle("Отсутвует фото")
                .setMessage("Данные без фото счетчика не принимаются")
                .setNegativeButton("Ок") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        if (latitude == 0.0 || longitude == 0.0) {
            AlertDialog.Builder(requireContext())
                .setTitle("Отсутвует данные GPS")
                .setMessage("GPS данные отсутствуют. Пожалуйста, включите GPS и сделайте фото.")
                .setNegativeButton("Ок") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        if (currentReading != null) {
            val difference = currentReading - mtrVl
            val percentageDifference = difference / sredRashod * 100
            if (percentageDifference > 20 || percentageDifference < -20) {
                showConfirmationDialog()
            } else {
                saveData()
            }
        }else{
            AlertDialog.Builder(requireContext())
                .setTitle("Отсутвует данные по расходу")
                .setMessage("Данные по счётчику не предоставленны")
                .setNegativeButton("Ок") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Подтверждение")
            .setMessage("Расход электроэнергии отличается более чем на 20%. Если вы уверены в результате, нажмите 'Да', иначе нажмите 'Нет' и измените данные.")
            .setPositiveButton("Да") { _, _ -> saveData() }
            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun saveData() {
        val currentReadingText = binding.currentReading.text.toString()
        val currentReading = currentReadingText.toBigDecimalOrNull()
        val photoPath = cameraHandler.getCurrentPhotoPath() // Получаем путь к фото
        if (photoPath.isNullOrEmpty() || !File(photoPath).exists()) {  // Обработка случая, когда photoPath может быть null или пустым
            Toast.makeText(context, "Необходимо сделать фото", Toast.LENGTH_SHORT).show()
            return
        }

        if (currentReading != null && isPhotoTaken && latitude != 0.0 && longitude != 0.0) {
            val meterReading = MeterReading(
                mtrId = args.mtrId,
                currentReading = currentReading,
                photoPath = photoPath,
                latitude = latitude,
                longitude = longitude,
                createdAt = Date()
            )

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                withContext(Dispatchers.IO){
                    db.meterReadingDao().insertMeterReading(meterReading)
                }
                withContext(Dispatchers.Main){
                    Toast.makeText(context, "Данные сохранены", Toast.LENGTH_SHORT).show()

                    findNavController().popBackStack(R.id.abonentsFragment, false)
                }

            }
        } else {
            Toast.makeText(context, "Необходимо заполнить все поля и сделать фото", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CAMERA_PERMISSION && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraHandler.dispatchTakePictureIntent()
        } else {
            Toast.makeText(context, "Разрешение на использование камеры не предоставлено", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CameraHandler.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            isPhotoTaken = true
            cameraHandler.handlePhotoResult()
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запрос разрешений на доступ к местоположению
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // Разрешения уже предоставлены
            getLocationAndTakePhoto()
        }
    }

    private fun getLocationAndTakePhoto() {
        locationHandler.getCurrentLocation { location ->
            if (location != null) {
                latitude = location.latitude
                longitude = location.longitude
                // Обработка координат: сохранение или добавление в метаданные
                Log.d("Locations", "Latitude: ${latitude}")
                Log.d("Locations", "longitude: ${longitude}")
            }
            if (!isPhotoTaken) {  // Проверка перед открытием камеры
                cameraHandler.dispatchTakePictureIntent()
            }
        }
    }

    private fun loadMeterReadingData() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())

            // Получаем данные последнего ввода для данного счетчика
            val lastMeterReading = withContext(Dispatchers.IO) {
                db.meterReadingDao().getLastMeterReadingForMtr(args.mtrId)
            }

            // Если данные существуют, отображаем их
            lastMeterReading?.let { reading ->
                binding.currentReading.setText(reading.currentReading.toString())
                binding.imageView.setImageURI(Uri.parse(reading.photoPath))
                latitude = reading.latitude
                longitude = reading.longitude
                isPhotoTaken = true
                cameraHandler.setCurrentPhotoPath(reading.photoPath)
            }
        }
    }

}
