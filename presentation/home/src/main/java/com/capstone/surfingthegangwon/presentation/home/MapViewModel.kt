package com.capstone.surfingthegangwon.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.surfingthegangwon.HubPlace
import com.capstone.surfingthegangwon.HubResult
import com.capstone.surfingthegangwon.TourRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repo: TourRepository
) : ViewModel() {
    private val _hubList = MutableStateFlow<List<HubPlace>>(emptyList())
    val hubList: StateFlow<List<HubPlace>> = _hubList

    fun fetchHubSample(
        baseYm: String = "202503",
        areaCd: String = "51",
        signguCd: String = "51830"
    ) {
        viewModelScope.launch {
            runCatching {
                repo.getHubs(baseYm, areaCd, signguCd, pageNo = 1, numOfRows = 30)
            }.onSuccess { res ->
                _hubList.value = res.items
                Log.d("TourAPI", "HubList: ${res.items}")
            }.onFailure { e ->
                Log.e("TourAPI", "fetchHubSample failed", e)
                _hubList.value = emptyList()
            }
        }
    }
}