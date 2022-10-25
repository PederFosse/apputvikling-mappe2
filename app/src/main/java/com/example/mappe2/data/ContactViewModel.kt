package com.example.mappe2.data

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

class ContactViewModel(private val contactDao: ContactDao, private val appointmentDao: AppointmentDao): ViewModel() {
    val allContacts: LiveData<List<Contact>> = contactDao.getContacts().asLiveData()
    val allAppointments: LiveData<List<Appointment>> = appointmentDao.getAppointments().asLiveData()

    fun isEntryValid(name: String, phone: String): Boolean {
        if (name.isBlank() || phone.isBlank()) {
            return false
        }
        return true
    }

    private fun getNewContactEntry(name: String, phone: String): Contact {
        return Contact(contactName = name, phoneNumber = phone)
    }

    private fun getUpdatedContactEntry(id: Int, name: String, phone: String): Contact {
        return Contact(id, name, phone)
    }

    private fun insertContact(contact: Contact) {
        viewModelScope.launch { contactDao.insert(contact) }
    }

    private fun updateContact(contact: Contact) {
        viewModelScope.launch { contactDao.update(contact) }
    }

    fun addNewContact(name: String, phone: String) {
        val newContact = getNewContactEntry(name, phone)
        insertContact(newContact)
    }

    fun updateContact(id: Int, name: String, phone: String) {
        val updatedContact = getUpdatedContactEntry(id, name, phone)
        updateContact(updatedContact)
    }

    fun retrieveContact(id: Int): LiveData<Contact> {
        return contactDao.getContact(id).asLiveData()
    }

    fun deleteContact(contact: Contact) {
        viewModelScope.launch { contactDao.delete(contact) }
    }

    fun isValidAppointment(name: String, place: String, time: String, contactId: String): Boolean {
        if (name.isBlank() || place.isBlank() || contactId.isBlank() || time.isBlank()) {
            return false
        }
        return true
    }

    private fun getNewAppointment(name: String, place: String, message: String?,  time: LocalDateTime, contactId: Int): Appointment {
        var optionalMessage = ""
        if (message !== null) {
            optionalMessage = message
        }
        return Appointment(name=name, place=place, time=time, message=optionalMessage, contactId=contactId);
    }

    private fun getUpdatedAppointment(id: Int, name: String, place: String, message: String?, time: LocalDateTime, contactId: Int): Appointment {
        var optionalMessage = ""
        if (message !== null) {
            optionalMessage = message
        }
        return Appointment(id, name, place, optionalMessage, time, contactId);
    }

    private fun insertContact(appointment: Appointment) {
        viewModelScope.launch { appointmentDao.insert(appointment) }
    }

    private fun updateAppointment(appointment: Appointment) {
        viewModelScope.launch { appointmentDao.update(appointment) }
    }

    fun addNewAppointment(name: String, place: String, message: String?, time: LocalDateTime, contactId: String) {
        val newAppointment = getNewAppointment(name, place, message, time, contactId.toInt());
        insertContact(newAppointment)
    }

    fun updateAppointment(id: Int, name: String, place: String, message: String?, time: LocalDateTime, contactId: String) {
        val updatedAppointment = getUpdatedAppointment(id, name, place,message, time, contactId.toInt());
        updateAppointment(updatedAppointment)
    }

    fun retrieveAppointment(id: Int): LiveData<Appointment> {
        return appointmentDao.getAppointment(id).asLiveData()
    }

    fun deleteAppointment(appointment: Appointment) {
        viewModelScope.launch { appointmentDao.delete(appointment) }
    }
}

class ContactViewModelFactory(private val contactDao: ContactDao, private val appointmentDao: AppointmentDao): ViewModelProvider.Factory {
    override fun <T: ViewModel> create (modelClass:Class<T>): T {
        if(modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(contactDao, appointmentDao) as T
        }
        throw IllegalArgumentException("Unknown viewModel class")
    }
}