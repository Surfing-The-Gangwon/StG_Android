package com.capstone.surfingthegangwon.presentation.main

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.CONSUMED
import androidx.core.view.updatePadding
import androidx.lifecycle.ReportFragment.Companion.reportFragment
import com.capstone.surfingthegangwon.presentation.main.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySystemBarInsetsPadding()
    }

    /**
     * 시스템 표시줄 삽입을 기준으로 지정된 보기에 하단 패딩을 적용하는 함수
     * (예: 내비게이션 바), 시스템 UI에 의해 콘텐츠가 가려지지 않도록 합니다.
     */
    private fun applySystemBarInsetsPadding() {
        val navigationView = binding.cardView
        ViewCompat.setOnApplyWindowInsetsListener(navigationView) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemInsets.bottom)
            WindowInsetsCompat.CONSUMED
        }

    }
}