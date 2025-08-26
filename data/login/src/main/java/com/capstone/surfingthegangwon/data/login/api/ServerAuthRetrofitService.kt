package com.capstone.surfingthegangwon.data.login.api

import com.capstone.surfingthegangwon.data.login.dto.TokenRes
import retrofit2.http.POST
import retrofit2.http.Query

interface ServerAuthRetrofitService {

    @POST("/kakao/callback")
    suspend fun exchangeByGet(@Query("code") code: String): TokenRes
}