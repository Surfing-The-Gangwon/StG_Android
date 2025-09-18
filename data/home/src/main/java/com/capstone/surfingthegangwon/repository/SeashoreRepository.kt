package com.capstone.surfingthegangwon.repository

import com.capstone.surfingthegangwon.dto.ForecastDto
import com.capstone.surfingthegangwon.dto.PlaceDetailDto
import com.capstone.surfingthegangwon.dto.SeashoreDetailDto
import com.capstone.surfingthegangwon.dto.SeashoreDto
import com.capstone.surfingthegangwon.dto.SeashoreMarkerDto
import com.capstone.surfingthegangwon.dto.TideDayDto

interface SeashoreRepository {
    suspend fun getSeashores(cityId: Int): List<SeashoreDto>
    suspend fun getSeashoreDetail(seashoreId: Int): SeashoreDetailDto
    suspend fun getSeashoreMarkers(seashoreId: Int): List<SeashoreMarkerDto>
    suspend fun getSeashoreUV(seashoreId: Int): String
    suspend fun getForecasts(seashoreId: Int): List<ForecastDto>
    suspend fun getTides(seashoreId: Int): List<TideDayDto>
}