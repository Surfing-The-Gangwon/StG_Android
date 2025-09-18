package com.capstone.surfingthegangwon.api

import com.capstone.surfingthegangwon.dto.SeashoreDto
import com.capstone.surfingthegangwon.dto.City
import com.capstone.surfingthegangwon.dto.ForecastDto
import com.capstone.surfingthegangwon.dto.LessonDto
import com.capstone.surfingthegangwon.dto.PlaceDetailDto
import com.capstone.surfingthegangwon.dto.RentalDto
import com.capstone.surfingthegangwon.dto.SeashoreDetailDto
import com.capstone.surfingthegangwon.dto.SeashoreMarkerDto
import com.capstone.surfingthegangwon.dto.SurfingShopDetailDto
import com.capstone.surfingthegangwon.dto.TideDayDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ServerApi {
    @GET("api/cities")
    suspend fun getCities(): List<City>

    @GET("api/cities/{cityId}/seashores")
    suspend fun getSeashores(@Path("cityId") cityId: Int): List<SeashoreDto>

    @GET("api/seashores/{id}")
    suspend fun getSeashoreDetail(@Path("id") seashoreId: Int): SeashoreDetailDto

    @GET("api/seashores/{id}/markers")
    suspend fun getSeashoreMarkers(@Path("id") seashoreId: Int): List<SeashoreMarkerDto>

    @GET("api/seashores/{id}/uv")
    suspend fun getSeashoreUV(@Path("id") seashoreId: Int): String

    @GET("api/seashores/{id}/forecasts")
    suspend fun getForecasts(@Path("id") seashoreId: Int): List<ForecastDto>

    @GET("api/seashores/{id}/forecasts/tide")
    suspend fun getTides(@Path("id") seashoreId: Int): List<TideDayDto>

    @GET("api/surfing/{marker_id}")
    suspend fun getSurfing(@Path("marker_id") markerId: Int): SurfingShopDetailDto

    @GET("api/surfing/{marker_id}/lesson")
    suspend fun getLessons(@Path("marker_id") markerId: Int): List<LessonDto>

    @GET("api/surfing/{marker_id}/rental")
    suspend fun getRentals(@Path("marker_id") markerId: Int): List<RentalDto>
}