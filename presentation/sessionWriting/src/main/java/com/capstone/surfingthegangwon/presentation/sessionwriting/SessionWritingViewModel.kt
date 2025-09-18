package com.capstone.surfingthegangwon.presentation.sessionwriting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.surfingthegangwon.data.sessionWriting.CreateGatheringReq
import com.capstone.surfingthegangwon.data.sessionWriting.GatheringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SessionWritingViewModel @Inject constructor(
    private val repo: GatheringRepository
) : ViewModel() {
    data class UiState(
        val loading: Boolean = false,
        val success: Boolean = false,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    // 폼 상태 (Fragment에서 set)
    var selectedDate: LocalDate? = null
    var selectedTime: LocalTime? = null

    fun create(
        title: String,
        contents: String,
        phone: String,
        maxCount: Int,
        seashoreName: String,
        level: String
    ) {
        val mt = buildMeetingTime(selectedDate, selectedTime)
            ?: return setError("모임 날짜/시간을 선택해주세요.")

        if (title.isBlank()) return setError("제목을 입력해주세요.")
        if (seashoreName.isBlank()) return setError("해변을 선택해주세요.")

        val body = CreateGatheringReq(
            title = title.trim(),
            contents = contents.trim(),
            phone = phone.trim(),
            maxCount = maxCount,
            seashoreName = seashoreName.trim(),
            meetingTime = mt,
            level = level // 그대로 서버 ENUM 문자열 사용
        )

        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null, success = false)
            repo.create(body)
                .onSuccess {
                    _ui.value = UiState(loading = false, success = true, error = null)
                }
                .onFailure { e ->
                    _ui.value = UiState(loading = false, success = false, error = e.message)
                }
        }
    }

    private fun buildMeetingTime(d: LocalDate?, t: LocalTime?): String? {
        if (d == null || t == null) return null
        fun Int.p2() = toString().padStart(2, '0')
        return "${d.year}-${d.monthValue.p2()}-${d.dayOfMonth.p2()}-${t.hour.p2()}-${t.minute.p2()}"
    }

    private fun setError(msg: String) {
        _ui.value = UiState(loading = false, success = false, error = msg)
    }
}