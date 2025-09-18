package com.capstone.surfingthegangwon.repositoryImpl

import com.capstone.surfingthegangwon.repository.CityRepository
import com.capstone.surfingthegangwon.api.ServerApi
import com.capstone.surfingthegangwon.dto.City
import javax.inject.Inject

class CityRepositoryImpl @Inject constructor(
    private val api: ServerApi
) : CityRepository {
    override suspend fun getCities(): List<City> = api.getCities()
}