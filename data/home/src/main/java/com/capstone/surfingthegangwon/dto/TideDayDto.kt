package com.capstone.surfingthegangwon.dto

data class TideDayDto(
    val tide: List<TideDto>
)
data class TideDto(
    val beachNum: String,
    val baseDate: String,
    val tiStnld: String,
    val tiTime: String,
    val tiType: String,
    val tilevel: String
)