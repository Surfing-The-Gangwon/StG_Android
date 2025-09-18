package com.capstone.surfingthegangwon.dto

import com.capstone.surfingthegangwon.BeachInfo

fun BeachInfoDTO.toDomain(): BeachInfo {
    return BeachInfo(
        seashoreId = this.seashoreId,
        beachName = this.beachName,
        airTemp = this.airTemp,
        waterTemp = this.waterTemp,
        waveHeight = this.waveHeight,
        wavePeriod = this.wavePeriod,
        windDirection = this.windDirection,
        windSpeed = this.windSpeed
    )
}