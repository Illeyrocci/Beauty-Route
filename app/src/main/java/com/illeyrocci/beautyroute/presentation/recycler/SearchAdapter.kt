package com.illeyrocci.beautyroute.presentation.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.illeyrocci.beautyroute.databinding.ItemSearchListBinding
import com.illeyrocci.beautyroute.domain.model.User

class SearchAdapter(
    private val onUserClicked: (String) -> Unit
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {


    private var data: List<User> = emptyList()

    class SearchViewHolder(private val binding: ItemSearchListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            user: User,
            onUserClicked: (String) -> Unit
        ) {

            binding.nameMaster.text = user.name
            binding.servicesMaster.text = user.services.map{ it.name }.reduce { acc, s -> "$acc, $s" }

            binding.root.setOnClickListener {
                onUserClicked(user.uid)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchListBinding.inflate(inflater, parent, false)
        return SearchViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, onUserClicked)
    }

    fun update(results: List<User>) {
        data = results
        notifyDataSetChanged()
    }
}