package com.capstone.surfingthegangwon

fun BeachInfoDTO.toDomain(): BeachInfo {
    return BeachInfo(
        beachName = this.beachName,
        airTemp = this.airTemp,
        waterTemp = this.waterTemp,
        waveHeight = this.waveHeight,
        wavePeriod = this.wavePeriod,
        windDirection = this.windDirection,
        windSpeed = this.windSpeed
    )
}

/**
 * 외부에서 불러온 API에서 전처리가 필요하다면
 * 여기에(Mapper) 추가적인 변환 로직을 작성할 수 있음.
 */