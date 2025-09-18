package com.capstone.surfingthegangwon.dto

data class LessonDto(
    val title: String,
    val contents: String?,
    val classTime: Int?,
    val originalPrice: Int?,
    val discountedPrice: Int?
)