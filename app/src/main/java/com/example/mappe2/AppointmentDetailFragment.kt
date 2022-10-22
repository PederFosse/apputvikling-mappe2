package com.example.mappe2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mappe2.data.Appointment
import com.example.mappe2.data.ContactViewModel
import com.example.mappe2.data.ContactViewModelFactory
import com.example.mappe2.databinding.FragmentAppointmentDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AppointmentDetailFragment : Fragment() {
    private val navigationArgs: AppointmentDetailFragmentArgs by navArgs()
    lateinit var appointment: Appointment

    private var _binding: FragmentAppointmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as ContactsApplication).database.contactDao(),
            (activity?.application as ContactsApplication).database.appointmentDao()
        )
    }

    private fun bind(appointment: Appointment) {
        binding.apply {
            appointmentName.text = appointment.name
            appointmentPlace.text = appointment.place
            appointmentTime.text = appointment.time.toString()
            appointmentContact.text = appointment.contactId.toString()
            deleteAppointment.setOnClickListener{
                showConfirmationDialog()
            }
            editAppointment.setOnClickListener {
                editAppointment()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppointmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.appointmentId

        viewModel.retrieveAppointment(id).observe(this.viewLifecycleOwner){selectedAppointment ->
            appointment = selectedAppointment
            bind(appointment)
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_appointment_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteAppointment()
            }
            .show()
    }

    private fun deleteAppointment() {
        viewModel.deleteAppointment(appointment)
        findNavController().navigateUp()
    }

    private fun editAppointment() {
        val action = AppointmentDetailFragmentDirections.actionAppointmentDetailFragmentToAddAppointmentFragment(appointment.id)
        this.findNavController().navigate(action)
    }

}