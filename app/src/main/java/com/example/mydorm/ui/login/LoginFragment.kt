package com.example.mydorm.ui.login

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mydorm.MainActivity
import com.example.mydorm.R
import com.example.mydorm.RecyclerRoomAdapter
import com.example.mydorm.VolleySingleton
import kotlinx.android.synthetic.main.login_fragment.*
import kotlinx.android.synthetic.main.room_fragment.*
import org.json.JSONObject
import java.lang.Exception
import kotlin.math.log


class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }
    private lateinit var viewModel: LoginViewModel
    private lateinit var username: EditText
    private lateinit var password: EditText

    private var url: String = "http://178.128.20.213/dorm/"
    private var url_login: String = "login.php"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        val btn_login = view!!.findViewById(R.id.button) as Button
        btn_login.setOnClickListener {
            if (!MainActivity.admin){
                activity?.let { it1 -> login(it1) }
            }
            else{
                MainActivity.admin = false
                editTextTextPersonName.isEnabled = true
                editTextTextPassword.isEnabled = true
                editTextTextPersonName.setText("")
                editTextTextPassword.setText("")
                button.setText("LOGIN")
            }
        }
        checkAdmin()
    }

    private fun checkAdmin(){
        if (MainActivity.admin){
            editTextTextPersonName.isEnabled = false
            editTextTextPassword.isEnabled = false
            button.setText("LOGOUT")
        }
        else{
            editTextTextPersonName.isEnabled = true
            editTextTextPassword.isEnabled = true
            editTextTextPersonName.setText("")
            editTextTextPassword.setText("")
            button.setText("LOGIN")
        }
    }

    private fun httpRequest(body: JSONObject, url: String, method: Int, context: Context){
        val request = JsonObjectRequest(method,this.url + url, body, Response.Listener { response ->
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
        if(url.equals(this.url_login)){
            val result = response.getJSONObject("result")
            try {
                result.getString("username")
                println("login success")
                MainActivity.admin = true

                editTextTextPersonName.isEnabled = false
                editTextTextPassword.isEnabled = false
                button.setText("LOGOUT")
            }
            catch (e: Exception){
                println(e.toString())
            }
        }
    }


    private fun login(context: Context){
        val username: EditText = view!!.findViewById(R.id.editTextTextPersonName)
        val password: EditText = view!!.findViewById(R.id.editTextTextPassword)
        println("Login ${username.text}:${password.text}")
        var param: JSONObject = JSONObject()
        param.put("username", username.text)
        param.put("password", password.text)
        this.httpRequest(param, this.url_login, Request.Method.POST , context)
    }

}
