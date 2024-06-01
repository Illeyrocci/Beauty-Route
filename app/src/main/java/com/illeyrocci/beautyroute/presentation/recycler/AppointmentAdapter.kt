package com.illeyrocci.beautyroute.presentation.recycler

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.illeyrocci.beautyroute.databinding.ItemAppointmentListBinding
import com.illeyrocci.beautyroute.domain.model.Appointment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppointmentAdapter(
    private val onClicked: (String) -> Unit
) : RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    private var data: ArrayList<Appointment> = arrayListOf()

    class AppointmentViewHolder(private val binding: ItemAppointmentListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            appointment: Appointment,
            onUserClicked: (String) -> Unit
        ) {

            binding.apply {
                nameAppointmentList.text = appointment.service.name
                val dateTime = Date(appointment.startTime)
                dateAppointmentList.text = SimpleDateFormat("d MMMM", Locale("ru")).format(dateTime)
                timeAppointmentList.text = SimpleDateFormat("HH:mm", Locale("ru")).format(dateTime)
                root.setOnClickListener { onUserClicked(appointment.id) }
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAppointmentListBinding.inflate(inflater, parent, false)
        return AppointmentViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, onClicked)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(list: ArrayList<Appointment>) {
        data = list
        notifyDataSetChanged()
    }
}