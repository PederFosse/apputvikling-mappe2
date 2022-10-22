package com.example.mappe2

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mappe2.data.Contact
import com.example.mappe2.data.ContactViewModel
import com.example.mappe2.data.ContactViewModelFactory
import com.example.mappe2.databinding.FragmentContactDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ContactDetailFragment : Fragment() {
    private val navigationArgs: ContactDetailFragmentArgs by navArgs()
    lateinit var contact: Contact

    private var _binding: FragmentContactDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as ContactsApplication).database.contactDao(),
            (activity?.application as ContactsApplication).database.appointmentDao()
        )
    }

    private fun bind(contact: Contact) {
        binding.apply {
            contactName.text = contact.contactName
            contactPhone.text = contact.phoneNumber
            deleteContact.setOnClickListener {
                showConfirmationDialog()
            }
            editContact.setOnClickListener {
                editContact()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.contactId

        viewModel.retrieveContact(id).observe(this.viewLifecycleOwner) {selectedContact ->
            contact = selectedContact
            bind(contact)
        }
    }

    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteContact()
            }
            .show()
    }

    private fun deleteContact() {
        viewModel.deleteContact(contact)
        findNavController().navigateUp()
    }

    private fun editContact() {
        val action = ContactDetailFragmentDirections.actionContactDetailFragmentToAddContactFragment(contact.id)
        this.findNavController().navigate(action)
    }
}