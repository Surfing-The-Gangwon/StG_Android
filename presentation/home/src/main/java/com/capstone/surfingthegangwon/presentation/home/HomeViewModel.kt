package com.capstone.surfingthegangwon.presentation.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.surfingthegangwon.BeachInfo
import com.capstone.surfingthegangwon.dto.City
import com.capstone.surfingthegangwon.dto.toBeachInfo
import com.capstone.surfingthegangwon.repository.CityRepository
import com.capstone.surfingthegangwon.repository.SeashoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cityRepository: CityRepository,
    private val seashoreRepository: SeashoreRepository
): ViewModel() {
    private val _beachWaveList = MutableLiveData<List<BeachInfo>>()
    val beachWaveList: LiveData<List<BeachInfo>> = _beachWaveList

    private val _cities = MutableLiveData<List<City>>()
    val cities: LiveData<List<City>> = _cities

    private val _tabTitles = MutableLiveData<List<String>>()
    val tabTitles: LiveData<List<String>> = _tabTitles

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadCities() {
        _loading.value = true
        viewModelScope.launch {
            try {
                val result = cityRepository.getCities()
                _cities.value = result
                _tabTitles.value = result.map { it.cityName }
                _loading.value = false

                Log.d("arieum", "loadCities: $result")

                result.firstOrNull()?.let { first ->
                    setBeachData(first.cityName)
                }
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "도시 목록을 가져오지 못했어요."
            }
        }
    }

    fun setBeachData(city: String) {
        val targetCityId = _cities.value?.firstOrNull { it.cityName == city }?.cityId
        if (targetCityId == null) {
            _error.value = "도시를 찾을 수 없어요: $city"
            return
        }

        _loading.value = true
        viewModelScope.launch {
            try {
                val seashores = seashoreRepository.getSeashores(targetCityId)
                val mapped = seashores.map { it.toBeachInfo() }
                _beachWaveList.value = mapped
                _loading.value = false
            } catch (e: Exception) {
                _loading.value = false
                _error.value = e.message ?: "해변 정보를 가져오지 못했어요."
            }
        }
    }

//    fun setBeachData(beach: String) {
//        val dummy = List(10) {
//            BeachInfo(
//                beachName = "$beach 죽도해변",
//                airTemp = "32.0 C˚",
//                waterTemp = "25.5 C˚",
//                waveHeight = "0.69M",
//                wavePeriod = "4.49S",
//                windDirection = "W",
//                windSpeed = "8.6m/s"
//            )
//        }
//
//        _beachWaveList.value = dummy
//    }
}