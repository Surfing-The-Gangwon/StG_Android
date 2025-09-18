package com.capstone.surfingthegangwon.dto

import com.google.gson.annotations.SerializedName

data class TourResponse(
    @SerializedName("response") val response: Response?
) {
    data class Response(
        @SerializedName("header") val header: Header?,
        @SerializedName("body") val body: Body?
    )

    data class Header(
        @SerializedName("resultCode") val resultCode: String?,
        @SerializedName("resultMsg") val resultMsg: String?
    )

    data class Body(
        @SerializedName("items") val items: Items?,
        @SerializedName("numOfRows") val numOfRows: Int?,
        @SerializedName("pageNo") val pageNo: Int?,
        @SerializedName("totalCount") val totalCount: Int?
    )

    data class Items(
        @SerializedName("item") val item: List<RelatedItem> = emptyList()
    )

    data class RelatedItem(
        @SerializedName("mapX") val mapX: String?,              // 경도(lng)
        @SerializedName("mapY") val mapY: String?,              // 위도(lat)
        @SerializedName("hubTatsNm") val hubTatsNm: String?,    // 명칭
        @SerializedName("hubCtgryMclsNm") val hubCtgryMclsNm: String? // 중분류
    )
}

