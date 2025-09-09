package com.capstone.surfingthegangwon.presentation.together

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.capstone.surfingthegangwon.domain.together.model.SessionItem
import com.capstone.surfingthegangwon.presentation.together.databinding.ItemSessionBinding

class SessionAdapter :
    ListAdapter<SessionItem, SessionAdapter.ContentViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SessionItem>() {
            override fun areItemsTheSame(
                oldItem: SessionItem,
                newItem: SessionItem
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: SessionItem,
                newItem: SessionItem
            ): Boolean = oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding = ItemSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** ViewHolder: 세션 컨텐츠 (타이틀, 시간, 참가자 등) */
    inner class ContentViewHolder(
        private val binding: ItemSessionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SessionItem) = with(binding) {
            // 텍스트 세팅
            title.text = item.title
            time.text = item.sessionTime
            numbers.text = item.participants
            grade.setGrade(item.grade)
        }
    }
}
