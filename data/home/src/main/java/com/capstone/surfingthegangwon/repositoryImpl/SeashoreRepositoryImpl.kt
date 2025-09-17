package com.capstone.surfingthegangwon.repositoryImpl

import com.capstone.surfingthegangwon.api.ServerApi
import com.capstone.surfingthegangwon.dto.SeashoreDto
import com.capstone.surfingthegangwon.repository.SeashoreRepository
import javax.inject.Inject

class SeashoreRepositoryImpl @Inject constructor(
    private val api: ServerApi
) : SeashoreRepository {
    override suspend fun getSeashores(cityId: Int): List<SeashoreDto> =
        api.getSeashores(cityId)
}