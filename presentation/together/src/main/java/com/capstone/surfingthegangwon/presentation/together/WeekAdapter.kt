package com.capstone.surfingthegangwon.presentation.together

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


class WeekAdapter(private val onDateClick: (LocalDate) -> Unit) :
    ListAdapter<LocalDate, WeekAdapter.DateViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<LocalDate>() {
        override fun areItemsTheSame(oldItem: LocalDate, newItem: LocalDate) = oldItem == newItem
        override fun areContentsTheSame(oldItem: LocalDate, newItem: LocalDate) = oldItem == newItem
    }

    inner class DateViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDay = view.findViewById<TextView>(R.id.day)
        val tvDate = view.findViewById<TextView>(R.id.date)

        init {
            view.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val clickedDate = getItem(position)
                    onDateClick(clickedDate)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_week_day, parent, false)
        return DateViewHolder(view)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = getItem(position)
        holder.tvDay.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
        holder.tvDate.text = date.dayOfMonth.toString()

        val context = holder.itemView.context
        val red = ContextCompat.getColor(
            context,
            com.capstone.surfingthegangwon.core.resource.R.color.sunday
        )
        val blue = ContextCompat.getColor(
            context,
            com.capstone.surfingthegangwon.core.resource.R.color.saturday
        )
        val defaultColor = ContextCompat.getColor(context, android.R.color.black)

        when (date.dayOfWeek) {
            DayOfWeek.SUNDAY -> {
                holder.tvDay.setTextColor(red)
                holder.tvDate.setTextColor(red)
            }

            DayOfWeek.SATURDAY -> {
                holder.tvDay.setTextColor(blue)
                holder.tvDate.setTextColor(blue)
            }

            else -> {
                holder.tvDay.setTextColor(defaultColor)
                holder.tvDate.setTextColor(defaultColor)
            }
        }
    }
}
