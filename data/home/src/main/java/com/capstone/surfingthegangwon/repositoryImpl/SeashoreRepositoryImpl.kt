package com.capstone.surfingthegangwon.repositoryImpl

import com.capstone.surfingthegangwon.api.ServerApi
import com.capstone.surfingthegangwon.dto.LessonDto
import com.capstone.surfingthegangwon.dto.RentalDto
import com.capstone.surfingthegangwon.dto.SeashoreDto
import com.capstone.surfingthegangwon.dto.SurfingShopDetailDto
import com.capstone.surfingthegangwon.repository.SeashoreRepository
import javax.inject.Inject

class SeashoreRepositoryImpl @Inject constructor(
    private val api: ServerApi
) : SeashoreRepository {
    override suspend fun getSeashores(cityId: Int): List<SeashoreDto> =
        api.getSeashores(cityId)

    override suspend fun getSeashoreDetail(seashoreId: Int) =
        api.getSeashoreDetail(seashoreId)

    override suspend fun getSeashoreMarkers(seashoreId: Int) =
        api.getSeashoreMarkers(seashoreId)

    override suspend fun getSeashoreUV(seashoreId: Int) =
        api.getSeashoreUV(seashoreId)

    override suspend fun getForecasts(seashoreId: Int) =
        api.getForecasts(seashoreId)

    override suspend fun getTides(seashoreId: Int) =
        api.getTides(seashoreId)
}