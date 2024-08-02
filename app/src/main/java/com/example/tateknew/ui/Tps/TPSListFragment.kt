package com.example.tateknew.ui.Tps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tateknew.data.TpsEntity
import com.example.tateknew.databinding.FragmentObjectListBinding

class TPSListFragment : Fragment() {

    private var _binding: FragmentObjectListBinding? = null
    private val binding get() = _binding!!

    private lateinit var tpsAdapter: TPsAdapter
    private lateinit var tpsViewModel: TPsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentObjectListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tpsAdapter = TPsAdapter(emptyList()) { objectItem ->
            val action = TPSListFragmentDirections.actionTPSListFragmentToAbonentsFragment(objectItem.id)
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = tpsAdapter
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel and observe data in onViewCreated
        tpsViewModel = ViewModelProvider(this).get(TPsViewModel::class.java)

        tpsViewModel.allObjects.observe(viewLifecycleOwner, Observer { objects ->
            objects?.let { updateUI(it) }
        })
    }

    private fun updateUI(objects: List<TpsEntity>) {
        tpsAdapter.updateData(objects)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
