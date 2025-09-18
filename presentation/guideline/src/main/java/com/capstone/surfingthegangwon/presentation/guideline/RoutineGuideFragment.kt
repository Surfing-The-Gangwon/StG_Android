package com.capstone.surfingthegangwon.presentation.guideline

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.surfingthegangwon.presentation.guideline.databinding.FragmentRoutineGuideBinding

class RoutineGuideFragment : Fragment() {
    private lateinit var binding: FragmentRoutineGuideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoutineGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

}