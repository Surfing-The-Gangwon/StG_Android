package com.capstone.surfingthegangwon.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.surfingthegangwon.presentation.home.databinding.ItemPlaceListBinding

class PlaceListAdapter: ListAdapter<PlaceMarker, PlaceListAdapter.PlaceViewHolder>(diffCallback) {

    inner class PlaceViewHolder(private val binding: ItemPlaceListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: PlaceMarker) = with(binding) {
            tvPlaceName.text = place.name
            tvPlaceAddress.text = "강원특별자치도 양양시 양양군 70 죽도해변"
            tvPlacePhone.text = "033-253-9999"

            imageThumbnail.setImageResource(
                when (place.category) {
                    Category.SURF_SHOP   -> R.drawable.beach_image_1
                    Category.SURF_SCHOOL -> R.drawable.beach_image_2
                    Category.TOURIST     -> R.drawable.beach_image_1
                    else                 -> R.drawable.beach_image_1
                }
            )
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PlaceMarker>() {
            override fun areItemsTheSame(oldItem: PlaceMarker, newItem: PlaceMarker): Boolean {
                return oldItem.name == newItem.name && oldItem.lat == newItem.lat && oldItem.lng == newItem.lng
            }

            override fun areContentsTheSame(oldItem: PlaceMarker, newItem: PlaceMarker): Boolean {
                return oldItem == newItem
            }
        }
    }
}