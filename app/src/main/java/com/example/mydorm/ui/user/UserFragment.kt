package com.example.mydorm.ui.user

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.mydorm.*
import kotlinx.android.synthetic.main.room_fragment.*
import kotlinx.android.synthetic.main.user_fragment.*
import org.json.JSONObject
import java.lang.Exception

class UserFragment : Fragment() {

    companion object {
        fun newInstance() = UserFragment()
    }

    private lateinit var viewModel: UserViewModel
    private var firtnameList = mutableListOf<String>()
    private var lastnameList = mutableListOf<String>()
    private var telList = mutableListOf<String>()
    private var idList = mutableListOf<Int>()
    private var imageList = mutableListOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.user_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        context?.let { this.postToList(it) }

        addUserBtn.setOnClickListener {
            val activity = view?.context as AppCompatActivity
            this.addUser(activity)
        }

        searchUserBtn.setOnClickListener {
            context?.let { it1 -> getUserByTel(it1) }
        }

        checkAdmin()

    }

    private fun checkAdmin(){
        if(MainActivity.admin){
            addUserBtn.isVisible = true
        }
        else{
            addUserBtn.isVisible = false
        }
    }

    private fun clearData(){
        firtnameList.clear()
        lastnameList.clear()
        telList.clear()
        imageList.clear()
        idList.clear()
    }

    private fun addUser(activity: AppCompatActivity){
        val navController = Navigation.findNavController(activity, R.id.nav_host_fragment)
        val bundle = Bundle()
        bundle.putInt("id", 0)
        bundle.putString("firstname", "")
        bundle.putString("lastname", "")
        bundle.putString("tel", "")
        navController.navigate(R.id.nav_user_edit, bundle)
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
        if(url.equals(MainActivity.read_user) || url.equals(MainActivity.get_user_by_tel)){
            clearData()
            val result = response.getJSONArray("result")
            for(i in 0..(result.length() - 1)){
                val item = JSONObject(result[i].toString())
                val id = item.getInt("id")
                val firstname = item.getString("first_name")
                val lastname = item.getString("last_name")
                val tel = item.getString("tel")
                addUser(firstname, lastname, tel, id, R.mipmap.ic_launcher_round)
            }
            rv_user.layoutManager = LinearLayoutManager(activity)
            rv_user.adapter = RecyclerUserAdapter(idList, firtnameList, lastnameList, telList, imageList)
        }
    }

    private fun addUser(firstname: String, lastname: String, tel: String, id: Int, image: Int){
        firtnameList.add(firstname)
        lastnameList.add(lastname)
        telList.add(tel)
        imageList.add(image)
        idList.add(id)
    }

    private fun postToList(context: Context){
        this.httpRequest(JSONObject(), MainActivity.read_user, Request.Method.GET , context)
    }

    private fun getUserByTel(context: Context){
        var payload = JSONObject()
        payload.put("tel", telEditText.text)
        this.httpRequest(payload, MainActivity.get_user_by_tel, Request.Method.POST , context)
    }

}