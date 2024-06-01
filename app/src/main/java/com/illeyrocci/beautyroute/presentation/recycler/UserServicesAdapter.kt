package com.illeyrocci.beautyroute.presentation.recycler

import android.content.Context
import android.text.Editable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.illeyrocci.beautyroute.R
import com.illeyrocci.beautyroute.databinding.ItemUserServiceListBinding
import com.illeyrocci.beautyroute.domain.model.Service

class UserServicesAdapter(
    private val context: Context,
    private val onMakeAppointment: (servicePosition: Int, duration: Int) -> Unit
) :
    RecyclerView.Adapter<UserServicesAdapter.UserServiceViewHolder>() {

    private var data: ArrayList<Service> = arrayListOf()

    class UserServiceViewHolder(
        private val binding: ItemUserServiceListBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            item: Service,
            context: Context,
            onMakeAppointment: (servicePosition: Int, duration: Int) -> Unit
        ) {
            binding.apply {
                val adapter = ServicePhotosAdapter(context)

                includeExpandedServiceUser.imageList.adapter = adapter

                makeAppointment.setOnClickListener {
                    onMakeAppointment(
                        adapterPosition,
                        binding.editDurationUser.text.toString().toInt()
                    )
                }

                item.photos?.let { adapter.update(it) }

                nameUserServicesList.text = item.name
                editDurationUser.text = item.duration.toString()
                editCostUser.text = if (item.price == null) "" else item.price.toString()
                includeExpandedServiceUser.editDescription.text = item.description ?: ""

                iconDown.setOnClickListener {
                    includeExpandedServiceUser.root.isVisible = !includeExpandedServiceUser.root.isVisible
                    iconDown.setImageResource(if (includeExpandedServiceUser.root.isVisible) R.drawable.ic_up else R.drawable.ic_down)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserServiceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemUserServiceListBinding.inflate(inflater, parent, false)
        return UserServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserServiceViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, context, onMakeAppointment)
    }

    override fun getItemCount(): Int = data.size

    fun update(list: ArrayList<Service>) {
        val diffCallback = ServiceListComparator(data, list)
        val diffServices = DiffUtil.calculateDiff(diffCallback)
        data = list
        diffServices.dispatchUpdatesTo(this)
    }
}