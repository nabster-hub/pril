package com.example.tateknew.ui.MTR
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tateknew.R
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.ItemMtrBinding
import com.example.tateknew.ui.getData.OnAbonentClickListener


class MtrAdapter(private val mtrs: List<MtrWithAbonent>, private val listener: OnAbonentClickListener) :
    RecyclerView.Adapter<MtrAdapter.MtrViewHolder>() {

    inner class MtrViewHolder(val binding: ItemMtrBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val mtr = mtrs[adapterPosition]
                listener.onAbonentClick(mtr.mtr.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtrViewHolder {
        val binding = ItemMtrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MtrViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MtrViewHolder, position: Int) {
        val mtrWithAbonent = mtrs[position]
        println(mtrWithAbonent.mtr.id)
        // Bind data to your views here
        holder.binding.name.text = "Наименование МТР: ${mtrWithAbonent.mtr.name}, номер: ${mtrWithAbonent.mtr.itemNo}"
    }

    override fun getItemCount() = mtrs.size
}
