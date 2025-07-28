package com.capstone.surfingthegangwon.presentation.together

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.surfingthegangwon.presentation.together.databinding.ItemWeekDayBinding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import com.capstone.surfingthegangwon.core.resource.R as CoreRes


class WeekAdapter(private val onDateClick: (LocalDate) -> Unit) :
    ListAdapter<LocalDate, WeekAdapter.DateViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<LocalDate>() {
        override fun areItemsTheSame(oldItem: LocalDate, newItem: LocalDate) = oldItem == newItem
        override fun areContentsTheSame(oldItem: LocalDate, newItem: LocalDate) = oldItem == newItem
    }

    inner class DateViewHolder(
        private val binding: ItemWeekDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(date: LocalDate) = with(binding) {
            tvDay.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
            tvDate.text = date.dayOfMonth.toString()

            val context = root.context
            val red = ContextCompat.getColor(context, CoreRes.color.sunday)
            val blue = ContextCompat.getColor(context, CoreRes.color.saturday)
            val defaultColor = Color.BLACK

            when (date.dayOfWeek) {
                DayOfWeek.SUNDAY -> {
                    tvDay.setTextColor(red)
                    tvDate.setTextColor(red)
                }

                DayOfWeek.SATURDAY -> {
                    tvDay.setTextColor(blue)
                    tvDate.setTextColor(blue)
                }

                else -> {
                    tvDay.setTextColor(defaultColor)
                    tvDate.setTextColor(defaultColor)
                }
            }

            root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onDateClick(getItem(position))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemWeekDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

