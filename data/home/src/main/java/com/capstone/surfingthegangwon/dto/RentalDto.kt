package com.capstone.surfingthegangwon.dto

data class RentalDto(
    val name: String,
    val rentalTime: Int?,
    val originalPrice: Int?,
    val discountedPrice: Int?
)