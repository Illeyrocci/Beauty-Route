package com.illeyrocci.beautyroute.presentation.recycler

import androidx.recyclerview.widget.DiffUtil
import com.illeyrocci.beautyroute.domain.model.ScheduleDay

class ScheduleGridComparator(
    private val oldDay: ScheduleDay,
    private val newDay: ScheduleDay
) : DiffUtil.Callback() {
    override fun getOldListSize() = 96

    override fun getNewListSize() = 96

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldItemPosition == newItemPosition

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldDay.sections[oldItemPosition] == newDay.sections[newItemPosition]
}
