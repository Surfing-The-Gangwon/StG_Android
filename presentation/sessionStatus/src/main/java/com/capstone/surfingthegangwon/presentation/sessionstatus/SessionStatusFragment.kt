package com.capstone.surfingthegangwon.presentation.sessionstatus

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.surfingthegangwon.presentation.sessionstatus.databinding.FragmentSessionStatusBinding

class SessionStatusFragment : Fragment() {
    private lateinit var binding: FragmentSessionStatusBinding

    private lateinit var sessionAdapter: SessionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.topAppBar.setTitle(getString(R.string.recruitment_status))
        binding.topAppBar.setOnBackClick {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /** 세션 RecyclerView 설정 */
    private fun setupSessionRecyclerView() {
        sessionAdapter = SessionAdapter()
        binding.sessionRcv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sessionAdapter
        }
    }

    companion object {
    }
}