package com.capstone.surfingthegangwon.presentation.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.capstone.surfingthegangwon.presentation.main.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavigation()
    }

    /**
     * 네비게이션 초기화
     */
    private fun initNavigation() {
        val nav = binding.bottomNavigationView
        val navController =
            binding.fragmentContainer.getFragment<NavHostFragment>().navController
        binding.bottomNavigationView.setupWithNavController(navController)
        val navigationView = binding.cardView

        setUpNaviagtion(nav, navController)
        applySystemBarInsetsPadding(navigationView)
    }

    /**
     * 네비게이션 작동 함수
     */
    private fun setUpNaviagtion(
        nav: BottomNavigationView,
        navController: NavController
    ) {
        nav.setOnItemSelectedListener {
            navController.popBackStack(navController.graph.startDestinationId, false)
            navController.navigate(it.itemId)
            true
        }
    }

    /**
     * 시스템 표시줄 삽입을 기준으로 지정된 보기에 하단 패딩을 적용하는 함수
     * (예: 내비게이션 바), 시스템 UI에 의해 콘텐츠가 가려지지 않도록 합니다.
     */
    private fun applySystemBarInsetsPadding(uiView: View) {
        ViewCompat.setOnApplyWindowInsetsListener(uiView) { view, insets ->
            val systemInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemInsets.bottom)
            WindowInsetsCompat.CONSUMED
        }

    }
}