package com.capstone.surfingthegangwon.presentation.together

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.capstone.surfingthegangwon.presentation.together.databinding.ItemAreaBinding
import com.capstone.surfingthegangwon.core.resource.R as CoreRes

class AreaAdapter(private val items: List<String>) :
    RecyclerView.Adapter<AreaAdapter.ViewHolder>() {

    private var selectedPosition = 0

    inner class ViewHolder(val binding: ItemAreaBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.area.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val previousPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(previousPosition)
                    notifyItemChanged(selectedPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAreaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.area.text = items[position]

        val context = holder.itemView.context
        val selectedTypeface = ResourcesCompat.getFont(context, CoreRes.font.roboto_bold)
        val defaultTypeface = ResourcesCompat.getFont(context, CoreRes.font.roboto_regular)
        val blue = ContextCompat.getColor(
            context,
            CoreRes.color.blue_700
        )

        if (position == selectedPosition) {
            holder.binding.area.setTextColor(blue)
            holder.binding.area.typeface = selectedTypeface
        } else {
            holder.binding.area.setTextColor(Color.BLACK)
            holder.binding.area.typeface = defaultTypeface
        }
    }

    override fun getItemCount() = items.size
}


