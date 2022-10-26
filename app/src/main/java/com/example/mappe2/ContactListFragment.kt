package com.example.mappe2

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mappe2.data.ContactViewModel
import com.example.mappe2.data.ContactViewModelFactory
import com.example.mappe2.databinding.FragmentContactListBinding

class ContactListFragment : Fragment() {
    private var _binding: FragmentContactListBinding? = null
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
        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val adapter = ContactListAdapter {
            val action = ContactListFragmentDirections.actionContactListFragmentToContactDetailFragment(it.id)
            this.findNavController().navigate(action)
        }

        viewModel.allContacts.observe(this.viewLifecycleOwner) {
            contacts -> contacts.let {
                adapter.submitList(it)
            }
        }

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        binding.appointmentsButton.setOnClickListener {
            val action = ContactListFragmentDirections.actionContactListFragmentToAppointmentListFragment()
            this.findNavController().navigate(action)
        }
        binding.floatingActionButton.setOnClickListener{
            val action = ContactListFragmentDirections.actionContactListFragmentToAddContactFragment()
            this.findNavController().navigate(action)
        }
    }
}