package com.capstone.surfingthegangwon.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.surfingthegangwon.BeachInfo

class HomeViewModel : ViewModel() {
    private val _beachWaveList = MutableLiveData<List<BeachInfo>>()
    val beachWaveList: LiveData<List<BeachInfo>> = _beachWaveList

    fun setBeachData() {
        val dummy = List(5) {
            BeachInfo(
                beachName = "죽도해변B",
                airTemp = 32.0f,
                waterTemp = 22.5f,
                waveHeight = 0.69f,
                wavePeriod = 4.49f,
                windDirection = "W",
                windSpeed = 8.6f
            )
        }

        _beachWaveList.value = dummy
    }
}