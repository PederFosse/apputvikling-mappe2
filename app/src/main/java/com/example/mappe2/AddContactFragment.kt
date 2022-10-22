package com.example.mappe2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mappe2.data.Contact
import com.example.mappe2.data.ContactViewModel
import com.example.mappe2.data.ContactViewModelFactory
import com.example.mappe2.databinding.FragmentAddContactBinding

class AddContactFragment : Fragment() {
    private val navigationArgs: AddContactFragmentArgs by navArgs()

    private var _binding: FragmentAddContactBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactViewModel by activityViewModels {
        ContactViewModelFactory(
            (activity?.application as ContactsApplication).database.contactDao(),
            (activity?.application as ContactsApplication).database.appointmentDao()
        )
    }

    lateinit var contact: Contact

    private fun bind(contact: Contact) {
        binding.apply {
            contactName.setText(contact.contactName, TextView.BufferType.SPANNABLE)
            contactNumber.setText(contact.phoneNumber, TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener{updateContact()}
        }
    }

    private fun updateContact() {
        if (isEntryValid()) {
            viewModel.updateContact(
                this.navigationArgs.contactId,
                this.binding.contactName.text.toString(),
                this.binding.contactNumber.text.toString()
            )
            val action = AddContactFragmentDirections.actionAddContactFragmentToContactListFragment()
            findNavController().navigate(action)
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.contactName.text.toString(),
            binding.contactNumber.text.toString(),
        )
    }

    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewContact(
                binding.contactName.text.toString(),
                binding.contactNumber.text.toString()
            )
            val action = AddContactFragmentDirections.actionAddContactFragmentToContactListFragment()
            findNavController().navigate(action)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = navigationArgs.contactId
        if (id > 0) {
            viewModel.retrieveContact(id).observe(this.viewLifecycleOwner) { selectedItem ->
                contact = selectedItem
                bind(contact)
            }
        } else {
            binding.saveAction.setOnClickListener { addNewItem() }
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