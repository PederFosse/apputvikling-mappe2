package com.example.mappe2

import android.app.Application
import com.example.mappe2.data.ContactRoomDatabase

class ContactsApplication: Application() {
    val database: ContactRoomDatabase by lazy {ContactRoomDatabase.getDatabase(this)}
}