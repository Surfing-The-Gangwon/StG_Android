package com.capstone.surfingthegangwon.data.sessionstatus.mapper

import com.capstone.surfingthegangwon.core.model.Grade
import com.capstone.surfingthegangwon.core.model.SessionState
import com.capstone.surfingthegangwon.core.util.toHourMinute
import com.capstone.surfingthegangwon.data.sessionstatus.dto.ReservedPostRes
import com.capstone.surfingthegangwon.data.sessionstatus.dto.ReservedPostResItem
import com.capstone.surfingthegangwon.domain.sessionstatus.SessionItem

fun ReservedPostResItem.toSessionItem(): SessionItem =
    SessionItem(
        id = id,
        title = title,
        sessionTime = meetingTime.toHourMinute(),
        participants = "$currentCount/$maxCount",
        grade = Grade.from(level),
        state = SessionState.from(state)
    )

fun ReservedPostRes.toSessionItems(): List<SessionItem> = map { it.toSessionItem() }