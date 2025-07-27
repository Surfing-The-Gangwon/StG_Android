package com.capstone.surfingthegangwon.presentation.together

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.time.DayOfWeek
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.capstone.surfingthegangwon.core.ui.CustomHeaderView
import com.capstone.surfingthegangwon.presentation.together.databinding.FragmentTogetherBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import java.time.LocalDate

class TogetherFragment : Fragment() {
    private lateinit var binding: FragmentTogetherBinding

    private lateinit var weekTitle: TextView
    private lateinit var weekAdapter: WeekAdapter
    private lateinit var areaAdapter: AreaAdapter

    private var baseDate: LocalDate = LocalDate.now()

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
        setClickListners()
        setRecyclerViews()
        updateWeek()
    }

    /**
     * 클릭리스너를 세팅하는 메소드
     */
    private fun setClickListners() {
        binding.btnPrevWeek.setOnClickListener {
            baseDate = baseDate.minusWeeks(1)
            updateWeek()
        }

        binding.btnNextWeek.setOnClickListener {
            baseDate = baseDate.plusWeeks(1)
            updateWeek()
        }
    }

    /**
     * 리사이클러뷰를 세팅하는 메소드
     */
    private fun setRecyclerViews() {
        setAreaRcv()
        setCalenderRcv()
    }

    /**
     * 지역 리스트 리사이클러뷰를 세팅하는 메소드
     */
    private fun setAreaRcv() {
        val areaList = binding.recyclerArea

        val dummy_areas = listOf("죽도해변A", "죽도해변B", "죽도해변C", "죽도해변D")

        areaAdapter = AreaAdapter(dummy_areas)
        areaList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        areaList.adapter = areaAdapter
    }

    /**
     * 주간 캘린더를 세팅하는 메소드
     */
    private fun setCalenderRcv() {
        val calenderWeek = binding.recyclerWeek
        weekTitle = binding.weekTitle

        weekAdapter = WeekAdapter { clickedDate ->
            weekTitle.text =
                "${clickedDate.year}년 ${clickedDate.monthValue}월 ${clickedDate.dayOfMonth}일"
        }
        val layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.CENTER
        }
        calenderWeek.layoutManager = layoutManager
        calenderWeek.adapter = weekAdapter
    }

    /**
     * 기준 날짜의 주(일요일부터 토요일까지) 날짜를 화면에 표시하고, 리스트(리사이클러뷰 등)를 업데이트하는 메소드
     */
    private fun updateWeek() {
        val weekDates = getWeekDates(baseDate)
        weekTitle.text = "${baseDate.year}년 ${baseDate.monthValue}월 ${baseDate.dayOfMonth}일"
        weekAdapter.submitList(weekDates)
    }

    /**
     * 특정 날짜가 속한 주의 일요일부터 시작해서 토요일까지 날짜 리스트를 돌려주는 메소드
     */
    private fun getWeekDates(date: LocalDate): List<LocalDate> {
        val sunday = date.with(DayOfWeek.SUNDAY)
        return (0..6).map { sunday.plusDays(it.toLong()) }
    }

    /**
     * 헤더 뷰를 세팅하는 메소드
     */
    private fun setHeaderView() {
        binding.headerView.setBeachTabItem()
        binding.headerView.setScreenTitle("같이 타기")
        binding.headerView.setOnTabSelectedListener(object :
            CustomHeaderView.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                // 탭이 선택 되었을 때의 동작 정의 (예시코드)
                var place = ""
                if (position == 0) {
                    place = "양양"
                } else if (position == 1) {
                    place = "고성"
                } else if (position == 2) {
                    place = "속초"
                } else if (position == 3) {
                    place = "강릉"
                }

                Log.d("TogetherFragment", "선택된 탭: $position, 장소: $place")
            }
        })

    }
}