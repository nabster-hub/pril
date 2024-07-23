package com.example.tateknew.ui.getData

import com.example.tateknew.data.MtrWithAbonent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tateknew.R

class MtrAdapter(
    private val mtrs: List<MtrWithAbonent>
) : RecyclerView.Adapter<MtrAdapter.MtrViewHolder>() {

    class MtrViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.mtrName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MtrViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mtr, parent, false)
        return MtrViewHolder(view)
    }

    override fun onBindViewHolder(holder: MtrViewHolder, position: Int) {
        val item = mtrs[position]
        holder.nameTextView.text = item.address
    }

    override fun getItemCount() = mtrs.size
}
