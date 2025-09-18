package com.capstone.surfingthegangwon.repository

import com.capstone.surfingthegangwon.dto.LessonDto
import com.capstone.surfingthegangwon.dto.RentalDto
import com.capstone.surfingthegangwon.dto.SurfingShopDetailDto

interface SurfingRepository {
    data class Bundle(
        val detail: SurfingShopDetailDto,
        val lessons: List<LessonDto>,
        val rentals: List<RentalDto>
    )
    suspend fun fetch(markerId: Int): Bundle
}