package com.example.mydorm.ui.user

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
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mydorm.MainActivity
import com.example.mydorm.R
import com.example.mydorm.VolleySingleton
import kotlinx.android.synthetic.main.user_edit_fragment.*
import org.json.JSONObject
import java.lang.Exception
import kotlin.concurrent.timer

class UserEditFragment : Fragment() {

    lateinit var firstname_text: EditText
    lateinit var lastname_text: EditText
    lateinit var tel_text: EditText
    private var user_id: Int = 0

    companion object {
        fun newInstance() = UserEditFragment()
    }

    private lateinit var viewModel: UserEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_edit_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserEditViewModel::class.java)

        firstname_text = view!!.findViewById(R.id.firstnameEditText)
        lastname_text = view!!.findViewById(R.id.lastnameEditText)
        tel_text = view!!.findViewById(R.id.telEditText)

        if (!arguments!!.isEmpty) {
            println(arguments!!.getInt("id").toString())
            println(arguments!!.getString("firstname"))
            println(arguments!!.getString("lastname"))
            println(arguments!!.getString("tel"))

            firstname_text.setText(arguments!!.getString("firstname"))
            lastname_text.setText(arguments!!.getString("lastname"))
            tel_text.setText(arguments!!.getString("tel"))
            this.user_id = arguments!!.getInt("id")

            if (this.user_id == 0){
                deleteUserEditBtn.visibility = View.INVISIBLE
                deleteUserEditBtn.isClickable = false
            }
        }

        saveUserEditBtn.setOnClickListener {
            context?.let { it1 -> this.save(it1) }
        }

        deleteUserEditBtn.setOnClickListener {
            context?.let { it1 -> this.delete(it1) }
        }

        checkAdmin()
    }

    private fun checkAdmin(){
        if(!MainActivity.admin){
            firstnameEditText.isEnabled = false
            lastnameEditText.isEnabled = false
            telEditText.isEnabled = false
            saveUserEditBtn.isVisible = false
            deleteUserEditBtn.isVisible = false
        }
    }

    private fun httpRequest(body: JSONObject, url: String, method: Int, context: Context){
        val request = JsonObjectRequest(method,MainActivity.url + url, body, Response.Listener { response ->
            try {
                Log.i("httpRequest", "$response")
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


    private fun save(context: Context){
        var payload = JSONObject()
        payload.put("firstname", firstname_text.text.toString())
        payload.put("lastname", lastname_text.text.toString())
        payload.put("tel", tel_text.text.toString())
        payload.put("id", this.user_id)
        if (this.user_id == 0){
            this.httpRequest(payload, MainActivity.create_user, Request.Method.POST , context)
        }
        else{
            this.httpRequest(payload, MainActivity.update_user, Request.Method.POST , context)
        }
        this.back(context)
    }

    private fun delete(context: Context) {
        var payload = JSONObject()
        payload.put("id", this.user_id)
        this.httpRequest(payload, MainActivity.delete_user, Request.Method.POST , context)
        this.back(context)
    }

    private fun back(context: Context){
        val activity = context as AppCompatActivity
        val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
        Thread.sleep(500)
        navController.navigateUp()
    }

}