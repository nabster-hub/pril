package com.example.tateknew.ui.getData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tateknew.data.ObjectEntity
import com.example.tateknew.databinding.FragmentObjectListBinding

class ObjectListFragment : Fragment() {

    private var _binding: FragmentObjectListBinding? = null
    private val binding get() = _binding!!

    private lateinit var objectAdapter: ObjectAdapter
    private lateinit var objectListViewModel: ObjectListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentObjectListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        objectAdapter = ObjectAdapter(emptyList()) { objectItem ->
            val action = ObjectListFragmentDirections.actionObjectListFragmentToObjectDetailFragment(objectItem.id)
            findNavController().navigate(action)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = objectAdapter
        }

        objectListViewModel = ViewModelProvider(this).get(ObjectListViewModel::class.java)

        objectListViewModel.allObjects.observe(viewLifecycleOwner, Observer { objects ->
            objects?.let { updateUI(it) }
        })

        return root
    }

    private fun updateUI(objects: List<ObjectEntity>) {
        objectAdapter.updateData(objects)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
