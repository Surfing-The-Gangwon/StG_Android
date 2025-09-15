package com.capstone.surfingthegangwon.presentation.guideline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.surfingthegangwon.presentation.guideline.databinding.FragmentWaveTermsBinding

class WaveTermsFragment : Fragment() {
    private lateinit var binding: FragmentWaveTermsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWaveTermsBinding.inflate(inflater, container, false)
        return binding.root
    }
}