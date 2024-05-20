package com.illeyrocci.beautyroute.presentation.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.illeyrocci.beautyroute.databinding.ItemServiceGalleryBinding


class ServicePhotosAdapter(private val context: Context) :
    RecyclerView.Adapter<ServicePhotosAdapter.ServicePhotoViewHolder>() {

    private var data: ArrayList<String> = arrayListOf()

    class ServicePhotoViewHolder(
        private val binding: ItemServiceGalleryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(url: String, context: Context) {
            binding.apply {
                Glide.with(context)
                    .load(url)
                    .apply(RequestOptions().centerCrop())
                    .into(itemImageView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServicePhotoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemServiceGalleryBinding.inflate(inflater, parent, false)
        return ServicePhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServicePhotoViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, context)
    }

    override fun getItemCount(): Int = data.size

    fun update(list: ArrayList<String>) {
        val diffCallback = ServiceGalleryComparator(data, list)
        val diffServices = DiffUtil.calculateDiff(diffCallback)
        data = list
        diffServices.dispatchUpdatesTo(this)
    }
}