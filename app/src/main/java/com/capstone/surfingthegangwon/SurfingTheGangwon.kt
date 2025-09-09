package com.capstone.surfingthegangwon

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.kakao.vectormap.KakaoMapSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SurfingTheGangwon: Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d("Kakao_Key_Hash", Utility.getKeyHash(this))

        KakaoSdk.init(this, BuildConfig.KAKAO_API_KEY)
        KakaoMapSdk.init(this, BuildConfig.KAKAO_API_KEY)
    }
}