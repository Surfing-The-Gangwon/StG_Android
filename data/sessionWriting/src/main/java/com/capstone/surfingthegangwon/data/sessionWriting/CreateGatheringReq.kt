package com.capstone.surfingthegangwon.data.sessionWriting

data class CreateGatheringReq(
    val title: String,
    val contents: String,
    val phone: String,
    val maxCount: Int,
    val seashoreName: String,
    val meetingTime: String,
    val level: String
)
