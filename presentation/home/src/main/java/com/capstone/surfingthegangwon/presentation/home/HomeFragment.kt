package com.capstone.surfingthegangwon.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.surfingthegangwon.BeachInfo
import com.capstone.surfingthegangwon.core.ui.CustomHeaderView
import com.capstone.surfingthegangwon.presentation.home.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    private val beachAdapter by lazy {
        BeachInfoAdapter { beach -> Toast.makeText(requireContext(), "놀러오세요 ${beach.beachName}으로!", Toast.LENGTH_SHORT).show() }
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
    }

    private fun setHeaderView() {
        val beachMenu = listOf("양양", "고성", "속초", "강릉")

        binding.headerView.setScreenTitle("파도 정보")
        binding.headerView.setBeachTabItem(beachMenu)
        binding.headerView.setOnTabSelectedListener(object : CustomHeaderView.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                viewModel.setBeachData(beachMenu[position])
            }
        })

        viewModel.setBeachData(beachMenu[0]) // 초기 데이터 설정
        binding.headerView.adjustIndicatorWidth()
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

    private fun navigateToMap(beach: BeachInfo) {
        val args = bundleOf("beachName" to beach.beachName)
        findNavController().navigate(com.capstone.surfingthegangwon.core.navigation.R.id.action_home_to_map, args)
    }

}