package com.capstone.surfingthegangwon.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SeashoreDetailArg(
    val name: String,
    val address: String?,
    val telephone: String?,
    val lat: Double,
    val lng: Double
) : Parcelable