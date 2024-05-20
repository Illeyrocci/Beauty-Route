package com.illeyrocci.beautyroute.presentation.recycler

import androidx.recyclerview.widget.DiffUtil

class ServiceGalleryComparator(
    private val oldList: List<String>,
    private val newList: List<String>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        oldList[oldItemPosition] == newList[newItemPosition]

    override fun getOldListSize() =
        oldList.size

    override fun getNewListSize() =
        newList.size
}