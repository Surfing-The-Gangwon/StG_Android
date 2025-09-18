package com.capstone.surfingthegangwon.core.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl

class TourQueryInterceptor(
    private val serviceKey: String,
    private val mobileOs: String = "AND",
    private val mobileApp: String = "SurfingTheGangwon",
    private val responseType: String = "json"
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val orig = chain.request()

        Log.d("TourAPI", "Original URL: ${orig.url}")
        Log.d("TourAPI", "Service Key: ${serviceKey}")

        val url: HttpUrl = orig.url.newBuilder()
            .addEncodedQueryParameter("serviceKey", serviceKey)
            .addQueryParameter("MobileOS", mobileOs)
            .addQueryParameter("MobileApp", mobileApp)
            .addQueryParameter("_type", responseType)
            .build()

        Log.d("TourAPI", "Final URL: $url")

        val newReq = orig.newBuilder()
            .header("Accept", "application/json")
            .url(url)
            .build()
        return chain.proceed(newReq)
    }
}