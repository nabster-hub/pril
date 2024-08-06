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

    private var onItemClickListener: ((MtrWithAbonent) -> Unit)? = null

    fun setOnItemClickListener(listener: (MtrWithAbonent) -> Unit) {
        onItemClickListener = listener
    }

    inner class MtrViewHolder(val binding: ItemMtrBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val mtr = mtrs[adapterPosition]
                listener.onAbonentClick(mtr.mtr.id)
                onItemClickListener?.invoke(mtr)
            }
        }
        fun bind(item: MtrWithAbonent) {
            binding.name.text = "Наименование МТР: ${item.mtr.name}, номер: ${item.mtr.itemNo}"
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
