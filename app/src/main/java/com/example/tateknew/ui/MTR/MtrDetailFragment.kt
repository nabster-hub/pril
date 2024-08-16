package com.example.tateknew.ui.MTR

import MtrDetailViewModelFactory
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.navigation.fragment.navArgs
import com.example.tateknew.R
import com.example.tateknew.databinding.FragmentMtrDetailBinding
import com.example.tateknew.utils.LocationHandler
import com.example.tateknew.utils.PermissionUtils

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
        if (currentReading != null) {
            val difference = currentReading - mtrVl
            val percentageDifference = difference / sredRashod * 100
            if (percentageDifference > 20 || percentageDifference < -20) {
                showConfirmationDialog()
            } else {
                saveData()
            }
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
        Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT).show()
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
            cameraHandler.dispatchTakePictureIntent()
        }
    }

}
