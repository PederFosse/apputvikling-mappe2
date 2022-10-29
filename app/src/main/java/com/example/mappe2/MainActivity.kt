package com.example.mappe2

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupActionBarWithNavController

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 1
    private lateinit var navController: NavController
    private lateinit var br: BroadcastReceiver
    private val CHANNEL_ID = "channel01"

    @SuppressLint("UseSwitchCompatOrMaterialCode", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment)

        println(navHostFragment)
        navController = (navHostFragment  as NavHostFragment).navController
        setupActionBarWithNavController(this, navController)

        setSharedPreferance()
        checkForSmsPermission()
        createNotificationChannel()

        br = MyBroadcastReceiver()
        val filter = IntentFilter("START_REMINDERS")
        registerReceiver(br, filter)

        var brIntent = Intent(this, MyBroadcastReceiver::class.java)


        val switchReminder: Switch = findViewById<Switch>(R.id.reminders)
        switchReminder.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) {
                switchReminder.text = "Reminders ON"
                Toast.makeText(this@MainActivity, "ENABLE BR", Toast.LENGTH_SHORT).show()
                brIntent.putExtra("START", true)
                sendBroadcast(brIntent)
            }
            else {
                switchReminder.text = "Reminders OFF"
                Toast.makeText(this@MainActivity, "DISABLE BR", Toast.LENGTH_SHORT).show()
                brIntent.putExtra("START", false)
                sendBroadcast(brIntent)
            }
        }



    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(br)
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
                    // Tilgang ble gitt.
                } else {
                    // Tilgang nektet
                }
            }
        }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun setSharedPreferance () {
        val sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val edit = sharedPref.edit()

        edit.clear()
        edit.putString("defaultMessage", "Husk avtalen med meg!")
        edit.putString("messageTime", "15:32")
        edit.apply()
    }
    private fun checkForSmsPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.SEND_SMS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG,"Tilgang nektet")
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.SEND_SMS),
                MY_PERMISSIONS_REQUEST_SEND_SMS
            )
        } else {
            Log.d(TAG, "Tilgang ble gitt")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "AppointmentNotifications"
            val descriptionText = "AppointmentNotifications vises her"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
