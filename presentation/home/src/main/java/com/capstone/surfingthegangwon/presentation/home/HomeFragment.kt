package com.capstone.surfingthegangwon.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.surfingthegangwon.core.ui.CustomHeaderView
import com.capstone.surfingthegangwon.presentation.home.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val beachAdapter = BeachInfoAdapter()

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

        binding.headerView.setScreenTitle("파도정보")
        binding.headerView.setBeachTabItem(beachMenu)
        binding.headerView.setOnTabSelectedListener(object : CustomHeaderView.OnTabSelectedListener {
            override fun onTabSelected(position: Int) {
                viewModel.setBeachData(beachMenu[position])
            }
        })
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
}