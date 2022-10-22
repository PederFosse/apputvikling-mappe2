package com.example.mappe2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mappe2.data.Appointment
import com.example.mappe2.data.Contact
import com.example.mappe2.databinding.AppointmentListItemBinding
import com.example.mappe2.databinding.ContactListItemBinding

class AppointmentListAdapter(private val onItemClicked: (Appointment) -> Unit): ListAdapter<Appointment, AppointmentListAdapter.AppointmentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        return AppointmentViewHolder(AppointmentListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener{
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class AppointmentViewHolder(private var binding: AppointmentListItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(appointment: Appointment) {
            binding.apply {
                appointmentName.text = appointment.name
                appointmentPlace.text = appointment.place
                appointmentTime.text = appointment.time.toString()
                appointmentContact.text = appointment.contactId.toString()
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Appointment>() {
            override fun areItemsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Appointment, newItem: Appointment): Boolean {
                return oldItem.name == newItem.name
            }
        }
    }
}