package com.example.tateknew.ui.getData

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tateknew.R
import com.example.tateknew.data.ObjectEntity

class ObjectAdapter(
    private var objects: List<ObjectEntity>,
    private val onItemClick: (ObjectEntity) -> Unit
) : RecyclerView.Adapter<ObjectAdapter.ObjectViewHolder>() {

    class ObjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.objectName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObjectViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_object, parent, false)
        return ObjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ObjectViewHolder, position: Int) {
        val item = objects[position]
        holder.nameTextView.text = item.name
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = objects.size

    fun updateData(newObjects: List<ObjectEntity>) {
        objects = newObjects
        notifyDataSetChanged()
    }
}
