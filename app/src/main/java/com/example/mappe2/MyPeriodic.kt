package com.example.mappe2

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.widget.Toast
import java.util.*

class MyPeriodic : Service() {

    lateinit var periodicIntent: PendingIntent
    lateinit var smsIntent: PendingIntent

    override fun onCreate() {
        super.onCreate()
        val periodicLaunchIntent = Intent(this, MyPeriodic::class.java)
        periodicIntent = PendingIntent.getService(this, 0, periodicLaunchIntent, 0)

        val smsLaunchIntent = Intent(this, MySendService::class.java)
        smsIntent = PendingIntent.getService(this, 1, smsLaunchIntent, 0)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scheduleAlarm()
        return super.onStartCommand(intent, flags, startId)

    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    fun scheduleAlarm() {
        val manager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intervalSec = 60 //60 * 60 * 24
        val intervalMs: Long = 60 * 1000

        Toast.makeText(applicationContext, "SCHEDULED PERIODIC CHECK", Toast.LENGTH_SHORT).show()

        val c = Calendar.getInstance()
        c.add(Calendar.SECOND, intervalSec)
        val afterSetDelay = c.timeInMillis

        manager.setRepeating(AlarmManager.RTC_WAKEUP, afterSetDelay, intervalMs, smsIntent) // repeating interval
        //manager.setExact(AlarmManager.RTC, afterSetDelay, periodicIntent)
        //manager.setExact(AlarmManager.RTC, afterSetDelay, smsIntent)

    }


}