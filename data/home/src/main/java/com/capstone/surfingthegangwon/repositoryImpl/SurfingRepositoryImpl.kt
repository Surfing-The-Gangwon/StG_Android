package com.capstone.surfingthegangwon.repositoryImpl

import com.capstone.surfingthegangwon.api.ServerApi
import com.capstone.surfingthegangwon.repository.SurfingRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SurfingRepositoryImpl @Inject constructor(
    private val api: ServerApi
) : SurfingRepository {

    override suspend fun fetch(markerId: Int): SurfingRepository.Bundle = coroutineScope {
        val d = async { api.getSurfing(markerId) }
        val l = async { api.getLessons(markerId) }
        val r = async { api.getRentals(markerId) }
        SurfingRepository.Bundle(
            detail = d.await(),
            lessons = l.await(),
            rentals = r.await()
        )
    }
}