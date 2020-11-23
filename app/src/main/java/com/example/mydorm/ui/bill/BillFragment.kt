package com.example.mydorm.ui.bill

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mydorm.MainActivity
import com.example.mydorm.R
import com.example.mydorm.RecyclerBillAdapter
import com.example.mydorm.VolleySingleton
import kotlinx.android.synthetic.main.bill_edit_fragment.*
import kotlinx.android.synthetic.main.bill_fragment.*
import org.json.JSONObject
import java.lang.Exception


class BillFragment : Fragment() {

    companion object {
        fun newInstance() = BillFragment()
    }

    private var idList = mutableListOf<Int>()
    private var typeList = mutableListOf<String>()
    private var roomList = mutableListOf<String>()
    private var userList = mutableListOf<String>()
    private var priceList = mutableListOf<String>()
    private var unitList = mutableListOf<String>()
    private var statusList = mutableListOf<String>()
    private var dateList = mutableListOf<String>()
    private var imageList = mutableListOf<Int>()

    private var billStatusSearch = "all"

    private var spinnerStatusData = mutableListOf<String>()

    private lateinit var viewModel: BillViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bill_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BillViewModel::class.java)

        this.clearData()

//        context?.let { this.getBill(it) }

        val adapter = context?.let {
            ArrayAdapter(
                it, // Context
                android.R.layout.simple_spinner_item, // Layout
                spinnerStatusData // Array
            )
        }
        spinnerStatusData.clear()
        spinnerStatusData.add("all")
        spinnerStatusData.add("pending")
        spinnerStatusData.add("paid")
        BillSearchStatusSpinner.adapter = adapter

        BillSearchStatusSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                billStatusSearch = spinnerStatusData[i]
                context?.let { getBillByRoomNo(it) }

            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })


        addBillBtn.setOnClickListener {
            val activity = view?.context as AppCompatActivity
            this.addNewBill(activity)
        }

        searchBillBtn.setOnClickListener {
            context?.let { it1 -> this.getBillByRoomNo(it1) }
        }

        checkAdmin()
    }

    private fun checkAdmin(){
        if(!MainActivity.admin){
            addBillBtn.isVisible = false
        }
    }

    private fun clearData(){
        idList.clear()
        typeList.clear()
        roomList.clear()
        userList.clear()
        priceList.clear()
        statusList.clear()
        dateList.clear()
        imageList.clear()
        unitList.clear()
    }

    private fun addNewBill(activity: AppCompatActivity){
        val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
        val bundle = Bundle()
        bundle.putInt("id", 0)
        bundle.putString("room_id", "")
        bundle.putString("user_id", "")
        bundle.putString("price", "")
        bundle.putString("status", "")
        bundle.putString("date", "")
        bundle.putString("type", "")
        bundle.putString("unit", "")
        navController.navigate(R.id.nav_bill_edit, bundle)
    }

    private fun httpRequest(body: JSONObject, url: String, method: Int, context: Context){
        val request = JsonObjectRequest(method,
            MainActivity.url + url, body, Response.Listener { response ->
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
        if(url.equals(MainActivity.list_bill) || url.equals(MainActivity.list_bill_by_room_no)){
            this.clearData()
            val result = response.getJSONArray("result")
            for(i in 0..(result.length() - 1)){
                val item = JSONObject(result[i].toString())
                val id = item.getInt("id")
                val type = item.getString("type")
                println("type " + type)
                val room = item.getString("room_id")
                println("room_id " + room)
                val user = item.getString("user_id")
                println("user " + user)
                val unit = item.getString("unit")
                println("unit " + unit)
                val price = item.getString("price")
                println("price " + price)
                val status = item.getString("status")
                println("status " + status)
                val date = item.getString("date")
                println("date " + date)
                this.addBill(id, type, room, user, price, unit, status, date, R.mipmap.ic_launcher_round)
            }
            rv_bill.layoutManager = LinearLayoutManager(activity)
            rv_bill.adapter = RecyclerBillAdapter(idList, typeList, roomList, userList, priceList, unitList, statusList, dateList, imageList)
        }
    }

    private fun addBill(id: Int, type: String, room: String, user: String, price: String, unit: String, status: String, date: String, image: Int){
        typeList.add(type)
        roomList.add(room)
        userList.add(user)
        priceList.add(price)
        statusList.add(status)
        dateList.add(date)
        imageList.add(image)
        idList.add(id)
        unitList.add(unit)
    }

    private fun getBill(context: Context){
        this.httpRequest(JSONObject(), MainActivity.list_bill, Request.Method.GET , context)
    }

    private fun getBillByRoomNo(context: Context){
        var payload = JSONObject()
        payload.put("no", searchRoomEditText.text)
        payload.put("status", billStatusSearch)
        this.httpRequest(payload, MainActivity.list_bill_by_room_no, Request.Method.POST , context)

    }

}
