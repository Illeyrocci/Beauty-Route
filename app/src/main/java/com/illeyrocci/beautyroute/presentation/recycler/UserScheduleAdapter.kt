package com.illeyrocci.beautyroute.presentation.recycler

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.ItemMyScheduleBinding
import com.illeyrocci.beautyroute.domain.model.CustomPair
import com.illeyrocci.beautyroute.domain.model.ScheduleDay
import java.util.Date

class UserScheduleAdapter(
    private val duration: Int,
    private val onMakeAppointment: (Int) -> Unit
) : RecyclerView.Adapter<UserScheduleAdapter.TimeSlotViewHolder>() {

    private var data: ScheduleDay

    init {
        val sections: ArrayList<CustomPair> = arrayListOf()
        repeat(96) { sections.add(CustomPair(false, null)) }
        data = ScheduleDay(dayStartUnixTime = (Date().time / 86400000) * 86400000, sections = sections)
    }

    class TimeSlotViewHolder(private val binding: ItemMyScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            duration: Int,
            sections: ArrayList<CustomPair>,
            onMakeAppointment: (Int) -> Unit
        ) {

            with(binding) {
                excl.isVisible = false

                val sectionsNeeded = (duration + 14) / 15
                var currentSlotMatches = true
                for (i in adapterPosition until adapterPosition+sectionsNeeded) {
                    if (sections[i%96].first) currentSlotMatches = false
                }

                root.setBackgroundResource(if (currentSlotMatches) R.color.white_back else R.color.grey_transparent)

                root.setOnClickListener {
                    if (currentSlotMatches) onMakeAppointment(adapterPosition)
                }

                val minutes = 6 * 60 + adapterPosition * 15
                val min = if (minutes % 60 == 0) "00" else minutes % 60
                timeStamp.text = "${(minutes / 60) % 24}:$min"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMyScheduleBinding.inflate(inflater, parent, false)
        return TimeSlotViewHolder(binding)
    }

    override fun getItemCount(): Int = 96

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        holder.bind(duration, data.sections, onMakeAppointment)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(day: ScheduleDay) {
        Log.d("TAGGG", "sch day = $day")
        data = day
        notifyDataSetChanged()
    }
}