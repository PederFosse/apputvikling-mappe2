package com.example.mappe2

import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.asLiveData
import com.example.mappe2.data.Appointment
import com.example.mappe2.data.Contact
import com.example.mappe2.data.ContactRoomDatabase
import kotlinx.coroutines.delay
import java.time.ZoneId

val TAG = "channel01"




class MySendService : LifecycleService() {

    private var myAppointments: List<Appointment>? = null
    private var myContacts: List<Contact>? = null
    private val CHANNEL_ID = "channel01"

    override fun onCreate() {
        super.onCreate()
        //Setter opp observers på db lesing
        val db = ContactRoomDatabase.getDatabase(applicationContext)
        db.appointmentDao().getAppointments().asLiveData().observe(this) {
            appointments -> appointments.let {
            myAppointments = appointments
            }
        }
        db.contactDao().getContacts().asLiveData().observe(this) {
                contacts -> contacts.let {
            myContacts = contacts
            }
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val sendit = intent.getBooleanExtra("SEND", false)
        // Henter intent boolean og sender kun sms om send er true. Hvis send ikke er true opprettes kun obervere og lytter etter data
        if (sendit) {
            sendMessages(myAppointments, myContacts)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    fun notificationPopup(listAppointments: List<Appointment>?) {
        if (listAppointments === null) { return }

        var builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Du har ${listAppointments?.size} avtale(r) i dag")
            .setContentText("Gå inn i appen for å lese mer om avtalene dine")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(101, builder.build())
        }
    }

    fun sendMessages (listAppointments: List<Appointment>?, listContacts: List<Contact>?) {
        // Filterer så kun avtaler som er i dag blir inkludert
        val todaysMessages = listAppointments?.filter { appointment ->
            DateUtils.isToday(appointment.time.atZone(ZoneId.of("Europe/Oslo")).toInstant().toEpochMilli())
        }

        if (todaysMessages === null || listContacts === null) {
            // Returner om data er tom
            return
        } else {


        notificationPopup(todaysMessages)

        // Henter shared preference data
        val sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val defaultMessage = sharedPref.getString("defaultMessage", null)

        var smsManager=SmsManager.getDefault()

            // Utføres for hver avtale
        todaysMessages.forEach{
            val contact = listContacts.find { c -> c.id == it.contactId }
            val customMessage = it.message

            if (contact != null ) {
                var message = "Appointment with " + contact.contactName + " at " + it.place + ". "
                val phonenumber = contact.phoneNumber
                message += if (customMessage=="") {
                    defaultMessage
                } else  {
                    customMessage
                }

                try {
                    smsManager.sendTextMessage(phonenumber, null, message, null, null)
                } catch (e: Exception) {
                    Log.d(TAG, "Failure")
                }
            } else {
                Log.d(TAG, "Contact was null")
            }
        }
        }
    }
}