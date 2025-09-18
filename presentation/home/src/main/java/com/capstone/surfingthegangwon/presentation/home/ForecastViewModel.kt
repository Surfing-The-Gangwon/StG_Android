package com.capstone.surfingthegangwon.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.surfingthegangwon.dto.ForecastDto
import com.capstone.surfingthegangwon.dto.TideDayDto
import com.capstone.surfingthegangwon.dto.TideDto
import com.capstone.surfingthegangwon.repository.SeashoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val repo: SeashoreRepository
) : ViewModel() {
    data class HalfDay(
        val weatherText: String,
        val skyCode: String,
        val windDir: String,
        val windSpeedMs: String,
        val waveM: String
    )
    data class TideHalf(
        val time: String?,
        val levelM: String?
    )

    data class DayUiModel(
        val date: LocalDate,
        val am: HalfDay?,
        val pm: HalfDay?,
        val tideAm: TideHalf?,
        val tidePm: TideHalf?
    )

    private val _days = MutableStateFlow<List<DayUiModel>>(emptyList())
    val days: StateFlow<List<DayUiModel>> = _days

    data class UiState(
        val loading: Boolean = false,
        val days: List<DayUiModel> = emptyList(),
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun load(seashoreId: Int) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            runCatching {
                val forecasts = repo.getForecasts(seashoreId)
                val tides = repo.getTides(seashoreId)
                Log.d("arieum", "ForecastViewModel load() forecasts=$forecasts")
                buildUiModels(forecasts, tides)
            }.onSuccess { list ->
                _days.value = list.take(5)
                _ui.value = UiState(loading = false, days = _days.value, error = null)

            }.onFailure { e ->
                _days.value = emptyList()
                _ui.value = UiState(loading = false, days = emptyList(), error = e.message)

            }
        }
    }

    private fun buildUiModels(
        forecasts: List<ForecastDto>,
        tideDays: List<TideDayDto>
    ): List<DayUiModel> {
        // 1) 날짜별 예보(오전/오후)로 쪼개기
        val byDate = forecasts.groupBy { it.tmef.substring(0, 8) } // yyyyMMdd
        val dateKeys = byDate.keys.sorted() // 오름차순

        // 2) 물때를 날짜별(yyyy-MM-dd)로 접근하기 쉽게 맵 구성
        val tideByDate: Map<String, List<TideDto>> = tideDays
            .flatMap { it.tide }
            .groupBy { it.baseDate } // yyyy-MM-dd

        return dateKeys.map { ymd ->
            val list = byDate[ymd].orEmpty()
            val am = list.firstOrNull { it.tmef.endsWith("0000") }?.toHalfDay()
            val pm = list.firstOrNull { it.tmef.endsWith("1200") }?.toHalfDay()

            val date = LocalDate.parse(ymd, DateTimeFormatter.ofPattern("yyyyMMdd"))

            // 물때는 해당 날짜의 가장 이른/가장 늦은 값 1개씩만 사용(없으면 null)
            val tides = tideByDate[date.toString()].orEmpty().sortedBy { it.tiTime }
            val tideAm = tides.firstOrNull()?.toTideHalf()
            val tidePm = tides.lastOrNull()?.toTideHalf()

            DayUiModel(date, am, pm, tideAm, tidePm)
        }
    }

    private fun ForecastDto.toHalfDay() = HalfDay(
        weatherText = wf,
        skyCode = sky,
        windDir = w2,
        windSpeedMs = s2,
        waveM = wh2
    )

    private fun TideDto.toTideHalf(): TideHalf {
        val levelM = tilevel.toDoubleOrNull()?.let { (it / 1000.0).format1() } // mm→m
        return TideHalf(time = tiTime, levelM = levelM?.let { "$it m" })
    }

    private fun Double.format1(): String = java.text.DecimalFormat("#.#").format(this)
}