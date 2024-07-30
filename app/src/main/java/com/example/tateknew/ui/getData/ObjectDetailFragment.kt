package com.example.tateknew.ui.getData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.FragmentObjectDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ObjectDetailFragment : Fragment(), OnAbonentClickListener {

    private var objectId: Int = 0
    private lateinit var binding: FragmentObjectDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            objectId = it.getInt("objectId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentObjectDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val mtrsWithAbonents = withContext(Dispatchers.IO) {
                db.mtrDao().getAbonents(objectId)
            }

            val adapter = MtrAdapter(mtrsWithAbonents, this@ObjectDetailFragment)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onAbonentClick(abonentId: Long) {
        val action = ObjectDetailFragmentDirections.actionObjectDetailFragmentToMtrDetailFragment(abonentId)
        findNavController().navigate(action)
//        val db = AppDatabase.getDatabase(requireContext())
//
//        lifecycleScope.launch {
//            val mtrs = withContext(Dispatchers.IO) {
//                db.mtrDao().getMtrsByAbonentId(abonentId)
//            }
//
//            val mtrItems = withContext(Dispatchers.IO) {
//                mtrs.map { entity ->
//                    val abonent = db.abonentDao().getAbonentById(entity.abonentId)
//                    MtrWithAbonent(
//                        mtr = entity,
//                        abonent = abonent
//                    )
//                }
//            }
//
//            val adapter = MtrAdapter(mtrItems, this@ObjectDetailFragment)
//            binding.recyclerView.adapter = adapter
//        }
    }
}
