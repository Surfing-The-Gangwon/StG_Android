package com.capstone.surfingthegangwon

data class BeachInfo (
    val beachName: String, // 해변 이름
    val airTemp: String, // 기온
    val waterTemp: String, // 수온
    val waveHeight: String, // 파고
    val wavePeriod: String, // 파주기
    val windDirection: String, // 풍향
    val windSpeed: String, // 풍속
    val latitude: Double = 38.13105, // 위도
    val longitude: Double = 128.624284 // 경도
)