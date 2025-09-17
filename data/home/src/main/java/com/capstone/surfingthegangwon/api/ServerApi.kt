package com.capstone.surfingthegangwon.api

import com.capstone.surfingthegangwon.dto.SeashoreDto
import com.capstone.surfingthegangwon.dto.City
import retrofit2.http.GET
import retrofit2.http.Path

interface ServerApi {
    @GET("api/cities")
    suspend fun getCities(): List<City>

    @GET("api/cities/{cityId}/seashores")
    suspend fun getSeashores(@Path("cityId") cityId: Int): List<SeashoreDto>
}