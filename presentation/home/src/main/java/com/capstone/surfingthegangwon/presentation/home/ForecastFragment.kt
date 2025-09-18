package com.capstone.surfingthegangwon.presentation.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.capstone.surfingthegangwon.presentation.home.databinding.FragmentForecastBinding
import com.capstone.surfingthegangwon.presentation.home.databinding.ItemForecastDayBinding
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ForecastFragment : Fragment() {

    private lateinit var binding: FragmentForecastBinding
    private val viewModel: ForecastViewModel by viewModels()
    private val args: ForecastFragmentArgs by navArgs()

    private val dateOutFmt = DateTimeFormatter.ofPattern("MM월 dd일 (E)", java.util.Locale.KOREAN)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForecastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.load(seashoreId = args.seashoreId)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui ->
                    // 로딩 UI
                    if (ui.loading) {
                        binding.contentScroll.visibility = View.INVISIBLE
                        binding.lottieLoading.visibility = View.VISIBLE
                        binding.lottieLoading.playAnimation()
                    } else {
                        binding.lottieLoading.cancelAnimation()
                        binding.lottieLoading.visibility = View.GONE
                        binding.contentScroll.visibility = View.VISIBLE
                    }

                    // 데이터 바인딩 (5개 고정)
                    val days = ui.days
                    bindDay(binding.day1, days.getOrNull(0))
                    bindDay(binding.day2, days.getOrNull(1))
                    bindDay(binding.day3, days.getOrNull(2))
                    bindDay(binding.day4, days.getOrNull(3))
                    bindDay(binding.day5, days.getOrNull(4))
                }
            }
        }
    }

    private fun bindDay(card: ItemForecastDayBinding, day: ForecastViewModel.DayUiModel?) {
        if (day == null) {
            card.root.visibility = View.GONE
            return
        }
        card.root.visibility = View.VISIBLE

        // 날짜 헤더
        card.tvDate.text = day.date.format(dateOutFmt)

        // 오전
        card.tvWindDirAm.text   = day.am?.windDir ?: "-"
        card.tvWindSpeedAm.text = day.am?.windSpeedMs?.let { "$it m/s" } ?: "-"
        card.tvWaveAm.text      = day.am?.waveM?.let { "$it m" } ?: "-"

        card.tvTideTimeAm.text  = day.tideAm?.time ?: "-"
        card.tvTideLevelAm.text = day.tideAm?.levelM ?: "-"

        // 오후
        card.tvWindDirPm.text   = day.pm?.windDir ?: "-"
        card.tvWindSpeedPm.text = day.pm?.windSpeedMs?.let { "$it m/s" } ?: "-"
        card.tvWavePm.text      = day.pm?.waveM?.let { "$it m" } ?: "-"

        card.tvTideTimePm.text  = day.tidePm?.time ?: "-"
        card.tvTideLevelPm.text = day.tidePm?.levelM ?: "-"

        // 하늘상태 코드별 아이콘으로 Chip 아이콘 바꾸기
        card.chipAm.setChipIconResource(day.am?.skyCode.toIconRes())
        card.chipPm.setChipIconResource(day.pm?.skyCode.toIconRes())
    }

    private fun Chip.setChipIconResource(resId: Int?) {
        if (resId == null) return
        this.setChipIconResource(resId)
    }

    // DB01~DB04 매핑 (임의 예시)
    private fun String?.toIconRes(): Int? = when (this) {
        "DB01" -> R.drawable.ic_sunny
        "DB02" -> R.drawable.ic_rainy
        "DB03" -> R.drawable.ic_rainy
        "DB04" -> R.drawable.ic_rainy
        else   -> null
    }
}
