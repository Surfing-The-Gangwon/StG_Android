package com.capstone.surfingthegangwon.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.surfingthegangwon.BeachInfo
import com.capstone.surfingthegangwon.core.ui.CustomHeaderView
import com.capstone.surfingthegangwon.dto.SeashoreDetailArg
import com.capstone.surfingthegangwon.presentation.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private var selectedSeashoreId: Int = -1
    private var isTabsInitialized = false

    private val beachAdapter by lazy {
        BeachInfoAdapter { beach -> navigateToMap(beach) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHeaderView()
        initRecyclerView()
        observeBeachInfoList()
        observeCitiesAndTabs()
        observeDetailForNav()

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            setLoading(isLoading) // 로딩 상태에 따라 UI 업데이트
        }
        if (!isTabsInitialized) {
            viewModel.loadCities()
        }
    }

    private fun observeDetailForNav() {
        viewModel.detailForNav.observe(viewLifecycleOwner) { d ->
            if (d == null) return@observe

            val cityId = viewModel.currentCityIdOrNull() ?: 1
            val detailArg = SeashoreDetailArg(
                name = d.name,
                address = d.address,
                telephone = d.telephone,
                lat = d.latitude,
                lng = d.longitude
            )

            val action = HomeFragmentDirections.actionHomeToMap(
                cityId     = cityId,
                seashoreId = selectedSeashoreId,
                detail     = detailArg
            )
            findNavController().navigate(action)
            viewModel.consumeDetailForNav()
        }
    }

    private fun setHeaderView() {
        binding.headerView.setScreenTitle("파도 정보")

//        val beachMenu = listOf("양양", "고성", "속초", "강릉")
//        binding.headerView.setBeachTabItem(beachMenu)
//        binding.headerView.setOnTabSelectedListener(object : CustomHeaderView.OnTabSelectedListener {
//            override fun onTabSelected(position: Int) {
//                viewModel.setBeachData(beachMenu[position])
//            }
//        })
//
//        viewModel.setBeachData(beachMenu[0]) // 초기 데이터 설정
//        binding.headerView.adjustIndicatorWidth()
    }

    private fun initRecyclerView() {
        binding.beachRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beachAdapter
        }
    }

    private fun observeBeachInfoList() {
        viewModel.beachWaveList.observe(viewLifecycleOwner) { list ->
            beachAdapter.submitList(list)
        }
    }

    private fun observeCitiesAndTabs() {
        viewModel.tabTitles.observe(viewLifecycleOwner) { titles ->
            if (titles.isNullOrEmpty() || isTabsInitialized) return@observe

            binding.headerView.setBeachTabItem(titles)
            binding.headerView.setOnTabSelectedListener(object : CustomHeaderView.OnTabSelectedListener {
                override fun onTabSelected(position: Int) {
                    if (viewModel.loading.value == true) return // 로딩 중이면 무시 -> 중복요청 방지

                    // 선택된 도시명으로 파도 데이터 요청
                    val selected = titles.getOrNull(position) ?: return
                    viewModel.setBeachData(selected)
                }
            })

            binding.headerView.adjustIndicatorWidth()
            isTabsInitialized = true
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (!msg.isNullOrBlank()) {
                // Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setLoading(show: Boolean) {
        if (show) {
            binding.loadingOverlay.isVisible = true
            binding.progressLottie.playAnimation()
        } else {
            binding.progressLottie.cancelAnimation()
            binding.loadingOverlay.isVisible = false
        }
        binding.beachRv.isEnabled = !show
        binding.headerView.isEnabled = !show
    }

    private fun navigateToMap(beach: BeachInfo) {
        selectedSeashoreId = beach.seashoreId
        viewModel.loadSeashoreDetailForNav(beach.seashoreId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isTabsInitialized = false
    }
}