package com.example.tateknew.ui.Abonents

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.ItemAbonentsBinding
import com.example.tateknew.ui.getData.OnAbonentClickListener

class AbonentAdapter(
    private var mtrItems: List<MtrWithAbonent>,
    private val clickListener: OnAbonentClickListener
) : RecyclerView.Adapter<AbonentAdapter.AbonentViewHolder>() {

    inner class AbonentViewHolder(val binding: ItemAbonentsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MtrWithAbonent) {
            binding.name.text = "Лицевой №:  ${item.abonent.clientNo}, Адрес: ${item.abonent.address} "
            binding.root.setOnClickListener {
                clickListener.onAbonentClick(item.abonent.clientId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbonentViewHolder {
        val binding = ItemAbonentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AbonentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AbonentViewHolder, position: Int) {
        holder.bind(mtrItems[position])
    }

    override fun getItemCount() = mtrItems.size

    fun updateData(newItems: List<MtrWithAbonent>) {
        mtrItems = newItems
        notifyDataSetChanged()
    }
}
