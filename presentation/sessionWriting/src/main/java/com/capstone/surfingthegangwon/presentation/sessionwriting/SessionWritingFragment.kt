package com.capstone.surfingthegangwon.presentation.sessionwriting

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.surfingthegangwon.presentation.sessionwriting.databinding.FragmentSessionWritingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@AndroidEntryPoint
class SessionWritingFragment : Fragment() {
    private lateinit var binding: FragmentSessionWritingBinding
    private val viewModel: SessionWritingViewModel by viewModels()

    private lateinit var regionAdapter: RegionAdapter
    private lateinit var beachAdapter: BeachAdapter

    private fun getSelectedSeashoreName(): String =
        beachAdapter.getSelectedValue() ?: ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSessionWritingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUi()
        collectUi()
        collectServerLists()
    }

    private fun collectServerLists() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // 도시 목록
                launch {
                    viewModel.cities.collect { list ->
                        val names = list.map { it.cityName }
                        // keepSelection=true면 기존 선택 유지 (신규 진입시 자동 첫 선택 콜백 발생)
                        regionAdapter.setItems(names, keepSelection = true, triggerCallback = true)
                    }
                }
                // 해변 목록
                launch {
                    viewModel.seashores.collect { list ->
                        val names = list.map { it.name }
                        beachAdapter.setItems(names, keepSelection = false, triggerCallback = true)
                    }
                }
            }
        }
    }

    private fun initUi() {
        setTopToolBar()
        setupRecyclerViews()
        setupClickListeners()
    }

    private fun collectUi() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.ui.collect { ui ->
                    binding.registrationBtn.isEnabled = !ui.loading
                    if (ui.loading) {
                        // 로딩 UI 원하면 추가 (스낵바, 프로그레스 등)
                    }
                    ui.error?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                    }
                    if (ui.success) {
                        Toast.makeText(requireContext(), "등록 완료!", Toast.LENGTH_SHORT).show()
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }
    }

    private fun setTopToolBar() {
        binding.topAppBar.setTitle(getString(R.string.create_new_session))
        binding.topAppBar.setOnBackClick {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    /** 버튼 클릭 리스너 설정 */
    private fun setupClickListeners() {
        binding.timeTv.setOnClickListener { view -> showTimePicker(view) }
        binding.calanderTv.setOnClickListener { view -> showDatePicker(view) }

        binding.registrationBtn.setOnClickListener {
            val title = binding.sessionTitle.text?.toString().orEmpty()
            val contents = binding.sessionContent.text?.toString().orEmpty()

            val phone = listOf(
                binding.prefixNumber.text?.toString().orEmpty(),
                binding.exchangeNumber.text?.toString().orEmpty(),
                binding.subscriberNumber.text?.toString().orEmpty()
            ).joinToString("-")

            val maxCount = getSelectedMaxCount()
            val seashoreName = getSelectedSeashoreName()

            val level = when {
                binding.rbIntermediate.isChecked -> "INTERMEDIATE"
                binding.rbAdvanced.isChecked     -> "ADVANCED"
                else                             -> "ELEMENTARY"
            }

            viewModel.create(
                title = title,
                contents = contents,
                phone = phone,
                maxCount = maxCount,
                level = level
            )
        }
    }

    private fun getSelectedMaxCount(): Int {
        // 더 간단한 구현: 해당 영역의 라디오 버튼 중 체크된 것의 text를 Int로 파싱
        val radios = listOf(
            binding.root.findViewById<RadioButton>(R.id.radio1),
            binding.root.findViewById<RadioButton>(R.id.radio2),
            binding.root.findViewById<RadioButton>(R.id.radio3),
            binding.root.findViewById<RadioButton>(R.id.radio4),
            binding.root.findViewById<RadioButton>(R.id.radio5),
            binding.root.findViewById<RadioButton>(R.id.radio6)
        )
        val checked = radios.firstOrNull { it.isChecked }?.text?.toString() ?: "1"
        return checked.toIntOrNull() ?: 1
    }

    /** 모든 리사이클러 뷰 초기화 */
    private fun setupRecyclerViews() {
        setupRegionRecyclerView()
        setupBeachesRecyclerView()
    }

    /**
     * 지역 리사이클러 뷰 설정
     */
    private fun setupRegionRecyclerView() {
//        val dummyRegions =
//            listOf("양양", "고성", "속초", "강릉")
//
//        regionAdapter = RegionAdapter(dummyRegions)
//        binding.regionsRcv.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = regionAdapter
//        }
        regionAdapter = RegionAdapter(
            onSelected = { _, position ->
                val cities = viewModel.cities.value
                val cityId = cities.getOrNull(position)?.cityId ?: return@RegionAdapter
                viewModel.onCitySelected(cityId)
            }
        )
        binding.regionsRcv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = regionAdapter
        }
    }

    /**
     * 해변 리사이클러 뷰 설정
     */
    private fun setupBeachesRecyclerView() {
//        val dummyBeaches =
//            listOf("경포대 해수욕장", "죽도해변B", "죽도해변C", "죽도해변D", "죽도해변A", "죽도해변B", "죽도해변C", "죽도해변D")
//
//        beachAdapter = BeachAdapter(dummyBeaches)
//        binding.beachesRcv.apply {
//            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//            adapter = beachAdapter
//        }
        beachAdapter = BeachAdapter(
            onSelected = { name->
                viewModel.onSeashoreSelected(name)
            }
        )
        binding.beachesRcv.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = beachAdapter
        }
    }

    /**
     * 데이트피커를 보여주고 텍스트뷰 날짜를 변경하는 함수
     */
    private fun showDatePicker(view: View) {
        val dateTextView = view as TextView
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, y, m, d ->
                viewModel.selectedDate = LocalDate.of(y, m + 1, d)
                dateTextView.text = "%d년 %d월 %d일".format(y, m + 1, d)
                dateTextView.isSelected = true
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * 타임피커를 보여주고 텍스트뷰 시간을 변경하는 함수
     */
    private fun showTimePicker(view: View) {
        val timeTextView = view as TextView
        val cal = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                viewModel.selectedTime = LocalTime.of(hourOfDay, minute)
                // 표시용 텍스트 (12시간제), 실제 전송은 24시간제 문자열로 ViewModel에서 처리
                val amPm = if (hourOfDay < 12) "오전" else "오후"
                val hour12 = when {
                    hourOfDay == 0 -> 12
                    hourOfDay > 12 -> hourOfDay - 12
                    else -> hourOfDay
                }
                val txt = if (minute == 0) "%s %d시".format(amPm, hour12)
                else "%s %d시 %d분".format(amPm, hour12, minute)
                timeTextView.text = txt
                timeTextView.isSelected = true
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }
}