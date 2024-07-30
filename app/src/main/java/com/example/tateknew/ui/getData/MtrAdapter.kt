package com.example.tateknew.ui.getData

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.ItemMtrBinding

class MtrAdapter(
    private val mtrItems: List<MtrWithAbonent>,
    private val clickListener: OnAbonentClickListener
) : RecyclerView.Adapter<MtrAdapter.MtrViewHolder>() {

    inner class MtrViewHolder(val binding: ItemMtrBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MtrWithAbonent) {
            binding.name.text = "Лицевой №:  ${item.abonent.clientNo}, Адрес: ${item.abonent.address} "
            binding.root.setOnClickListener {
                clickListener.onAbonentClick(item.abonent.clientId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtrViewHolder {
        val binding = ItemMtrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MtrViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MtrViewHolder, position: Int) {
        holder.bind(mtrItems[position])
    }

    override fun getItemCount() = mtrItems.size
}
