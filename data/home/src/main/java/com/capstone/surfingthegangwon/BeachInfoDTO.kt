package com.capstone.surfingthegangwon

data class BeachInfoDTO(
    val beachName: String, // 해변 이름
    val airTemp: Float, // 기온
    val waterTemp: Float, // 수온
    val waveHeight: Float, // 파고
    val wavePeriod: Float, // 파주기
    val windDirection: String, // 풍향
    val windSpeed: Float, // 풍속
    val latitude: Double = 38.13105, // 위도
    val longitude: Double = 128.624284 // 경도
)
