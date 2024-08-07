package com.example.tateknew.ui.MTR

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tateknew.R
import com.example.tateknew.databinding.FragmentMtrBinding

class MTRFragment : Fragment() {

    private var mtrId: Long = 0
    private lateinit var binding: FragmentMtrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mtrId = it.getLong("mtrId")
        }
        
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentMtrBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })

        binding.button.setOnClickListener { // неправильно работает блок так как выводить не фрагмент mtr, а item-mtr
            val action = MtrFragmentDirections.actionMtrFragmentToMtrDetailFragment(mtrId = 1L)
            findNavController().navigate(action)
        }
    }
}
