package com.capstone.surfingthegangwon.domain.sessionstatus

import com.capstone.surfingthegangwon.core.model.Grade

data class SessionItem(
    val id: Int,
    val title: String,
    val sessionTime: String,
    val participants: String,
    val grade: Grade,
    val state: SessionState
)
