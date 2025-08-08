package com.capstone.surfingthegangwon.presentation.session

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.surfingthegangwon.presentation.session.databinding.ActivitySessionReadingBinding

class SessionReadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySessionReadingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionReadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setTitle(getString(R.string.together))
        binding.topAppBar.setOnBackClick {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}