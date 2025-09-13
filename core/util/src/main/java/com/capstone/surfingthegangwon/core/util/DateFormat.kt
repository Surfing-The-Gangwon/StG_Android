package com.capstone.surfingthegangwon.core.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun String.toHourMinute(): String {
    return try {
        val parsed = LocalDateTime.parse(this) // "2025-09-03T18:00:00"
        parsed.format(DateTimeFormatter.ofPattern("HH:mm")) // "18:00"
    } catch (e: Exception) {
        this // 파싱 실패 시 원본 리턴
    }
}
