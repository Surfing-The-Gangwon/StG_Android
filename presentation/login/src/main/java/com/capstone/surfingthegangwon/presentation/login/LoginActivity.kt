package com.capstone.surfingthegangwon.presentation.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.surfingthegangwon.core.ui.ColorGradient.Companion.setTextColorAsLinearGradient
import com.capstone.surfingthegangwon.presentation.login.databinding.ActivityLoginBinding
import com.capstone.surfingthegangwon.presentation.main.MainActivity
import com.capstone.surfingthegangwon.core.resource.R as CoRes

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTvLogoAsGradient()
        setClickKakaoLoginBtn()
    }

    private fun setClickKakaoLoginBtn() {
        binding.kakaoLoginBtn.setOnClickListener {
            val intent = Intent(
                this,
                MainActivity::class.java
            )
            startActivity(intent)
        }
    }

    /**
     * 로고 텍스트에 그라데이션 적용
     */
    private fun setTvLogoAsGradient() {
        val colors = arrayOf(
            ContextCompat.getColor(this, CoRes.color.logo_start_color),
            ContextCompat.getColor(this, CoRes.color.logo_end_color)
        )
        binding.titleSurfing.setTextColorAsLinearGradient(colors)
        binding.titleThe.setTextColorAsLinearGradient(colors)
        binding.titleGangwon.setTextColorAsLinearGradient(colors)
    }
}