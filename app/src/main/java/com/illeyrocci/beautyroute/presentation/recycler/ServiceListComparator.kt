package com.illeyrocci.beautyroute.presentation.recycler

import androidx.recyclerview.widget.DiffUtil
import com.illeyrocci.beautyroute.domain.model.Service

class ServiceListComparator(
    private val oldList: List<Service>,
    private val newList: List<Service>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition].uid == newList[newItemPosition].uid

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getOldListSize() =
        oldList.size

    override fun getNewListSize() =
        newList.size
}