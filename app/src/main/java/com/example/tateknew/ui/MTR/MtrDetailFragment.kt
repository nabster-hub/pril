package com.example.tateknew.ui.MTR

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.tateknew.R
import com.example.tateknew.databinding.FragmentMtrDetailBinding


class MtrDetailFragment : Fragment() {

    private val args: MtrDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentMtrDetailBinding

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
        Log.d("Test", "В MtrDetailFragment")
        // Set the mtrId
        val mtrId = args.mtrId

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
        binding.mtrType.text = "Пример типа счетчика"
        binding.mtrNumber.text = "123456"

        // Set up button to take photo
        binding.takePhoto.setOnClickListener {
            // Implement taking a photo
        }
    }
}
