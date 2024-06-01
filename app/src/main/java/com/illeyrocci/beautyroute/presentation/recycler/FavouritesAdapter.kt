package com.illeyrocci.beautyroute.presentation.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.illeyrocci.beautyroute.databinding.ItemFavouriteListBinding
import com.illeyrocci.beautyroute.domain.model.User

class FavouritesAdapter(
    private val onUserClicked: (String) -> Unit,
    private val onUserExclude: (String) -> Unit
) : RecyclerView.Adapter<FavouritesAdapter.FavouriteViewHolder>() {

    private var data: List<User> = listOf()

    class FavouriteViewHolder(private val binding: ItemFavouriteListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            user: User,
            onUserClicked: (String) -> Unit,
            onUserExclude: (String) -> Unit
        ) {

            Log.d("FINTAGG", user.toString())
            binding.nameFavourite.text = user.name
            binding.servicesFavourite.text =
                if (user.services.isNotEmpty()) user.services.map { it.name }
                    .reduce { acc, s -> "$acc, $s" } else ""

            binding.root.setOnClickListener {
                onUserClicked(user.uid)
            }
            binding.iconExclude.setOnClickListener {
                onUserExclude(user.uid)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemFavouriteListBinding.inflate(inflater, parent, false)
        return FavouriteViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, onUserClicked, onUserExclude)
    }

    fun update(results: List<User>) {
        data = results
        Log.d("FINTAGG", data.toString())
        notifyDataSetChanged()
    }
}