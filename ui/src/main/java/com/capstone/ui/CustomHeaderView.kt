package com.capstone.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.capstone.ui.databinding.HeaderBinding

class CustomHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = HeaderBinding.inflate(LayoutInflater.from(context), this, true)

    fun setScreenTitle(title: String) {
        binding.tvScreenTitle.text = title
    }

    fun setBeachTabItem(tabItems: List<String> = listOf("양양", "고성", "속초", "강릉", "삼척")) {
        val tabLayout = binding.beachTabLayout

        tabItems.forEach { beachName ->
            tabLayout.addTab(tabLayout.newTab().setText(beachName))
        }
    }
}
