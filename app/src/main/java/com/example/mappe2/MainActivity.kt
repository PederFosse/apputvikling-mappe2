package com.example.mappe2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController
import com.example.mappe2.data.ContactViewModel
import com.example.mappe2.data.ContactViewModelFactory
import com.example.mappe2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 1
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)

        println(navHostFragment)
        navController = (navHostFragment  as NavHostFragment).navController
        setupActionBarWithNavController(this, navController)

        setSharedPreferance()

        checkForSmsPermission()

        val intent = Intent(this, MySendService::class.java)
        startService(intent)

    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_SEND_SMS -> {
                if (permissions[0].equals(Manifest.permission.SEND_SMS)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // Permission was granted.
                } else {
                    // Permission denied.
                    Toast.makeText(this,"Failure to obtain permission!",Toast.LENGTH_LONG).show()
                }
            }
        }

    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setSharedPreferance () {
        val sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val edit = sharedPref.edit()
        edit.clear()
        edit.putString("defaultMessage", "Dette er standard SMS dersom avtalen ikke har en melding")
        edit.putString("messageTime", "9:00")
        edit.apply()
        val fef = sharedPref.getString("defaultMessage", null)
    }
    private fun checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "Permission not granted", Toast.LENGTH_SHORT).show()

            // Permission not yet granted. Use requestPermissions().
            // MY_PERMISSIONS_REQUEST_SEND_SMS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.SEND_SMS),
                MY_PERMISSIONS_REQUEST_SEND_SMS
            )
        } else {
            // Permission already granted. Enable the SMS button.

        }
    }

}
