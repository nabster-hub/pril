package com.example.tateknew.ui.MTR

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tateknew.R
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.FragmentMtrBinding
import com.example.tateknew.ui.getData.OnAbonentClickListener

class MTRFragment : Fragment() {

    private var mtrId: Long = 0
    private lateinit var binding: FragmentMtrBinding
    private lateinit var mtrAdapter: MtrAdapter
    private val mtrList: List<MtrWithAbonent> = listOf()

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

        // Инициализация адаптера
        mtrAdapter = MtrAdapter(mtrList, object : OnAbonentClickListener {
            override fun onAbonentClick(mtrId: Long) {
                // Вызывается, когда элемент списка нажимается
                val action = MTRFragmentDirections.actionMtrFragmentToMtrDetailFragment(mtrId)
                findNavController().navigate(action)
            }
        })

        // Установка обработчика кликов на элемент списка
        mtrAdapter.setOnItemClickListener { mtrWithAbonent ->
            val action = MTRFragmentDirections.actionMtrFragmentToMtrDetailFragment(mtrWithAbonent.mtr.id)
            findNavController().navigate(action)
        }

        // Установка адаптера и layoutManager для RecyclerView
        binding.recyclerView.apply {
            adapter = mtrAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
