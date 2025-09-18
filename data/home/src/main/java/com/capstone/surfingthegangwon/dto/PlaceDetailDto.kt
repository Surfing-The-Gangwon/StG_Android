package com.capstone.surfingthegangwon.dto

data class PlaceDetailDto(
    val name: String,
    val address: String?,
    val phone: String?,
    val introduce: String?,
    val shopImg: List<ImageDto>
) {
    data class ImageDto(val imgUrl: String)
}