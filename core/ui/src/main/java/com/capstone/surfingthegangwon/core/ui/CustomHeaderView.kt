package com.capstone.surfingthegangwon.core.ui

import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.capstone.surfingthegangwon.core.ui.databinding.HeaderBinding
import com.google.android.material.tabs.TabLayout

class CustomHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val binding = HeaderBinding.inflate(LayoutInflater.from(context), this, true)
    private var tabSelectedListener: OnTabSelectedListener? = null

    interface OnTabSelectedListener {
        fun onTabSelected(position: Int)
    }

    init {
        setTvLogoAsGradient()
    }

    fun setScreenTitle(title: String) {
        binding.tvScreenTitle.text = title
    }

    fun setBeachTabItem(tabItems: List<String> = listOf("양양", "고성", "속초", "강릉")) {
        val tabLayout = binding.beachTabLayout

        tabItems.forEach { beachName ->
            tabLayout.addTab(tabLayout.newTab().setText(beachName))
        }
    }

    fun setOnTabSelectedListener(listener: OnTabSelectedListener) {
        tabSelectedListener = listener

        binding.beachTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.let {
                    tabSelectedListener?.onTabSelected(it.position)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * 인디케이터의 넓이를 조정함
     */
    fun adjustIndicatorWidth(ratio: Float = 0.7f) {
        val tabLayout = binding.beachTabLayout
        val tabStrip = tabLayout.getChildAt(0) as? ViewGroup ?: return

        tabLayout.post {
            for (i in 0 until tabStrip.childCount) {
                val tabView = tabStrip.getChildAt(i)

                // 원래 너비 가져오기
                val originalWidth = tabView.width
                val newWidth = (originalWidth * ratio).toInt()

                // 가운데 정렬하려면 margin 조절
                val margin = (originalWidth - newWidth) / 2

                val params = tabView.layoutParams as MarginLayoutParams
                params.width = newWidth
                params.marginStart = margin
                params.marginEnd = margin
                tabView.layoutParams = params

                tabView.invalidate()
            }
        }
    }

    /**
     * 로고 텍스트에 그라데이션 적용
     */
    private fun setTvLogoAsGradient() {
        val colors = arrayOf(
            ContextCompat.getColor(context, R.color.logo_start_color),
            ContextCompat.getColor(context, R.color.logo_end_color)
        )
        binding.appTitle.setTextColorAsLinearGradient(colors)
    }

    /**
     * TextView의 텍스트 색상을 지정한 색상 배열을 이용해
     * 선형 그라데이션(LinearGradient) 효과로 변경하는 확장 함수입니다.
     */
    private fun TextView.setTextColorAsLinearGradient(colors: Array<Int>) {
        // 색상 배열이 비어 있으면 처리하지 않고 종료
        if (colors.isEmpty()) {
            return
        }

        // 기본 텍스트 색상을 첫 번째 색상으로 설정
        setTextColor(colors[0])

        // 텍스트에 그라데이션 효과를 적용하는 LinearGradient 생성
        this.paint.shader = LinearGradient(
            0f, // 그라데이션 시작 X 좌표 (텍스트 시작점)
            0f, // 그라데이션 시작 Y 좌표 (텍스트 위쪽)
            paint.measureText(this.text.toString()), // 그라데이션 끝 X 좌표 (텍스트 길이만큼)
            this.textSize, // 그라데이션 끝 Y 좌표 (텍스트 크기만큼)
            colors.toIntArray(), // 그라데이션에 사용할 색상 배열 (IntArray 형태)
            arrayOf(0f, 1f).toFloatArray(), // 색상 위치 배열 (0%에서 100%까지)
            Shader.TileMode.CLAMP // 경계 밖의 영역은 가장자리 색상으로 고정
        )
    }
}
