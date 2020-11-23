package com.example.mydorm.ui.facility

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
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
import com.example.mydorm.RecyclerRoomAdapter
import com.example.mydorm.VolleySingleton
import kotlinx.android.synthetic.main.bill_edit_fragment.*
import kotlinx.android.synthetic.main.facility_fragment.*
import kotlinx.android.synthetic.main.room_fragment.*
import org.json.JSONObject
import java.lang.Exception

class FacilityFragment : Fragment() {

    lateinit var waterText: EditText
    lateinit var elecText: EditText

    companion object {
        fun newInstance() = FacilityFragment()
    }

    private lateinit var viewModel: FacilityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.facility_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FacilityViewModel::class.java)

        this.elecText = view!!.findViewById(R.id.facilityElecEditText)
        this.waterText = view!!.findViewById(R.id.facilityWaterEditText)

        context?.let { this.readUtility(it) }

        facilitySaveBtn.setOnClickListener {
            context?.let { it1 -> this.updateUtility(it1) }
        }
        checkAdmin()
    }

    private fun checkAdmin(){
        if(!MainActivity.admin){
            facilityElecEditText.isEnabled = false
            facilityWaterEditText.isEnabled = false
            facilitySaveBtn.isVisible = false
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
        if(url.equals(MainActivity.read_utility)){
            val result = response.getJSONArray("result")
            val item = JSONObject(result[0].toString())
            val waterPrice = item.getString("water")
            val elecPrice = item.getString("electrical")
            this.elecText.setText(elecPrice)
            this.waterText.setText(waterPrice)
        }
        else if(url.equals(MainActivity.update_utility)){
            back(context)
        }
    }

    private fun readUtility(context: Context){
        this.httpRequest(JSONObject(), MainActivity.read_utility, Request.Method.GET , context)
    }

    private fun updateUtility(context: Context){
        var payload = JSONObject()
        payload.put("id", 1)
        payload.put("water", this.facilityWaterEditText.text)
        payload.put("elec", this.facilityElecEditText.text)
        this.httpRequest(payload, MainActivity.update_utility, Request.Method.POST , context)
    }

    private fun back(context: Context){
        val activity = context as AppCompatActivity
        val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
        navController.navigateUp()
    }

}