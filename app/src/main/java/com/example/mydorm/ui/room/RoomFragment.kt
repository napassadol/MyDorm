package com.example.mydorm.ui.room

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mydorm.MainActivity
import com.example.mydorm.R
import com.example.mydorm.RecyclerRoomAdapter
import com.example.mydorm.VolleySingleton
import kotlinx.android.synthetic.main.room_fragment.*
import org.json.JSONObject
import java.lang.Exception


class RoomFragment : Fragment() {

    companion object {
        fun newInstance() = RoomFragment()
    }

    private lateinit var viewModel: RoomViewModel

    private var idList = mutableListOf<Int>()
    private var userIdList = mutableListOf<Int?>()
    private var noList = mutableListOf<String>()
    private var statusList = mutableListOf<String>()
    private var priceList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.room_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RoomViewModel::class.java)

        activity?.let { postToList(it) }

        button2.setOnClickListener {
            context?.let { it1 -> getRoomByNo(it1) }
        }
    }

    private fun httpRequest(body: JSONObject, url: String, method: Int, context: Context){
        val request = JsonObjectRequest(method,MainActivity.url + url, body, Response.Listener { response ->
            try {
                Log.i("httpRequest", "$response")
                this.updateUi(url, response)
            }catch (e: Exception){
                Log.e("httpRequest", "$e")
            }

        }, Response.ErrorListener{
            Log.e("httpRequest", "$it")
        }
        )
        request.retryPolicy = DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 1f)
        VolleySingleton.getInstance(context).addToRequestQueue(request)
    }

    private fun updateUi(url: String, response: JSONObject) {
        Log.i("updateUi", url)
        if(url.equals(MainActivity.read_room) || url.equals(MainActivity.get_room_by_no)){
            val result = response.getJSONArray("result")
            idList.clear()
            noList.clear()
            statusList.clear()
            imageList.clear()
            userIdList.clear()
            priceList.clear()
            for(i in 0..(result.length() - 1)){
                val item = JSONObject(result[i].toString())
                val no = item.getString("no")
                val status = item.getString("status")
                val price = item.getString("price")
                val id = item.getInt("id")
                var userId : Int?
                try {
                    userId = item.getInt("user_id")
                } catch (error: Exception){
                    userId = null
                }
                addRoom(id, no, status, userId, price, R.mipmap.ic_launcher_round)
            }
            rv_recyclerView.layoutManager = LinearLayoutManager(activity)
            rv_recyclerView.adapter = RecyclerRoomAdapter(idList, noList, statusList, userIdList, priceList, imageList)
        }
    }

    private fun addRoom(id: Int, no: String, status: String, userId: Int?, price: String, image: Int){
        idList.add(id)
        noList.add(no)
        statusList.add(status)
        imageList.add(image)
        priceList.add(price)
        userIdList.add(userId)
    }

    private fun postToList(context: Context){
        this.httpRequest(JSONObject(), MainActivity.read_room, Request.Method.GET , context)
    }

    private fun getRoomByNo(context: Context){
        var payload = JSONObject()
        payload.put("no", editTextTextPersonName2.text)
        println(editTextTextPersonName2.text)
        this.httpRequest(payload, MainActivity.get_room_by_no, Request.Method.POST , context)
    }

}
