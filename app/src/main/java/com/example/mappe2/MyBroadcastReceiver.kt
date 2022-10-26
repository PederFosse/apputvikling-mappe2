package com.example.mappe2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

public const val TAGg = "MyBroadcastReceiver"

class MyBroadcastReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val start = intent.getBooleanExtra("START", false)

        if (start) {
            context.startService(Intent(context, MyPeriodic::class.java))
        } else {
            context.stopService(Intent(context, MyPeriodic::class.java))
        }



    }
}