package com.example.tateknew.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.tateknew.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        
        val tvWelcome: TextView = binding.tvWelcome
        val tvNext: TextView = binding.tvNext
        
        homeViewModel.tvWelcome.observe(viewLifecycleOwner){
            tvWelcome.text = it
        }
        homeViewModel.tvNext.observe(viewLifecycleOwner){
            tvNext.text = it
        }

        homeViewModel.setWelcome("Добро пожаловать, вы находитесь на первой странице для получения задачи перейдите в меню и переместитесь на странцу получения задач")
        homeViewModel.setNext("Желаем вам хорошего и продуктивного дня")

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}