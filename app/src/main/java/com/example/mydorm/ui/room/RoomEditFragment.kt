package com.example.mydorm.ui.room

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mydorm.MainActivity
import com.example.mydorm.R
import com.example.mydorm.VolleySingleton
import kotlinx.android.synthetic.main.room_edit_fragment.*
import kotlinx.android.synthetic.main.user_edit_fragment.*
import org.json.JSONObject


class RoomEditFragment : Fragment() {

    companion object {
        fun newInstance() = RoomEditFragment()
    }

    lateinit var roomNoText: EditText
    lateinit var roomPriceText: EditText
    private var roomId: Int = 0
    private var userId: Int = 0
    private var roomStatus: String = ""
    private var roomPrice = ""

    lateinit var sp: Spinner

    private lateinit var viewModel: RoomEditViewModel
    private var userIdList = mutableListOf<Int>()
    private var userFirstnameList = mutableListOf<String>()
    private var userLastnameList = mutableListOf<String>()
    private var userTelList = mutableListOf<String>()
    private var spinnerData = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.room_edit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RoomEditViewModel::class.java)

        this.roomNoText = view!!.findViewById(R.id.RoomNoEditText)
        this.sp = view!!.findViewById(R.id.UserSpinner)
        this.roomNoText.isEnabled = false
        this.roomPriceText = view!!.findViewById(R.id.roomPriceTextEdit)

        if (!arguments!!.isEmpty) {
            println(arguments!!.getInt("id").toString())
            println(arguments!!.getString("no"))
            println(arguments!!.getString("status"))
            println(arguments!!.getString("price"))
            println(arguments!!.getInt("user_id").toString())

            this.roomNoText.setText(arguments!!.getString("no"))
            this.roomPriceText.setText(arguments!!.getString("price"))
            this.roomStatus = arguments!!.getString("status").toString()
            this.roomId = arguments!!.getInt("id")
            this.userId = arguments!!.getInt("user_id")
        }

        view?.context?.let { this.bindUi(it) }

        roomSaveBtn.setOnClickListener {
            view?.context?.let { this.updateRoom(it) }
        }

        this.sp.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                println(i.toString())
                if (userIdList.size > 0 && i <= (userIdList.size -1) ) {
                    println(userIdList[i].toString())
                    userId = userIdList[i]
                }
                else if (i == userIdList.size){
                    userId = -1
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })

        checkAdmin()

    }

    private fun checkAdmin(){
        if(!MainActivity.admin){
            roomPriceTextEdit.isEnabled = false
            UserSpinner.isEnabled = false
            roomSaveBtn.isVisible = false
        }
    }

    private fun httpRequest(body: JSONObject, url: String, method: Int, context: Context){
        val request = JsonObjectRequest(method,MainActivity.url + url, body, Response.Listener { response ->
            try {
                Log.i("httpRequest", "$response")
                this.updateUi(url, response, context)
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

    private fun updateUi(url: String, response: JSONObject, context: Context) {
        Log.i("updateUi", url)
        if(url.equals(MainActivity.read_user)){
            val result = response.getJSONArray("result")
            for(i in 0..(result.length() - 1)){
                val item = JSONObject(result[i].toString())
                val id = item.getInt("id")
                val firstname = item.getString("first_name")
                val lastname = item.getString("last_name")
                val tel = item.getString("tel")
                addUser(firstname, lastname, tel, id)
            }
            val adapter = ArrayAdapter(
                context, // Context
                android.R.layout.simple_spinner_item, // Layout
                spinnerData // Array
            )
            spinnerData.add("")
            this.sp.adapter = adapter

            if (this.userId == -1){
                println("set blank user")
                this.sp.setSelection(this.sp.adapter.count - 1)
            }
            else{
                for (x in 1..this.userIdList.size){
                    if(this.userIdList[x - 1] == this.userId){
                        this.sp.setSelection(x - 1)
                    }
                }
            }
        }
        else if (url.equals(MainActivity.update_room)){
            this.back(context)
        }
    }


    private fun addUser(firstname: String, lastname: String, tel: String, id: Int){
        spinnerData.add(firstname + " " + lastname)
        userFirstnameList.add(firstname)
        userLastnameList.add(lastname)
        userTelList.add(tel)
        userIdList.add(id)
    }

    private fun bindUi(context: Context){
        this.httpRequest(JSONObject(), MainActivity.read_user, Request.Method.GET , context)
    }

    private fun updateRoom(context: Context){
        var payload = JSONObject()
        payload.put("id", this.roomId)
        payload.put("no", this.roomNoText.text)
        payload.put("status", this.roomStatus)
        payload.put("user_id", this.userId)
        payload.put("price", this.roomPriceText.text)
        this.httpRequest(payload, MainActivity.update_room, Request.Method.POST , context)
    }

    private fun back(context: Context){
        val activity = context as AppCompatActivity
        val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
        navController.navigateUp()
    }

}