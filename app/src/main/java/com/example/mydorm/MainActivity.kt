package com.example.mydorm

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    companion object {
        var admin = false
        var url = "http://178.128.20.213/dorm/"
        var create_user = "create_user.php"
        var delete_user = "delete_user.php"
        var login = "login.php"
        var read_room = "read_room.php"
        var get_room_by_no = "get_room_by_no.php"
        var get_user_by_tel = "get_user_by_tel.php"
        var read_room_occupine = "read_room_occupine.php"
        var read_user = "read_user.php"
        var get_user_by_id = "get_user_by_id.php"
        var update_user = "update_user.php"
        var update_room = "update_room.php"
        var list_bill = "list_bill.php"
        var list_bill_by_room_no = "get_bill_by_room_no.php"
        var create_bill = "create_bill.php"
        var update_bill = "update_bill.php"
        var delete_bill = "delete_bill.php"
        var paid_bill = "paid_bill.php"
        var read_utility = "read_utility.php"
        var update_utility = "update_utility.php"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_bill, R.id.nav_login,
                R.id.nav_room, R.id.nav_user
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
