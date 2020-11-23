package com.example.mydorm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView

class RecyclerRoomAdapter(private var ids: List<Int>, private var nos: List<String>, private var status: List<String>, private var userIds: List<Int?>, private var price: List<String>, private var images: List<Int>): RecyclerView.Adapter<RecyclerRoomAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemNo: TextView = itemView.findViewById(R.id.room_no)
        val itemStatus: TextView = itemView.findViewById(R.id.room_des)
        val itemPicture: ImageView = itemView.findViewById(R.id.iv_image)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val activity = v.context as AppCompatActivity
                val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)

                val bundle = Bundle()
                var user_id = -1
                if(userIds[position] != null){
                    user_id = userIds[position]!!
                }
                bundle.putInt("id", ids[position])
                bundle.putInt("user_id", user_id)
                bundle.putString("no", nos[position])
                bundle.putString("status", status[position])
                bundle.putString("price", price[position])
                navController.navigate(R.id.nav_room_edit, bundle)
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
        holder.itemNo.text = "Room No: " + nos[position]
        holder.itemStatus.text = "Status: " + status[position]
        holder.itemPicture.setImageResource(images[position])
    }
}