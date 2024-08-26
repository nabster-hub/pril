package com.example.tateknew.ui.MTR

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tateknew.data.MtrEntity
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.FragmentMtrBinding
import com.example.tateknew.ui.Abonents.AbonentsViewModel
import com.example.tateknew.ui.getData.OnAbonentClickListener

class MTRFragment : Fragment() {

    private var abonentId: Long = 0
    private var nobjId: Int = 0
    private lateinit var binding: FragmentMtrBinding
    private lateinit var mtrAdapter: MtrAdapter
    private val mtrList: List<MtrEntity> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            abonentId = it.getLong("abonentId")
            nobjId = it.getInt("nobjId")
        }
        println("mtr fragment ${abonentId.toString()}")
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

        // Загрузка списка MTR для abonentId
        val mtrsViewModel  = ViewModelProvider(this).get(MtrsViewModel::class.java)
        mtrsViewModel.loadMtrs(abonentId)
            mtrAdapter = MtrAdapter(mutableListOf(), object : OnAbonentClickListener {
                override fun onAbonentClick(abonentId: Long) {
                    val action = MTRFragmentDirections.actionMtrFragmentToMtrDetailFragment(abonentId)
                    findNavController().navigate(action)
                }
            })
        binding.recyclerView.apply {
            adapter = mtrAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Наблюдение за списком MTR и обновление адаптера при изменении данных
        mtrsViewModel.mtrs.observe(viewLifecycleOwner) { mtrs ->
            mtrAdapter.updateData(mtrs)
        }
    }

}
