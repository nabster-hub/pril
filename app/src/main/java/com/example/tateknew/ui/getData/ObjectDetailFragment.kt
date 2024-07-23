package com.example.tateknew.ui.getData

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tateknew.data.AppDatabase
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.FragmentObjectDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ObjectDetailFragment : Fragment() {

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
            val mtrs = withContext(Dispatchers.IO) {
                //db.mtrDao().getMtrsByObjectId(objectId)
                db.mtrDao().getAbonents(objectId)
            }

            val mtrItems = mtrs.map { entity ->
                MtrWithAbonent(
                    id = entity.id,
                    abonentId = entity.abonentId,
                    nobjId = entity.nobjId,
                    baseId = entity.baseId,
                    name = entity.name,
                    puName = entity.puName,
                    itemNo = entity.itemNo,
                    status = entity.status,
                    ecapId = entity.ecapId,
                    sredrashod = entity.sredrashod,
                    vl = entity.vl,
                    ktt = entity.ktt,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    clientId = entity.clientId,
                    ctt = entity.ctt,
                    ct = entity.ct,
                    abonentName = entity.abonentName,
                    clientNo = entity.clientNo,
                    address = entity.address,
                    abonentBaseId = entity.abonentBaseId,
                    clientGr = entity.clientGr,
                    street = entity.street,
                    home = entity.home,
                    flat = entity.flat,
                    abonentCreatedAt = entity.abonentCreatedAt,
                    abonentUpdatedAt = entity.abonentUpdatedAt,
                )
            }

            val adapter = MtrAdapter(mtrItems)
            binding.recyclerView.adapter = adapter
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        }
    }
}
