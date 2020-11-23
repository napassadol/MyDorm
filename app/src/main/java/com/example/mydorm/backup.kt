package com.example.mydorm

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class backup(private var ids: List<Int>, private var firstnames: List<String>, private var lastnames: List<String>, private var tels: List<String>, private var images: List<Int>): RecyclerView.Adapter<backup.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemFullName: TextView = itemView.findViewById(R.id.fullname)
        val itemTel: TextView = itemView.findViewById(R.id.tel)
        val itemPicture: ImageView = itemView.findViewById(R.id.user_image)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                Toast.makeText(itemView.context, "item : ${position}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ids.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemFullName.text = firstnames[position] + " " + lastnames[position]
        holder.itemTel.text = tels[position]
        holder.itemPicture.setImageResource(images[position])
    }
}