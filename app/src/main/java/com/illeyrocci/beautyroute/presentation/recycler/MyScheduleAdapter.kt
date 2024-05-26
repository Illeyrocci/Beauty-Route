package com.illeyrocci.beautyroute.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.ItemMyScheduleBinding
import com.illeyrocci.beautyroute.domain.model.CustomPair
import com.illeyrocci.beautyroute.domain.model.ScheduleDay
import java.util.Date

class MyScheduleAdapter(
    private val onBlockSlot: (Int) -> Unit,
    private val onGoToAppointment: (Int) -> Unit,
) : RecyclerView.Adapter<MyScheduleAdapter.TimeSlotViewHolder>() {


    private var data: ScheduleDay

    init {
        val sections: ArrayList<CustomPair> = arrayListOf()
        repeat(96) { sections.add(CustomPair(false, null)) }
        data = ScheduleDay(dayStartUnixTime = Date().time, sections = sections)
    }

    class TimeSlotViewHolder(private val binding: ItemMyScheduleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: CustomPair,
            onBlockSlot: (Int) -> Unit,
            onGoToAppointment: (Int) -> Unit
        ) {

            with(binding) {
                if (item.second == null) {
                    root.setBackgroundResource(if (item.first) R.color.white_back else R.color.grey_transparent)
                    excl.isVisible = false
                    root.setOnClickListener {
                        onBlockSlot(adapterPosition)
                    }
                } else {
                    root.setBackgroundResource(R.color.orange_transparent)
                    excl.isVisible = true
                    root.setOnClickListener {
                        onGoToAppointment(adapterPosition)
                    }
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
        val item = data.sections[position]
        holder.bind(item, onBlockSlot, onGoToAppointment)
    }

    fun update(day: ScheduleDay) {
        val diffCallback = ScheduleGridComparator(data, day)
        val diffServices = DiffUtil.calculateDiff(diffCallback)
        data = day
        diffServices.dispatchUpdatesTo(this)
    }
}