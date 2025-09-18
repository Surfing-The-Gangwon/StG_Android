package com.capstone.surfingthegangwon.dto

import com.capstone.surfingthegangwon.BeachInfo

private fun String.dropSuffixIfAny(suffix: String) =
    if (this.uppercase().endsWith(suffix.uppercase())) this.dropLast(suffix.length) else this

fun SeashoreDto.toBeachInfo(): BeachInfo {
    return BeachInfo(
        seashoreId = id,
        beachName = name,
        airTemp = temp,
        waterTemp = waterTemp,
        waveHeight = waveHeight,
        wavePeriod = wavePeriod,
        windDirection = windDir,
        windSpeed = windSpeed
    )
}