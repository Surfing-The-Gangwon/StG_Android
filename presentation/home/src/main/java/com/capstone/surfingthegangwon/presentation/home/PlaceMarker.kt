package com.capstone.surfingthegangwon.presentation.home

data class PlaceMarker(
    val id: Int? = null,
    val name: String,
    val lat: Double,
    val lng: Double,
    val category: Category
)