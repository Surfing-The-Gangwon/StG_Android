package com.capstone.surfingthegangwon.presentation.sessionwriting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.surfingthegangwon.presentation.sessionwriting.databinding.ActivitySessionWritingBinding

class SessionWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySessionWritingBinding

    private lateinit var regionAdapter: RegionAdapter
    private lateinit var beachAdapter: BeachAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setTitle(getString(R.string.create_new_session))
        binding.topAppBar.setOnBackClick {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun initUi() {
        setupRecyclerViews()
    }

    /** 모든 리사이클러 뷰 초기화 */
    private fun setupRecyclerViews() {
        setupRegionRecyclerView()
        setupBeachesRecyclerView()
    }

    /**
     * 지역 리사이클러 뷰 설정
     */
    private fun setupRegionRecyclerView() {
        val dummyRegions =
            listOf("양양", "고성", "속초", "강릉", "양양", "고성", "속초", "강릉")

        regionAdapter = RegionAdapter(dummyRegions)
        binding.regionsRcv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = regionAdapter
        }
    }

    /**
     * 해변 리사이클러 뷰 설정
     */
    private fun setupBeachesRecyclerView() {
        val dummyBeaches =
            listOf("죽도해변A", "죽도해변B", "죽도해변C", "죽도해변D", "죽도해변A", "죽도해변B", "죽도해변C", "죽도해변D")

        beachAdapter = BeachAdapter(dummyBeaches)
        binding.regionsRcv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = beachAdapter
        }
    }

    private fun setupCalendar() {

    }

    private fun setupTime() {

    }
}