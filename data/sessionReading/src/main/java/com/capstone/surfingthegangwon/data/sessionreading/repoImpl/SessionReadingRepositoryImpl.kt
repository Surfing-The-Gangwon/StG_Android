package com.capstone.surfingthegangwon.data.sessionreading.repoImpl

import com.capstone.surfingthegangwon.core.model.SessionItem
import com.capstone.surfingthegangwon.data.sessionreading.api.SessionReadingRetrofitService
import javax.inject.Inject

class SessionReadingRepositoryImpl @Inject constructor(
    private val readingApi: SessionReadingRetrofitService
) {

    suspend fun joinPost(gatheringId: Int): Result<Unit> {
        return runCatching {
            readingApi.joinPost(gatheringId)
        }
    }

    suspend fun closePost(gatheringId: Int): Result<Unit> {
        return runCatching {
            readingApi.closePost(gatheringId)
        }
    }

    suspend fun cancelPost(gatheringId: Int): Result<Unit> {
        return runCatching {
            readingApi.cancelPost(gatheringId)
        }
    }

    suspend fun deletePost(gatheringId: Int): Result<Unit> {
        return runCatching {
            readingApi.deletePost(gatheringId)
        }
    }
}