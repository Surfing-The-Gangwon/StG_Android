package com.capstone.surfingthegangwon.presentation.guideline

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class GuidePagerAdapter(parent: Fragment) : FragmentStateAdapter(parent) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> SafetyGuideFragment()
        1 -> RoutineGuideFragment()
        else -> WaveTermsFragment()
    }
}