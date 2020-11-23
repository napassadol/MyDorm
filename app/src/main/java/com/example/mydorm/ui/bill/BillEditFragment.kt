package com.example.mydorm.ui.bill

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mydorm.*
import kotlinx.android.synthetic.main.bill_edit_fragment.*
import kotlinx.android.synthetic.main.bill_fragment.*
import kotlinx.android.synthetic.main.user_fragment.*
import org.json.JSONObject
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BillEditFragment : Fragment() {

    companion object {
        fun newInstance() = BillEditFragment()
    }

    lateinit var billNo: EditText
    lateinit var price: EditText
    lateinit var dateTxt: EditText
    lateinit var status: EditText
    lateinit var user: EditText
    lateinit var unit: EditText
    private var spinnerTypeData = mutableListOf<String>()
    private var roomId = mutableListOf<String>()
    private var roomNo = mutableListOf<String>()
    private var roomPrice = mutableListOf<String>()
    private var roomUserId = mutableListOf<Int>()

    private var user_first_name = ""
    private var user_last_name = ""
    private var user_tel = ""

    private var typeSelect = ""
    private var roomSelect = ""
    private var userId = 0

    private var elecPriceUnit = 0.0
    private var waterPriceUnit = 0.0
    private var roomPriceUnit = 0.0

    private lateinit var viewModel: BillEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bill_edit_fragment, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(BillEditViewModel::class.java)

        billNo = view!!.findViewById(R.id.billNoEditText)
        price = view!!.findViewById(R.id.billPriceEditText)
        dateTxt = view!!.findViewById(R.id.billDateEditText)
        status = view!!.findViewById(R.id.billStatusEditText)
        user = view!!.findViewById(R.id.billUserEditText)
        unit = view!!.findViewById(R.id.billUnitTextEdit)

        context?.let { this.readRoomOccupine(it) }
        context?.let { this.readUtility(it) }

        billDeleteBtn.setOnClickListener {
            context?.let { it1 -> deleteBill(it1) }
        }

        val adapter = context?.let {
            ArrayAdapter(
                it, // Context
                android.R.layout.simple_spinner_item, // Layout
                spinnerTypeData // Array
            )
        }
        spinnerTypeData.add("Electrical")
        spinnerTypeData.add("Water")
        spinnerTypeData.add("Rental Fee")
        BillTypeSpinner.adapter = adapter

        if (!arguments!!.isEmpty) {
            println(arguments!!.getInt("id").toString())
            println(arguments!!.getString("room_id"))
            println(arguments!!.getString("user_id"))
            println(arguments!!.getString("unit"))
            println(arguments!!.getString("price"))
            println(arguments!!.getString("status"))
            println(arguments!!.getString("date"))
            println(arguments!!.getString("type"))

            val typeVal = arguments!!.getString("type")

            for (x in 0..this.spinnerTypeData.size - 1){
                if(spinnerTypeData[x] == typeVal){
                    BillTypeSpinner.setSelection(x)
                }
            }

            price.setText(arguments!!.getString("price"))
            unit.setText(arguments!!.getString("unit"))
            status.setText(arguments!!.getString("status"))
            dateTxt.setText(arguments!!.getString("date"))

            if (arguments!!.getString("user_id") != ""){
                context?.let { this.getUserById(it, arguments!!.getString("user_id")!!.toInt()) }
            }

            billNo.isEnabled = false
            status.isEnabled = false
            user.isEnabled = false
            price.isEnabled = false

            if (arguments!!.getInt("id") != 0){
                billNo.setText(arguments!!.getInt("id").toString())
                BillTypeSpinner.isEnabled = false
                billRoomSpinner.isEnabled = false

                billSaveBtn.setOnClickListener {
                    context?.let { it1 -> UpdateBill(it1) }
                }
            }
            else{
                status.setText("pending")
                billPaidBtn.isVisible = false
                billDeleteBtn.isVisible = false
                setDateNow()

                billSaveBtn.setOnClickListener {
                    context?.let { it1 -> CreateBill(it1) }
                }
            }

            if (arguments!!.getString("status") == "paid"){
                unit.isEnabled = false
                dateTxt.isEnabled = false
            }
        }

        billPaidBtn.setOnClickListener {
            context?.let { it1 -> paidBill(it1) }
        }

        BillTypeSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                typeSelect = spinnerTypeData[i]
                println("typeSelect: " + typeSelect)
                if(typeSelect == "Rental Fee"){
                    unit.isEnabled = false
                    unit.setText("1")
                    price.setText(roomPriceUnit.toString())
                }
                else{
                    unit.isEnabled = true
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })

        billRoomSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View,
                i: Int,
                l: Long
            ) {
                roomSelect = roomId[i]
                println("roomSelect: " + roomSelect)
                userId = roomUserId[i]
                roomPriceUnit = roomPrice[i].toDouble()
                context?.let { getUserById(it, roomUserId[i]) }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {
                return
            }
        })

        unit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                try {
                    var calPrice = 0.0
                    if(typeSelect == "Water"){
                        calPrice = unit.text.toString().toDouble() * waterPriceUnit
                    }
                    else if(typeSelect == "Electrical"){
                        calPrice = unit.text.toString().toDouble() * elecPriceUnit
                    }
                    price.setText(calPrice.toString())
                }
                catch (e: Exception){
                    println("error :" + e.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        checkAdmin()
    }

    private fun checkAdmin(){
        if(!MainActivity.admin){
            billUnitTextEdit.isEnabled = false
            billSaveBtn.isVisible = false
            billPaidBtn.isVisible = false
            billDeleteBtn.isVisible = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setDateNow(){
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val formatted = current.format(formatter)
        dateTxt.setText(formatted)
    }

    private fun httpRequest(body: JSONObject, url: String, method: Int, context: Context){
        val request = JsonObjectRequest(method,
            MainActivity.url + url, body, Response.Listener { response ->
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
        if(url.equals(MainActivity.read_room_occupine)){
            val result = response.getJSONArray("result")
            for(i in 0..(result.length() - 1)){
                val item = JSONObject(result[i].toString())
                val id = item.getInt("id")
                val no = item.getString("no")
                val user_id = item.getInt("user_id")
                val room_price = item.getString("price")
                roomId.add(id.toString())
                roomNo.add(no)
                roomUserId.add(user_id)
                roomPrice.add(room_price)
            }
            val roomAdapterSpinner = ArrayAdapter(
                    context, // Context
                    android.R.layout.simple_spinner_item, // Layout
                    roomNo // Array
            )

            billRoomSpinner.adapter = roomAdapterSpinner
        }
        else if(url.equals(MainActivity.get_user_by_id)){
            val result = response.getJSONArray("result")
            val item = JSONObject(result[0].toString())
            user_first_name = item.getString("first_name")
            user_last_name = item.getString("last_name")
            user_tel = item.getString("tel")
            user.setText(user_first_name + " " + user_last_name)
        }
        else if(url.equals(MainActivity.create_bill)){
            back(context)
        }
        else if(url.equals(MainActivity.update_bill)){
            back(context)
        }
        else if(url.equals(MainActivity.delete_bill)){
            back(context)
        }
        else if(url.equals(MainActivity.paid_bill)){
            back(context)
        }
        else if(url.equals(MainActivity.read_utility)){
            val result = response.getJSONArray("result")
            val item = JSONObject(result[0].toString())
            this.elecPriceUnit = item.getString("electrical").toDouble()
            this.waterPriceUnit = item.getString("water").toDouble()
        }
    }

    private fun readUtility(context: Context){
        this.httpRequest(JSONObject(), MainActivity.read_utility, Request.Method.GET , context)
    }

    private fun readRoomOccupine(context: Context){
        this.httpRequest(JSONObject(), MainActivity.read_room_occupine, Request.Method.GET , context)
    }

    private fun getUserById(context: Context, id: Int){
        var payload = JSONObject()
        payload.put("id", id)
        this.httpRequest(payload, MainActivity.get_user_by_id, Request.Method.POST , context)
    }

    private fun UpdateBill(context: Context){
        var payload = JSONObject()
        payload.put("id", billNo.text)
        payload.put("type", typeSelect)
        payload.put("price", price.text)
        payload.put("date", dateTxt.text)
        payload.put("unit", unit.text)
        this.httpRequest(payload, MainActivity.update_bill, Request.Method.POST , context)
    }

    private fun CreateBill(context: Context){
        var payload = JSONObject()
        payload.put("room_id", roomSelect)
        payload.put("user_id", userId)
        payload.put("type", typeSelect)
        payload.put("price", price.text)
        payload.put("date", dateTxt.text)
        payload.put("unit", unit.text)
        payload.put("status", "pending")
        this.httpRequest(payload, MainActivity.create_bill, Request.Method.POST , context)
    }

    private fun paidBill(context: Context){
        var payload = JSONObject()
        payload.put("id", billNo.text)
        this.httpRequest(payload, MainActivity.paid_bill, Request.Method.POST , context)
    }


    private fun deleteBill(context: Context){
        var payload = JSONObject()
        payload.put("id", billNo.text)
        this.httpRequest(payload, MainActivity.delete_bill, Request.Method.POST , context)
    }

    private fun back(context: Context){
        val activity = context as AppCompatActivity
        val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
        navController.navigateUp()
    }


}