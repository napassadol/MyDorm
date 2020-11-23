package com.example.mydorm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.mydorm.ui.user.UserEditFragment
import kotlinx.android.synthetic.main.activity_main.view.*

class RecyclerBillAdapter (private var ids: List<Int>, private var type: List<String>, private var roomIds: List<String>, private var userIds: List<String>, private var prices: List<String>, private var unit: List<String>, private var status: List<String>, private var date: List<String>, private var images: List<Int>): RecyclerView.Adapter<RecyclerBillAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemBillId: TextView = itemView.findViewById(R.id.bill_id)
        val itemRoomNo: TextView = itemView.findViewById(R.id.bill_room_no)
        val itemPicture: ImageView = itemView.findViewById(R.id.bill_image)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val activity = v.context as AppCompatActivity
                val navController = findNavController(activity, R.id.nav_host_fragment)

                val bundle = Bundle()
                bundle.putInt("id", ids[position])
                bundle.putString("room_id", roomIds[position])
                bundle.putString("user_id", userIds[position])
                bundle.putString("price", prices[position])
                bundle.putString("unit", unit[position])
                bundle.putString("type", type[position])
                bundle.putString("status", status[position])
                bundle.putString("date", date[position])
                navController.navigate(R.id.nav_bill_edit, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bill_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ids.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemBillId.text = "ID: " + ids[position].toString()
        holder.itemRoomNo.text = "Room No:" + roomIds[position] + ", " + type[position] + ", " + prices[position]
        holder.itemPicture.setImageResource(images[position])
    }

}