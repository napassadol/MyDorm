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

class RecyclerUserAdapter (private var ids: List<Int>, private var firstnames: List<String>, private var lastnames: List<String>, private var tels: List<String>, private var images: List<Int>): RecyclerView.Adapter<RecyclerUserAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemFullName: TextView = itemView.findViewById(R.id.fullname)
        val itemTel: TextView = itemView.findViewById(R.id.tel)
        val itemPicture: ImageView = itemView.findViewById(R.id.user_image)

        init {
            itemView.setOnClickListener { v: View ->
                val position: Int = adapterPosition
                val activity = v.context as AppCompatActivity
                val navController = findNavController(activity, R.id.nav_host_fragment)

                val bundle = Bundle()
                bundle.putInt("id", ids[position])
                bundle.putString("firstname", firstnames[position])
                bundle.putString("lastname", lastnames[position])
                bundle.putString("tel", tels[position])
                navController.navigate(R.id.nav_user_edit, bundle)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return ids.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemFullName.text = "Name: " + firstnames[position] + " " + lastnames[position]
        holder.itemTel.text = "Tel: " + tels[position]
        holder.itemPicture.setImageResource(images[position])
    }

}