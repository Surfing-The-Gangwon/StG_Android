package com.capstone.surfingthegangwon.api

import com.capstone.surfingthegangwon.dto.TourResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TourApiService {
    @GET("areaBasedList1")
    suspend fun getRelatedTourList(
        @Query("pageNo") pageNo: Int = 1,
        @Query("numOfRows") numOfRows: Int = 20,
        @Query("baseYm") baseYm: String = "202509",
        @Query("areaCd") areaCd: String = "51",
        @Query("signguCd") signguCd: String = "51150"
    ): TourResponse
}