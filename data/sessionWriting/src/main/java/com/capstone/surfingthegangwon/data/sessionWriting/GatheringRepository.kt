package com.capstone.surfingthegangwon.data.sessionWriting

import javax.inject.Inject
import retrofit2.HttpException

class GatheringRepository @Inject constructor(
    private val service: GatheringService
) {
    suspend fun create(body: CreateGatheringReq): Result<Unit> = runCatching {
        val res = service.createGathering(body)
        if (!res.isSuccessful) throw HttpException(res)
        Unit
    }
}