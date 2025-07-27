package com.capstone.surfingthegangwon.presentation.together

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.surfingthegangwon.domain.together.model.Grade
import com.capstone.surfingthegangwon.domain.together.model.SessionListItem
import com.capstone.surfingthegangwon.presentation.together.databinding.ItemSectionHeaderBinding
import com.capstone.surfingthegangwon.presentation.together.databinding.ItemSessionBinding
import com.capstone.surfingthegangwon.core.resource.R as CoreRes

class SessionAdapter : ListAdapter<SessionListItem, RecyclerView.ViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<SessionListItem>() {
        override fun areItemsTheSame(oldItem: SessionListItem, newItem: SessionListItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: SessionListItem,
            newItem: SessionListItem
        ): Boolean {
            return oldItem == newItem
        }
    }


    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_CONTENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SessionListItem.Header -> TYPE_HEADER
            is SessionListItem.Content -> TYPE_CONTENT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val binding = ItemSectionHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                HeaderViewHolder(binding)
            }

            TYPE_CONTENT -> {
                val binding = ItemSessionBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                )
                ContentViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is SessionListItem.Header -> (holder as HeaderViewHolder).bind(item)
            is SessionListItem.Content -> (holder as ContentViewHolder).bind(item)
        }
    }

    inner class HeaderViewHolder(private val binding: ItemSectionHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SessionListItem.Header) = with(binding) {
            beachName.text = item.beachName
            temperature.text = item.temperature
            wave.text = item.wave
            wind.text = item.wind
        }
    }

    inner class ContentViewHolder(private val binding: ItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SessionListItem.Content) = with(binding) {
            title.text = item.title
            sessionTime.text = item.sessionTime
            time.text = item.time
            numbers.text = item.participants
            gradeText.text = item.grade.label

            val gradeColorRes = when (item.grade) {
                Grade.Beginner -> CoreRes.color.grade_beginner
                Grade.Intermediate -> CoreRes.color.grade_intermediate
                Grade.Advanced -> CoreRes.color.grade_advanced
            }

            val bgRes = when(item.grade) {
                Grade.Beginner -> R.drawable.grade_beginner_background
                Grade.Intermediate -> R.drawable.grade_intermediate_background
                Grade.Advanced -> R.drawable.grade_advanced_background
            }

            grade.setBackgroundResource(bgRes)
            val color = ContextCompat.getColor(root.context, gradeColorRes)
            gradeIcon.imageTintList = ColorStateList.valueOf(color)
        }
    }
}

