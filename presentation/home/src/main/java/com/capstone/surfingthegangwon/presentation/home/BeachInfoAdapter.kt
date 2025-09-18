package com.capstone.surfingthegangwon.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.surfingthegangwon.BeachInfo
import com.capstone.surfingthegangwon.presentation.home.databinding.ItemBeachInfoBinding

class BeachInfoAdapter(
    private val onItemClick: (BeachInfo) -> Unit
): ListAdapter<BeachInfo, BeachInfoAdapter.BeachInfoViewHolder>(
    object : DiffUtil.ItemCallback<BeachInfo>() {
        override fun areItemsTheSame(oldItem: BeachInfo, newItem: BeachInfo): Boolean {
            return oldItem.seashoreId == newItem.seashoreId
        }

        override fun areContentsTheSame(oldItem: BeachInfo, newItem: BeachInfo): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class BeachInfoViewHolder(private val binding: ItemBeachInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: BeachInfo) {
            binding.tvBeachName.text = item.beachName
            binding.tvTempInfo.text = "기온 ${item.airTemp} 수온 ${item.waterTemp}"
            binding.tvWaveInfo.text = "${item.waveHeight} - ${item.wavePeriod}"
            binding.tvWindInfo.text = "${item.windDirection} ${item.windSpeed}"

            binding.root.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeachInfoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBeachInfoBinding.inflate(inflater, parent, false)
        return BeachInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BeachInfoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}