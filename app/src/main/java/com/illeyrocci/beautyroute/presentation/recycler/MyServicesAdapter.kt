package com.illeyrocci.beautyroute.presentation.recycler

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.ItemMyServiceListBinding
import com.illeyrocci.beautyroute.domain.model.Service

class MyServicesAdapter(private val context: Context) :
    RecyclerView.Adapter<MyServicesAdapter.MyServiceViewHolder>() {

    private var data: ArrayList<Service> = arrayListOf()

    class MyServiceViewHolder(
        private val binding: ItemMyServiceListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Service, context: Context) {
            val adapter = ServicePhotosAdapter(context)

            binding.apply {
                includeExpandedService.imageList.adapter = adapter

                item.photos?.let { adapter.update(it) }

                nameUserServicesList.text = Editable.Factory().newEditable(item.name)
                includeExpandedService.editDuration.text =
                    Editable.Factory().newEditable(item.duration.toString())
                includeExpandedService.editCost.text =
                    Editable.Factory().newEditable(item.price.toString())
                includeExpandedService.editDescriptionServiceList.text =
                    Editable.Factory().newEditable(item.description.toString())
                iconDown.setOnClickListener {
                    includeExpandedService.root.isVisible = !includeExpandedService.root.isVisible
                    iconDown.setImageResource(if (includeExpandedService.root.isVisible) R.drawable.ic_up else R.drawable.ic_down)
                }
                includeExpandedService.addImage.setOnClickListener {

                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyServiceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemMyServiceListBinding.inflate(inflater, parent, false)
        return MyServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyServiceViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, context)
    }

    override fun getItemCount(): Int = data.size

    fun update(list: ArrayList<Service>) {
        val diffCallback = ServiceListComparator(data, list)
        val diffServices = DiffUtil.calculateDiff(diffCallback)
        data = list
        diffServices.dispatchUpdatesTo(this)
    }
}