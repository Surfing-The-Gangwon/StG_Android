package com.capstone.surfingthegangwon.repository

import com.capstone.surfingthegangwon.dto.HubResult

interface TourRepository {
    suspend fun getHubs(
        baseYm: String,
        areaCd: String,
        signguCd: String,
        pageNo: Int = 1,
        numOfRows: Int = 10
    ): HubResult
}