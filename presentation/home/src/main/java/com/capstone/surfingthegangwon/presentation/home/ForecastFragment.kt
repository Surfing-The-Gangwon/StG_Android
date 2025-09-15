package com.capstone.surfingthegangwon.presentation.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.surfingthegangwon.presentation.home.databinding.FragmentForecastBinding

class ForecastFragment : Fragment() {

    private lateinit var binding: FragmentForecastBinding

    private val viewModel: ForecastViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }
}