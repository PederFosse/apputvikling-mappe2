package com.example.mappe2

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.lang.Integer.parseInt
import java.util.*

class MyPeriodic : Service() {

    lateinit var smsIntent: PendingIntent
    lateinit var manager: AlarmManager


    override fun onCreate() {
        super.onCreate()
        Log.d("channel01", "Periodic create")


        val smsLaunchIntent = Intent(this, MySendService::class.java)
        smsLaunchIntent.putExtra("SEND", true)
        smsIntent = PendingIntent.getService(this, 1, smsLaunchIntent, 0)


        manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // to get observers running
        var primeObservers = Intent(this, MySendService::class.java)
        primeObservers.putExtra("SEND", false)
        startService(primeObservers)


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scheduleAlarm()
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("channel01", "Periodic destroy")
        manager.cancel(smsIntent)
    }


    fun scheduleAlarm() {

        val sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        var messageTime = sharedPref.getString("messageTime", null)

        if (messageTime != null) {
            Log.d("channel01", messageTime)

        } else {
            Log.d("channel01", "No time")
            messageTime = "09:30"
        }

        val intervalMs: Long = 1000 * 60 * 60 * 24


        val prefHour = parseInt("${messageTime[0]}${messageTime[1]}")
        val prefMinute = parseInt("${messageTime[3]}${messageTime[4]}")

        val cNow = Calendar.getInstance()
        val cPref = Calendar.getInstance()
        cPref.set(Calendar.HOUR_OF_DAY, prefHour)
        cPref.set(Calendar.MINUTE, prefMinute)
        cPref.set(Calendar.SECOND, 0)

        val diff = cPref.timeInMillis - cNow.timeInMillis
        var wait = 0L

        wait = if (diff >= 0) {
            cNow.timeInMillis + diff;
        } else {
            cNow.timeInMillis + (1000 * 60 * 60 * 24) - diff
        }

        wait += 1000 * 10

                    Log.d("channel01", "Wait ${wait}")


        manager.setRepeating(AlarmManager.RTC_WAKEUP, wait, AlarmManager.INTERVAL_DAY, smsIntent) // repeating interval
    }


}