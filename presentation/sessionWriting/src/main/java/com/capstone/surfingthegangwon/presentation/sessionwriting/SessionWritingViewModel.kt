package com.capstone.surfingthegangwon.presentation.sessionwriting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.surfingthegangwon.data.city.repoImpl.CityRepositoryImpl
import com.capstone.surfingthegangwon.data.sessionWriting.CreateGatheringReq
import com.capstone.surfingthegangwon.data.sessionWriting.GatheringRepository
import com.capstone.surfingthegangwon.domain.city.City
import com.capstone.surfingthegangwon.domain.city.Seashores
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class SessionWritingViewModel @Inject constructor(
    private val cityRepo: CityRepositoryImpl,
    private val repo: GatheringRepository
) : ViewModel() {
    data class UiState(
        val loading: Boolean = false,
        val success: Boolean = false,
        val error: String? = null
    )

    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    // 폼 상태
    var selectedDate: LocalDate? = null
    var selectedTime: LocalTime? = null

    // ===== 신규: 서버 데이터 상태 =====
    private val _cities = MutableStateFlow<List<City>>(emptyList())
    val cities: StateFlow<List<City>> = _cities

    private val _seashores = MutableStateFlow<List<Seashores>>(emptyList())
    val seashores: StateFlow<List<Seashores>> = _seashores

    // 선택 상태
    private val _selectedCityId = MutableStateFlow<Int?>(null)
    val selectedCityId: StateFlow<Int?> = _selectedCityId

    private val _selectedSeashoreName = MutableStateFlow<String?>(null)
    val selectedSeashoreName: StateFlow<String?> = _selectedSeashoreName

    init {
        viewModelScope.launch {
            loadCities() // 화면 진입 시 도시 목록 로드 & 첫 도시의 해변 자동 로드
        }
    }

    private suspend fun loadCities() {
        _ui.value = _ui.value.copy(loading = true, error = null)
        cityRepo.getCities()
            .onSuccess { list ->
                _cities.value = list
                _ui.value = _ui.value.copy(loading = false)
                list.firstOrNull()?.let { onCitySelected(it.cityId) }
            }
            .onFailure { e ->
                _cities.value = emptyList()
                _ui.value = _ui.value.copy(loading = false, error = e.message)
            }
    }

    fun onCitySelected(cityId: Int) {
        if (_selectedCityId.value == cityId) return
        _selectedCityId.value = cityId
        _selectedSeashoreName.value = null
        viewModelScope.launch { loadSeashores(cityId) }
    }

    private suspend fun loadSeashores(cityId: Int) {
        _ui.value = _ui.value.copy(loading = true, error = null)
        cityRepo.getSeashores(cityId)
            .onSuccess { list ->
                _seashores.value = list
                _ui.value = _ui.value.copy(loading = false)
                _selectedSeashoreName.value = list.firstOrNull()?.name   // 첫 해변 자동 선택
            }
            .onFailure { e ->
                _seashores.value = emptyList()
                _ui.value = _ui.value.copy(loading = false, error = e.message)
            }
    }

    fun onSeashoreSelected(name: String) {
        _selectedSeashoreName.value = name
    }

    fun create(
        title: String,
        contents: String,
        phone: String,
        maxCount: Int,
        level: String
    ) {
        val mt = buildMeetingTime(selectedDate, selectedTime)
            ?: return setError("모임 날짜/시간을 선택해주세요.")

        if (title.isBlank()) return setError("제목을 입력해주세요.")

        val seashoreName = _selectedSeashoreName.value.orEmpty()
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