package com.capstone.surfingthegangwon.data.sessionstatus.mapper

import com.capstone.surfingthegangwon.core.model.Grade
import com.capstone.surfingthegangwon.core.model.SessionState
import com.capstone.surfingthegangwon.core.util.formatIsoToKoreanDate
import com.capstone.surfingthegangwon.core.util.toHourMinute
import com.capstone.surfingthegangwon.data.sessionstatus.dto.WrittenPostRes
import com.capstone.surfingthegangwon.data.sessionstatus.dto.WrittenPostResItem
import com.capstone.surfingthegangwon.domain.sessionstatus.SessionItem

fun WrittenPostResItem.toSessionItem(): SessionItem =
    SessionItem(
        id = id,
        title = title,
        contents = contents,
        sessionTime = meetingTime.toHourMinute(),
        sessionDate = formatIsoToKoreanDate(meetingTime),
        participants = "$currentCount/$maxCount",
        phoneNumber = phone,
        grade = Grade.from(level),
//        action = PostAction.from(postAction),
        state = SessionState.from(state)
    )

fun WrittenPostRes.toSessionItems(): List<SessionItem> = map { it.toSessionItem() }
