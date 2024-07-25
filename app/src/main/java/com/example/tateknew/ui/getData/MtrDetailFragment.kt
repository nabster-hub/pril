// Файл: MtrDetailFragment.kt
package com.example.tateknew.ui.getData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.MtrEntity
import com.example.tateknew.databinding.FragmentMtrDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MtrDetailFragment : Fragment() {

    private var abonentId: Long = 0
    private lateinit var binding: FragmentMtrDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            abonentId = it.getLong("abonentId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMtrDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())

        lifecycleScope.launch {
            val mtrs = withContext(Dispatchers.IO) {
                db.mtrDao().getMtrsByAbonentId(abonentId)
            }

            val adapter = MtrEntityAdapter(mtrs)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(abonentId: Long) =
            MtrDetailFragment().apply {
                arguments = Bundle().apply {
                    putLong("abonentId", abonentId)
                }
            }
    }
}
