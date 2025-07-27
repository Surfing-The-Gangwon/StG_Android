package com.capstone.surfingthegangwon.presentation.together

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.capstone.surfingthegangwon.core.ui.CustomHeaderView
import com.capstone.surfingthegangwon.presentation.together.databinding.FragmentTogetherBinding

class TogetherFragment : Fragment() {
    private lateinit var binding: FragmentTogetherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTogetherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHeaderView()
    }

    private fun setHeaderView() {
        binding.headerView.setBeachTabItem()
        binding.headerView.setScreenTitle("같이타기")
        binding.headerView.setOnTabSelectedListener(object : CustomHeaderView.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                // 탭이 선택 되었을 때의 동작 정의 (예시코드)
                var place = ""
                if (position == 0) { place = "양양" }
                else if (position == 1) { place = "고성" }
                else if (position == 2) { place = "속초" }
                else if (position == 3) { place = "강릉" }

                Log.d("TogetherFragment", "선택된 탭: $position, 장소: $place")
            }
        })

    }
}