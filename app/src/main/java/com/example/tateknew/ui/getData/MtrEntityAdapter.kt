// Файл: MtrEntityAdapter.kt
package com.example.tateknew.ui.getData

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tateknew.data.MtrEntity
import com.example.tateknew.databinding.ItemMtrBinding

class MtrEntityAdapter(private val mtrItems: List<MtrEntity>) : RecyclerView.Adapter<MtrEntityAdapter.MtrEntityViewHolder>() {

    inner class MtrEntityViewHolder(val binding: ItemMtrBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MtrEntity) {
            binding.name.text = item.name
            // Другие поля можно заполнить здесь
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtrEntityViewHolder {
        val binding = ItemMtrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MtrEntityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MtrEntityViewHolder, position: Int) {
        holder.bind(mtrItems[position])
    }

    override fun getItemCount() = mtrItems.size
}
