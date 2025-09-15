package com.capstone.surfingthegangwon.presentation.guideline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.surfingthegangwon.presentation.guideline.databinding.FragmentGuideContainerBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.capstone.surfingthegangwon.core.ui.R as CoreUiR


class GuideContainerFragment : Fragment() {
    private lateinit var binding: FragmentGuideContainerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuideContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.headerView.setScreenTitle("가이드라인")         // 헤더 타이틀

        binding.viewPager.adapter = GuidePagerAdapter(this) // ViewPager2 어댑터

        val headerTabLayout = binding.headerView.findViewById<TabLayout>(CoreUiR.id.beachTabLayout) // 헤더 안의 TabLayout 참조

        val titles = listOf("안전가이드", "입수 전 루틴", "파도용어")

        // TabLayout ↔ ViewPager2 동기화
        TabLayoutMediator(headerTabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()

    }
}