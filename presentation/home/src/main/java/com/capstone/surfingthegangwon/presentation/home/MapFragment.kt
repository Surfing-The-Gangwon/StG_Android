package com.capstone.surfingthegangwon.presentation.home

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.capstone.surfingthegangwon.presentation.home.databinding.FragmentMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import java.lang.Exception
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.surfingthegangwon.HubPlace
import com.capstone.surfingthegangwon.presentation.home.databinding.BottomSheetListBinding
import com.capstone.surfingthegangwon.presentation.home.databinding.BottomSheetSurfSchoolBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTextBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()
    private val args: MapFragmentArgs by navArgs()

    private var kakaoMap: KakaoMap? = null
    private var labelLayer: LabelLayer? = null

    private val labelByPlace = mutableMapOf<PlaceMarker, Label>()
    private val placeByLabel = mutableMapOf<Label, PlaceMarker>()
    private var selectedCategory: Category? = null
    private var selectedPlace: PlaceMarker? = null

    private data class StyleBundle(val normal: LabelStyles, val dim: LabelStyles)
    private lateinit var styles: Map<Category, StyleBundle>

    private lateinit var placeListAdapter: PlaceListAdapter
    private lateinit var placeSheetBehavior: BottomSheetBehavior<*>
    private lateinit var listSheetBehavior: BottomSheetBehavior<*>
    private lateinit var surfSchoolBehavior: BottomSheetBehavior<*>
    private lateinit var listBinding: BottomSheetListBinding
    private lateinit var surfSchoolSheet: BottomSheetSurfSchoolBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val beachName = args.beachName
        binding.tvTitle.text = beachName
        Log.d("arieum", "Received beach name: $beachName")

        setupTabs()
        viewModel.fetchHubSample(baseYm = "202503", areaCd = "51", signguCd = "51830")
        setupBottomSheets()

        binding.mapview.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(error: Exception) {}
            },
            object : KakaoMapReadyCallback() {
                override fun getPosition(): LatLng = LatLng.from(37.9705, 128.7518)
                override fun getZoomLevel(): Int = 15
                override fun onMapReady(map: KakaoMap) {
                    kakaoMap = map
                    initLayerAndStyles(map)
                    addAllMarkers(beachName)     // 모든 마커 추가 (beach는 선명, 나머지는 흐림)
                    bindLabelClick(map)          // 마커 클릭 → 커짐(단일)
                    applyCategoryFilter()        // 초기 필터 적용

                    showDefaultBottomSheet(beachName)

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.hubList.collect { hubList ->
                                if (kakaoMap != null && labelLayer != null) {
                                    addTouristMarkers(hubList)
                                }
                                if (selectedCategory == Category.TOURIST) {
                                    placeListAdapter.submitList(buildTourPlaceMarkers())
                                }
                            }
                        }
                    }
                }
            }
        )

        with(binding) {
            listOf(btnWaterTemp, btnAirTemp, btnUv).forEach { it.navToForecast() }
        }
    }

    private fun View.navToForecast() {
        setOnClickListener {
            findNavController().navigate(MapFragmentDirections.actionMapToForecast())
        }
    }

    private fun setupBottomSheets() {
        placeSheetBehavior = BottomSheetBehavior.from(binding.placeBottomSheet).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        // 장소 리스트 시트
        listBinding = binding.placeListSheet
        listBinding.root.visibility = View.VISIBLE
        listSheetBehavior = BottomSheetBehavior.from(listBinding.root).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
            skipCollapsed = false
        }

        // 서핑스쿨 상세 시트
        surfSchoolSheet = binding.surfSchoolSheet
        surfSchoolSheet.root.visibility = View.VISIBLE
        surfSchoolBehavior = BottomSheetBehavior.from(surfSchoolSheet.root).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
            skipCollapsed = false
        }

        placeListAdapter = PlaceListAdapter()
        listBinding.recyclerViewPlaces.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = placeListAdapter
        }
    }

    /**
     * TourAPI 관광지 마커 추가
     */
    private fun addTouristMarkers(hubList: List<HubPlace>) {
        val layer = labelLayer ?: return

        hubList.forEach { hub ->
            val name = hub.name
            val lat = hub.lat ?: return@forEach
            val lng = hub.lng ?: return@forEach

            val place = PlaceMarker(
                name = name,
                lat = lat,
                lng = lng,
                category = Category.TOURIST
            )

            val style = styles[Category.TOURIST]?.dim ?: return@forEach
            val label = layer.addLabel(
                LabelOptions.from(LatLng.from(lat, lng))
                    .setStyles(style)
                    .setTexts(LabelTextBuilder().setTexts(name))
                    .setRank(1000)
            )

            labelByPlace[place] = label
            placeByLabel[label] = place
        }
    }

    /**
     * 진입 시 기본으로 표시할 BottomSheet 설정
     */
    private fun showDefaultBottomSheet(beachName: String) {
        val defaultPlace = dummyPlaces.find { it.category == Category.BEACH }
            ?: dummyPlaces.firstOrNull()

        defaultPlace?.let { place ->
            val placeModel = PlaceUiModel(
                title = place.name,
                address = "강원특별자치도 양양시 양양군 70 죽도해변",
                phone = if (place.category == Category.BEACH) "033-123-4567" else null,
                lat = place.lat,
                lng = place.lng
            )

            binding.placeBottomSheet.bind(placeModel) { model ->
                // 길찾기 버튼 클릭 시 동작
                openKakaoNavigation(model)
            }
        }
    }

    /**
     * 서핑스쿨 상세 시트 바인딩
     */
    private fun bindSurfSchoolSheet(p: PlaceUiModel) = with(surfSchoolSheet) {
        tvPlaceName.text = p.title
        tvAddress.text   = p.address ?: ""
        tvPhone.text     = p.phone ?: ""

        tvAddress.isVisible = !p.address.isNullOrBlank()
        tvPhone.isVisible   = !p.phone.isNullOrBlank()

        kakaoRouteBtn.setOnClickListener { openKakaoPlace(p) }
    }

    /**
     * 카카오맵 길찾기 실행
     */
    private fun openKakaoNavigation(place: PlaceUiModel) {
        try {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("kakaomap://route?ep=${place.lat},${place.lng}&by=FOOT")
            )
            startActivity(intent)
        } catch (e: Exception) {
            // 카카오맵이 없는 경우 웹으로 연결
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://map.kakao.com/link/to/${place.title},${place.lat},${place.lng}")
            )
            startActivity(intent)
        }
    }

    private fun openKakaoPlace(place: PlaceUiModel) {
        try {
            // 카카오맵 앱으로 '해당 좌표 보기'
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("kakaomap://look?p=${place.lat},${place.lng}")
            )
            startActivity(intent)
        } catch (e: Exception) {
            // 앱이 없으면 웹으로 (지도 열기)
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://map.kakao.com/link/map/${place.title},${place.lat},${place.lng}")
            )
            startActivity(intent)
        }
    }


    /**
     * 1) 레이어 & 스타일 등록
     * */
    private fun initLayerAndStyles(map: KakaoMap) {
        val lm = map.labelManager ?: return
        labelLayer = lm.layer

        fun makeStyles(resId: Int): LabelStyles? {
            val black = context?.let { ContextCompat.getColor(it, android.R.color.black) }
            val white = context?.let { ContextCompat.getColor(it, android.R.color.white) }

            val base = LabelStyle.from(resId)
                .setTextStyles(25, black!!, 2, white!!)

            return lm.addLabelStyles(LabelStyles.from(base))
        }

        styles = mapOf(
            Category.BEACH       to StyleBundle(
                normal  = makeStyles(R.drawable.ic_pin_beach)!!,
                dim     = makeStyles(R.drawable.ic_pin_beach_dim)!!
            ),
            Category.SURF_SHOP   to StyleBundle(
                normal  = makeStyles(R.drawable.ic_pin_shop)!!,
                dim     = makeStyles(R.drawable.ic_pin_shop_dim)!!
            ),
            Category.SURF_SCHOOL to StyleBundle(
                normal  = makeStyles(R.drawable.ic_pin_school)!!,
                dim     = makeStyles(R.drawable.ic_pin_school_dim)!!
            ),
            Category.TOURIST     to StyleBundle(
                normal  = makeStyles(R.drawable.ic_pin_tour)!!,
                dim     = makeStyles(R.drawable.ic_pin_tour_dim)!!
            )
        )
    }

    /**
     * 2) 모든 마커 추가 (초기: beach만 선명, 나머지 흐림)
     * */
    private fun addAllMarkers(beachName: String) {
        val layer = labelLayer ?: return
        labelByPlace.clear(); placeByLabel.clear()

        dummyPlaces.forEach { p ->
            val bundle = styles[p.category]!!
            val style  = if (p.category == Category.BEACH) bundle.normal else bundle.dim

            val label = layer.addLabel(
                LabelOptions.from(LatLng.from(p.lat, p.lng))
                    .setStyles(style)
                    .setTexts(LabelTextBuilder().setTexts(p.name))
                    .setRank(1000)
            )
            labelByPlace[p] = label
            placeByLabel[label] = p
        }
    }

    /**
     * 3) 탭 → 단일 선택 필터 (선택 카테고리만 선명, 그 외 흐림 / beach는 항상 선명)
     * */
    private fun applyCategoryFilter() {
        val layer = labelLayer ?: return
        labelByPlace.forEach { (place, oldLabel) ->
            val b = styles[place.category]!!
            val style = when {
                place.category == Category.BEACH       -> b.normal
                selectedCategory == null               -> b.dim
                place.category == selectedCategory     -> b.normal
                else                                   -> b.dim
            }
            replaceLabel(place, oldLabel, style, layer)
        }
    }

    /**
     * 4) 마커 클릭 → 한 개만 크게
     * */
    private fun bindLabelClick(map: KakaoMap) {
        map.setOnLabelClickListener(object : KakaoMap.OnLabelClickListener {
            override fun onLabelClicked(kMap: KakaoMap, layer: LabelLayer, label: Label): Boolean {
                val place = placeByLabel[label] ?: return true
                selectedPlace = if (selectedPlace == place) null else place
                applyCategoryFilter()

                if (place.category == Category.SURF_SCHOOL) {
                    // 필요하면 주소/전화는 실제 데이터로 채우세요
                    val model = PlaceUiModel(
                        title = place.name,
                        address = "강원특별자치도 양양시 양양군 70 죽도해변",   // 없으면 null 가능
                        phone = "033-252-3342",
                        lat = place.lat,
                        lng = place.lng
                    )

                    // 리스트 시트는 숨김
                    listSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    placeSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                    // 서핑스쿨 시트 바인딩 + 열기
                    bindSurfSchoolSheet(model)
                    surfSchoolSheet.root.bringToFront()
                    surfSchoolSheet.root.post {
                        surfSchoolBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                } else {
                    // 다른 카테고리는 기존 동작 유지(필요 시)
                }

                return true
            }
        })
    }

    /**
     * 5) 라벨 교체
     * */
    private fun replaceLabel(
        place: PlaceMarker,
        old: Label,
        style: LabelStyles,
        layer: LabelLayer
    ) {
        old.remove()
        val newLabel = layer.addLabel(
            LabelOptions.from(LatLng.from(place.lat, place.lng))
                .setStyles(style)
                .setTexts(LabelTextBuilder().setTexts(place.name))
                .setRank(1000)
        )
        labelByPlace[place] = newLabel
        placeByLabel.remove(old)
        placeByLabel[newLabel] = place
    }

    /**
     * 탭 선택 로직
     * */
    private fun setupTabs() = with(binding) {
        val tabs = listOf(tabShop, tabSchool, tabTour)

        fun select(tab: View) {
            tabs.forEach { it.isSelected = (it === tab) }
            when (tab.id) {
                R.id.tab_shop       -> showMarkers(Category.SURF_SHOP)
                R.id.tab_school     -> showMarkers(Category.SURF_SCHOOL)
                R.id.tab_tour       -> showMarkers(Category.TOURIST)
            }
        }

        tabs.forEach { tab ->
            tab.setOnClickListener { select(tab) }
        }
    }

    /**
     * 카테고리에 해당하는 마커만 표시하는 로직 구현
     * */
    private fun showMarkers(category: Category) {
        selectedCategory = category
        applyCategoryFilter()

        // 리스트 데이터 준비
        val listForSheet: List<PlaceMarker> = when (category) {
            Category.TOURIST -> buildTourPlaceMarkers()                     // TourAPI 리스트
            else -> dummyPlaces.filter { it.category == category }          // 더미 리스트
        }

        // 상세 시트는 숨기고
        placeSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        surfSchoolBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // 리스트 업데이트 후 바로 상태 전환
        placeListAdapter.submitList(listForSheet) {
            listBinding.root.post {
                listSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    /**
     * TourAPI 관광지 리스트를 PlaceMarker 리스트로 변환
     * */
    private fun buildTourPlaceMarkers(): List<PlaceMarker> {
        return viewModel.hubList.value
            .asSequence()
            .filter { it.lat != null && it.lng != null }
            .map { hub ->
                PlaceMarker(
                    name = hub.name,
                    lat = hub.lat!!,
                    lng = hub.lng!!,
                    category = Category.TOURIST
                )
            }
            .toList()
    }


    override fun onResume() {
        super.onResume()
        binding.mapview.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapview.pause()
    }
}

/**
 * 더미데이터
 * */
private val dummyPlaces = listOf(
    PlaceMarker("죽도해변", 37.9705, 128.7518, Category.BEACH),
    PlaceMarker("죽도 서핑샵 A", 37.98, 128.74, Category.SURF_SHOP),
    PlaceMarker("죽도 서핑샵 B", 37.9809, 128.73, Category.SURF_SHOP),
    PlaceMarker("죽도 서핑스쿨",  37.9712, 128.72, Category.SURF_SCHOOL),
)