package com.capstone.surfingthegangwon.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.surfingthegangwon.BeachInfo
import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class HomeViewModel : ViewModel() {

    private val _beachWaveList = MutableLiveData<List<BeachInfo>>()
    val beachWaveList: LiveData<List<BeachInfo>> = _beachWaveList

    private val regionBeaches = mapOf(
        "양양" to listOf("죽도해변", "인구해변", "낙산해변", "갯마을해변"),
        "고성" to listOf("아야진해변", "삼포해변", "송지호해변", "가진해변"),
        "속초" to listOf("속초해변", "외옹치해변", "대포해변"),
        "강릉" to listOf("경포해변", "안목해변", "주문진해변", "사천진해변")
    )

    private val windDirs = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")

    fun setBeachData(region: String) {
        val beaches = regionBeaches[region].orEmpty().ifEmpty { listOf("$region 해변") }

        val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
        val daySeed = today.dayOfYear

        val month = today.monthValue
        val airMin: Float
        val airMax: Float
        val waterMin: Float
        val waterMax: Float
        when (month) {
            9 -> { // 9월
                airMin = 23f; airMax = 29f
                waterMin = 21f; waterMax = 24f
            }
            10 -> { // 10월
                airMin = 16f; airMax = 24f
                waterMin = 18f; waterMax = 21f
            }
            else -> { // 그 외: 넓게
                airMin = 15f; airMax = 28f
                waterMin = 16f; waterMax = 24f
            }
        }

        val list = beaches.mapIndexed { idx, name ->
            // 해변별 편차를 위해 추가 해시 섞기
            val rnd = Random(daySeed * 1000 + region.hashCode() + name.hashCode() + idx)

            val airTemp = randIn(rnd, airMin, airMax)
            // 수온은 변동폭 적고 지역 편차 작게
            val waterTemp = clamp(
                randIn(rnd, waterMin, waterMax) + randIn(rnd, -0.4f, 0.4f),
                waterMin - 0.5f, waterMax + 0.5f
            )

            // 동해 가을 대략: 0.2~1.5m, 가끔 너울 들어오면 +0.2~0.5
            val baseWave = randIn(rnd, 0.2f, 1.5f)
            val extraSwell = if (rnd.nextFloat() < 0.15f) randIn(rnd, 0.2f, 0.5f) else 0f
            val waveHeight = baseWave + extraSwell

            // 파고 ↔ 주기 간 단순 상관: 4.0 ~ 10.0초
            val wavePeriod = clamp(4.0f + waveHeight * 3.0f + randIn(rnd, -0.5f, 0.8f), 4.0f, 10.0f)

            // 풍속 m/s: 1~12
            val windSpeed = randIn(rnd, 1.0f, 12.0f)
            val windDirection = windDirs[rnd.nextInt(windDirs.size)]

            BeachInfo(
                beachName = name,
                airTemp = round1(airTemp),
                waterTemp = round1(waterTemp),
                waveHeight = round2(waveHeight),
                wavePeriod = round2(wavePeriod),
                windDirection = windDirection,
                windSpeed = round1(windSpeed)
            )
        }

        _beachWaveList.value = list
    }

    // ---------- 유틸 ----------
    private fun randIn(rnd: Random, minV: Float, maxV: Float): Float =
        minV + rnd.nextFloat() * (maxV - minV)

    private fun clamp(v: Float, minV: Float, maxV: Float): Float =
        max(minV, min(maxV, v))

    private fun round1(v: Float): Float = (v * 10).toInt() / 10f
    private fun round2(v: Float): Float = (v * 100).toInt() / 100f
}