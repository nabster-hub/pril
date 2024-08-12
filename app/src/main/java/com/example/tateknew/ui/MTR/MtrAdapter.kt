package com.example.tateknew.ui.MTR
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tateknew.R
import com.example.tateknew.data.MtrEntity
import com.example.tateknew.data.MtrWithAbonent
import com.example.tateknew.databinding.ItemMtrBinding
import com.example.tateknew.ui.getData.OnAbonentClickListener


class MtrAdapter(private val mtrs: MutableList<MtrEntity>, private val listener: OnAbonentClickListener) :
    RecyclerView.Adapter<MtrAdapter.MtrViewHolder>() {

    private var onItemClickListener: ((MtrEntity) -> Unit)? = null

    fun setOnItemClickListener(listener: (MtrEntity) -> Unit) {
        onItemClickListener = listener
    }
    fun updateData(newMtrs: List<MtrEntity>) {
        mtrs.clear()
        mtrs.addAll(newMtrs)
        notifyDataSetChanged()
    }

    inner class MtrViewHolder(val binding: ItemMtrBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val mtr = mtrs[adapterPosition]
                listener.onAbonentClick(mtr.id)
                onItemClickListener?.invoke(mtr)
            }
        }
        fun bind(item: MtrEntity) {
            binding.name.text = "Тип: ${item.name}, №: ${item.itemNo}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtrViewHolder {
        val binding = ItemMtrBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MtrViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MtrViewHolder, position: Int) {
        holder.bind(mtrs[position])
    }

    override fun getItemCount() = mtrs.size
}
