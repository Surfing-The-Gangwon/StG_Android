package com.capstone.surfingthegangwon.core.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.capstone.surfingthegangwon.core.ui.databinding.HeaderBinding
import com.google.android.material.tabs.TabLayout

class CustomHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = HeaderBinding.inflate(LayoutInflater.from(context), this, true)
    private var tabSelectedListener: OnTabSelectedListener? = null

    interface OnTabSelectedListener {
        fun onTabSelected(position: Int)
    }

    fun setScreenTitle(title: String) {
        binding.tvScreenTitle.text = title
    }

    fun setBeachTabItem(tabItems: List<String> = listOf("양양", "고성", "속초", "강릉")) {
        val tabLayout = binding.beachTabLayout

        tabItems.forEach { beachName ->
            tabLayout.addTab(tabLayout.newTab().setText(beachName))
        }
    }

    fun setOnTabSelectedListener(listener: OnTabSelectedListener) {
        tabSelectedListener = listener

        binding.beachTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.let {
                    tabSelectedListener?.onTabSelected(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}
