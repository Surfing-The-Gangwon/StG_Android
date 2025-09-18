package com.capstone.surfingthegangwon.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.surfingthegangwon.dto.HubPlace
import com.capstone.surfingthegangwon.dto.LessonDto
import com.capstone.surfingthegangwon.dto.PlaceDetailDto
import com.capstone.surfingthegangwon.dto.RentalDto
import com.capstone.surfingthegangwon.dto.SeashoreDetailDto
import com.capstone.surfingthegangwon.dto.SeashoreMarkerDto
import com.capstone.surfingthegangwon.dto.SurfingShopDetailDto
import com.capstone.surfingthegangwon.repository.SeashoreRepository
import com.capstone.surfingthegangwon.repository.SurfingRepository
import com.capstone.surfingthegangwon.repository.TourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val seashoreRepository: SeashoreRepository,
    private val repo: TourRepository,
    private val surfingRepository: SurfingRepository
) : ViewModel() {
    private val _hubList = MutableStateFlow<List<HubPlace>>(emptyList())
    val hubList: StateFlow<List<HubPlace>> = _hubList

    private val _markers = MutableStateFlow<List<SeashoreMarkerDto>>(emptyList())
    val markers: StateFlow<List<SeashoreMarkerDto>> = _markers

    sealed interface SurfingUiState {
        object Idle : SurfingUiState
        object Loading : SurfingUiState
        data class Loaded(
            val detail: SurfingShopDetailDto,
            val lessons: List<LessonDto>,
            val rentals: List<RentalDto>
        ) : SurfingUiState
        data class Error(val error: Throwable) : SurfingUiState
    }

    private val _surfing = MutableStateFlow<SurfingUiState>(SurfingUiState.Idle)
    val surfing: StateFlow<SurfingUiState> = _surfing

    data class UvState(
        val indexText: String = "-",
        val levelText: String = "-"
    )

    private val _uv = MutableStateFlow(UvState())
    val uv: StateFlow<UvState> = _uv

    private data class City(val areaCd: String, val signguCd: String)

    /** 서버 CityId(1,2,3,4) -> TourAPI (areaCd, signguCd) 매핑 */
    private fun cityOf(serverCityCode: Int): City? = when (serverCityCode) {
        1 -> City(areaCd = "51", signguCd = "51150") // 강릉
        3 -> City(areaCd = "51", signguCd = "51210") // 속초
        4 -> City(areaCd = "51", signguCd = "51820") // 고성
        2 -> City(areaCd = "51", signguCd = "51830") // 양양
        else -> null
    }

    fun fetchHubsByCityCode(
        serverCityCode: Int,
        baseYm: String = "202503",
        pageNo: Int = 1,
        numOfRows: Int = 10
    ) {
        val city = cityOf(serverCityCode) ?: run {
            Log.e("TourAPI", "Unknown CityCODE: $serverCityCode")
            _hubList.value = emptyList()
            return
        }

        viewModelScope.launch {
            runCatching {
                repo.getHubs(baseYm, city.areaCd, city.signguCd, pageNo, numOfRows)
            }.onSuccess { res ->
                _hubList.value = res.items
                Log.d("TourAPI", "HubList(${serverCityCode}): ${res.items}")
            }.onFailure { e ->
                Log.e("TourAPI", "fetchHubsByCityCode failed", e)
                _hubList.value = emptyList()
            }
        }
    }

    fun fetchSeashoreMarkers(seashoreId: Int) {
        viewModelScope.launch {
            runCatching { seashoreRepository.getSeashoreMarkers(seashoreId) }
                .onSuccess {
                    _markers.value = it
                }
                .onFailure { e ->
                    _markers.value = emptyList()
                    Log.e("MapVM", "fetchSeashoreMarkers failed", e)
                }
        }
    }

    fun loadSurfing(markerId: Int) {
        viewModelScope.launch {
            _surfing.value = SurfingUiState.Loading
            runCatching { surfingRepository.fetch(markerId) }
                .onSuccess { b ->
                    _surfing.value = SurfingUiState.Loaded(
                        detail  = b.detail,
                        lessons = b.lessons.take(2),
                        rentals = b.rentals.take(2)
                    )
                }
                .onFailure { e -> _surfing.value = SurfingUiState.Error(e) }
        }
    }

    fun fetchUv(seashoreId: Int) {
        viewModelScope.launch {
            runCatching { seashoreRepository.getSeashoreUV(seashoreId) }
                .onSuccess { raw ->
                    val idx = raw.trim()
                    _uv.value = UvState(indexText = idx, levelText = idx.toUvLevel())
                }
                .onFailure {
                    _uv.value = UvState(indexText = "-", levelText = "데이터 없음")
                }
        }
    }

    fun String.toUvLevel(): String {
        val uv = this.toIntOrNull() ?: return "알 수 없음"
        return when {
            uv >= 11 -> "위험"
            uv in 8..10 -> "매우 높음"
            uv in 6..7 -> "높음"
            uv in 3..5 -> "보통"
            uv in 0..2 -> "낮음"
            else -> "알 수 없음"
        }
    }
}