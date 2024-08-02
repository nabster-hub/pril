package com.example.tateknew.ui.Abonents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.FragmentAbonentsDetailBinding
import com.example.tateknew.ui.getData.MtrAdapter
import com.example.tateknew.ui.getData.OnAbonentClickListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AbonentsFragment : Fragment(), OnAbonentClickListener {

    private var objectId: Int = 0
    private lateinit var binding: FragmentAbonentsDetailBinding
    private lateinit var abonentsViewModel: AbonentsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            objectId = it.getInt("objectId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAbonentsDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        abonentsViewModel = ViewModelProvider(this).get(AbonentsViewModel::class.java)
        abonentsViewModel.loadAbonents(objectId)

        abonentsViewModel.abonents.observe(viewLifecycleOwner) { abonents ->
            val adapter = MtrAdapter(abonents, this@AbonentsFragment)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    override fun onAbonentClick(abonentId: Long) {
        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val mtrs = withContext(Dispatchers.IO) {
                db.mtrDao().getMtrsByAbonentId(abonentId)
            }

            val mtrItems = withContext(Dispatchers.IO) {
                mtrs.map { entity ->
                    val abonent = db.abonentDao().getAbonentById(entity.abonentId)
                    MtrWithAbonent(
                        mtr = entity,
                        abonent = abonent
                    )
                }
            }

            val adapter = MtrAdapter(mtrItems, this@AbonentsFragment)
            binding.recyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
    }
}
