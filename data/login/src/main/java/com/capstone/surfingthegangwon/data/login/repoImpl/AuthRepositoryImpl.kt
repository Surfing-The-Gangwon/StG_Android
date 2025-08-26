package com.capstone.surfingthegangwon.data.login.repoImpl

import com.capstone.surfingthegangwon.data.login.api.ServerAuthRetrofitService
import com.capstone.surfingthegangwon.data.login.mapper.toDomain
import com.capstone.surfingthegangwon.domain.login.model.TokenPair
import com.capstone.surfingthegangwon.domain.login.repository.AuthRepository

class AuthRepositoryImpl(
    private val api: ServerAuthRetrofitService
) : AuthRepository {

    override suspend fun exchangeKakaoCode(code: String): TokenPair {
        val dto = api.exchangeByGet(code)
        return dto.toDomain()
    }
}