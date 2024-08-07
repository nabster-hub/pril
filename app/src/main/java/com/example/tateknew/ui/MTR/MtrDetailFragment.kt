package com.example.tateknew.ui.MTR

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.tateknew.R
import com.example.tateknew.databinding.FragmentMtrDetailBinding

class MtrDetailFragment : Fragment() {

    private lateinit var binding: FragmentMtrDetailBinding
    private val REQUEST_IMAGE_CAPTURE = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMtrDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mtrId = arguments?.getLong("mtrId")

        // Установка типа счетчика и номера счетчика из аргументов
        binding.tvMeterType.text = "Тип счётчика: $mtrId" // Замените это фактическим значением
        binding.tvMeterNumber.text = "Номер счётчика: $mtrId" // Замените это фактическим значением

        // Настройка Spinner
        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.situation_array,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSituation.adapter = adapter

        // Настройка кнопки для захвата фото
        binding.btnTakePhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.ivPhoto.setImageBitmap(imageBitmap)
        }
    }
}
