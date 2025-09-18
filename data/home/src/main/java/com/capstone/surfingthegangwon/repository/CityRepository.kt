package com.capstone.surfingthegangwon.repository

import com.capstone.surfingthegangwon.dto.City

interface CityRepository {
    suspend fun getCities(): List<City>
}