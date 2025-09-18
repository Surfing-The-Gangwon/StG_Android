package com.capstone.surfingthegangwon.dto

data class SurfingShopDetailDto(
    val name: String,
    val address: String?,
    val phone: String?,
    val introduce: String?,
    val shopImg: List<ShopImageDto> = emptyList()
)
data class ShopImageDto(val imgUrl: String)