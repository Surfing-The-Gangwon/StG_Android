package com.capstone.surfingthegangwon.dto

data class HubResult(
    val total: Int,
    val pageNo: Int,
    val numOfRows: Int,
    val items: List<HubPlace>
)

data class HubPlace(
    val name: String,           // hubTatsNm
    val categoryMid: String?,   // hubCtgryMclsNm
    val lat: Double?,           // mapY
    val lng: Double?            // mapX
)
