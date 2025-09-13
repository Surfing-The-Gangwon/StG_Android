package com.capstone.surfingthegangwon.core.retrofit

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class DebugResponseInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        val responseBody = response.body
        if (responseBody != null) {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE)
            val buffer = source.buffer
            val responseBodyString = buffer.clone().readUtf8()

            Log.d("API_RESPONSE", "Response: $responseBodyString")
            Log.d("API_RESPONSE", "Content-Type: ${response.header("Content-Type")}")
        }

        return response
    }
}