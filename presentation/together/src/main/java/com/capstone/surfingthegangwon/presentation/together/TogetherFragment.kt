package com.capstone.surfingthegangwon.presentation.together

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
}