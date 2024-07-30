package com.example.tateknew.ui.getData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.databinding.FragmentMtrDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MtrDetailFragment : Fragment() {

    private val args: MtrDetailFragmentArgs by navArgs()
    private lateinit var binding: FragmentMtrDetailBinding
    private lateinit var viewModel: MtrDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMtrDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(MtrDetailViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val abonentId = args.abonentId

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val mtrs = withContext(Dispatchers.IO) {
                db.mtrDao().getMtrsByAbonentId(abonentId)
            }

            // Обновите viewModel данными из базы данных
            viewModel.updateMtrs(mtrs)
        }
    }
}
