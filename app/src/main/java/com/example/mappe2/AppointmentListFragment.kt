package com.example.mappe2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mappe2.R
import com.example.mappe2.data.ContactViewModel
import com.example.mappe2.data.ContactViewModelFactory
import com.example.mappe2.databinding.FragmentAppointmentListBinding
import com.example.mappe2.databinding.FragmentContactListBinding

class AppointmentListFragment : Fragment() {
    private var _binding: FragmentAppointmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as ContactsApplication).database.contactDao(),
            (activity?.application as ContactsApplication).database.appointmentDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAppointmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = AppointmentListAdapter {
            val action = AppointmentListFragmentDirections.actionAppointmentListFragmentToAppointmentDetailFragment(it.id)
            this.findNavController().navigate(action)
        }

        viewModel.allAppointments.observe(this.viewLifecycleOwner) {
            appointments -> appointments.let {
                adapter.submitList(it)
            }
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.contactsButton.setOnClickListener {
            val action = AppointmentListFragmentDirections.actionAppointmentListFragmentToContactListFragment()
            this.findNavController().navigate(action)
        }
        binding.floatingActionButton.setOnClickListener {
            val action = AppointmentListFragmentDirections.actionAppointmentListFragmentToAddAppointmentFragment()
            this.findNavController().navigate(action)
        }
    }
}