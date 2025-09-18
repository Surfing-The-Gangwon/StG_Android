package com.capstone.surfingthegangwon.data.sessionWriting

import com.capstone.surfingthegangwon.core.retrofit.AuthHeaderInterceptor
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GatheringService {
    @POST("api/gathering")
    @Headers("${AuthHeaderInterceptor.HEADER_REQUIRE_AUTH}: true")
    suspend fun createGathering(
        @Body body: CreateGatheringReq
    ): Response<Unit>
}