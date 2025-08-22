package com.capstone.surfingthegangwon.presentation.sessionwriting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.surfingthegangwon.presentation.sessionwriting.databinding.ActivitySessionWritingBinding

class SessionWritingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySessionWritingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySessionWritingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setTitle(getString(R.string.create_new_session))
        binding.topAppBar.setOnBackClick {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}