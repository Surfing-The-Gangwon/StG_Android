package com.capstone.surfingthegangwon.domain.together.model

import com.capstone.surfingthegangwon.core.model.Grade
import com.capstone.surfingthegangwon.core.model.PostAction

data class SessionItem(
    val id: Int,
    val title: String,
    val contents: String,
    val sessionTime: String,
    val sessionDate: String,
    val participants: String,
    val phoneNumber: String,
    val grade: Grade,
    val action: PostAction
)
