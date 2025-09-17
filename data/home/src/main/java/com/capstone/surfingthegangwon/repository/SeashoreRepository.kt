package com.capstone.surfingthegangwon.repository

import com.capstone.surfingthegangwon.dto.SeashoreDto

interface SeashoreRepository {
    suspend fun getSeashores(cityId: Int): List<SeashoreDto>
}