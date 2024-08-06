package com.example.tateknew.ui.Tps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tateknew.R
import com.example.tateknew.data.TpsEntity

class TPsAdapter(
    private var tps: List<TpsEntity>,
    private val onItemClick: (TpsEntity) -> Unit
) : RecyclerView.Adapter<TPsAdapter.ObjectViewHolder>() {

    class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tpsName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tps, parent, false)
        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        val item = tps[position]
        holder.nameTextView.text = item.name
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = tps.size

    fun updateData(newObjects: List<TpsEntity>) {
        tps = newObjects
        notifyDataSetChanged()
    }
}
