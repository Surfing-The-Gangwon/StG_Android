package com.capstone.surfingthegangwon.presentation.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.surfingthegangwon.core.ui.ColorGradient.Companion.setTextColorAsLinearGradient
import com.capstone.surfingthegangwon.presentation.login.databinding.ActivityLoginBinding
import com.capstone.surfingthegangwon.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import com.capstone.surfingthegangwon.core.resource.R as CoRes

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val baseUrl = BuildConfig.SERVICE_BASE_URL
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        intent?.data?.let(::handleRedirectUri)
        setTvLogoAsGradient()
        setClickKakaoLoginBtn()
    }

//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        intent?.data?.let(::handleRedirectUri)
//    }

    private fun handleRedirectUri(uri: Uri) {
        if (uri.scheme == "${baseUrl}" && uri.host == "oauth" && uri.path == "/kakao/callback") {
            val error = uri.getQueryParameter("error")
            if (error != null) {
                // 에러 처리
                return
            }
            val code = uri.getQueryParameter("code")
            if (!code.isNullOrBlank()) {
                loginViewModel.handleKakaoRedirectCode(code)
            }
        }
    }

    private fun setClickKakaoLoginBtn() {
        binding.kakaoLoginBtn.setOnClickListener {
            switchActivityToMain()
        }
    }

    /**
     * 메인 액티비티로 이동
     */
    private fun switchActivityToMain() {
        val intent = Intent(
            this,
            MainActivity::class.java
        )
        startActivity(intent)
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

    companion object {
        private const val TAG = "LoginActivity"
    }
}