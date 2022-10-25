package com.example.mappe2

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mappe2.data.Appointment
import com.example.mappe2.data.ContactViewModel
import com.example.mappe2.data.ContactViewModelFactory
import com.example.mappe2.databinding.FragmentAddAppointmentBinding
import java.time.LocalDateTime
import java.util.*

class AddAppointmentFragment : Fragment() {
    private val navigationArgs: AddAppointmentFragmentArgs by navArgs()
    private var _binding: FragmentAddAppointmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as ContactsApplication).database.contactDao(),
            (activity?.application as ContactsApplication).database.appointmentDao()
        )
    }

    lateinit var appointment: Appointment

    private fun bind(appointment: Appointment) {
        binding.apply {
            appointmentName.setText(appointment.name, TextView.BufferType.SPANNABLE)
            appointmentPlace.setText(appointment.place, TextView.BufferType.SPANNABLE)
            appointmentMessage.setText(appointment.message, TextView.BufferType.SPANNABLE)
            appointmentTime.setText(appointment.time.toString(), TextView.BufferType.SPANNABLE)
            appointmentContact.setText(appointment.contactId.toString(), TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener{updateAppointment()}
        }
    }

    private fun updateAppointment() {
        if (isEntryValid()) {
            viewModel.updateAppointment(
                this.navigationArgs.appointmentId,
                this.binding.appointmentName.text.toString(),
                this.binding.appointmentPlace.text.toString(),
                binding.appointmentMessage.toString(),
                parseMeetingTime(binding.appointmentTime.text.toString()),
                this.binding.appointmentContact.text.toString()
            )
            val action = AddAppointmentFragmentDirections.actionAddAppointmentFragmentToAppointmentListFragment()
            findNavController().navigate(action)
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isValidAppointment(
            binding.appointmentName.text.toString(),
            binding.appointmentPlace.text.toString(),
            binding.appointmentMessage.text.toString(),
            binding.appointmentTime.text.toString(),
            binding.appointmentContact.text.toString()
        )
    }

    private fun addNewAppointment() {
        Log.d("TIME","Date input: ${this.binding.appointmentTime.text.toString()}")
        if (isEntryValid()) {
            viewModel.addNewAppointment(
                binding.appointmentName.text.toString(),
                binding.appointmentPlace.text.toString(),
                binding.appointmentMessage.text.toString(),
                parseMeetingTime(binding.appointmentTime.text.toString()),
                binding.appointmentContact.text.toString()
            )
            val action = AddAppointmentFragmentDirections.actionAddAppointmentFragmentToAppointmentListFragment()
            findNavController().navigate(action)
        }
    }

    private fun parseMeetingTime(time: String): LocalDateTime {
        val dateAndTime = this.binding.appointmentTime.text.toString().split(" ")
        val date = dateAndTime[0].split("/")
        val time = dateAndTime[1].split(":")
        return LocalDateTime.of(
            date[2].toInt(),
            date[1].toInt(),
            date[0].toInt(),
            time[0].toInt(),
            time[1].toInt()
        );
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.appointmentId
        if (id > 0) {
            viewModel.retrieveAppointment(id).observe(this.viewLifecycleOwner) {selectedAppointment ->
                appointment = selectedAppointment
                bind(appointment)
            }
        } else {
            binding.saveAction.setOnClickListener { addNewAppointment() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}