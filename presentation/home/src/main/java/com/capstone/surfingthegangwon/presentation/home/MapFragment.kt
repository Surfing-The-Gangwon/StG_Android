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
import android.widget.TextView
import android.widget.Toast
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
import com.bumptech.glide.Glide
import com.capstone.surfingthegangwon.dto.HubPlace
import com.capstone.surfingthegangwon.dto.LessonDto
import com.capstone.surfingthegangwon.dto.RentalDto
import com.capstone.surfingthegangwon.dto.SeashoreMarkerDto
import com.capstone.surfingthegangwon.dto.SurfingShopDetailDto
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

        setupTabs()
        val d = args.detail
        binding.tvTitle.text = d.name

        val currentBeach = PlaceUiModel(
            title = d.name, address = d.address, phone = d.telephone,
            lat = d.lat, lng = d.lng
        )
        binding.placeBottomSheet.bind(currentBeach) { openKakaoNavigation(it) }

        viewModel.fetchHubsByCityCode(serverCityCode = args.cityId)

        viewModel.fetchUv(args.seashoreId)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uv.collect { uv ->
                        binding.tvUv.text = uv.levelText
                    }
                }
            }
        }
        setupBottomSheets()

        binding.mapview.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {}
                override fun onMapError(error: Exception) {}
            },
            object : KakaoMapReadyCallback() {
                override fun getPosition(): LatLng = LatLng.from(d.lat, d.lng)
                override fun getZoomLevel(): Int = 15
                override fun onMapReady(map: KakaoMap) {
                    initLayerAndStyles(map)

                    // 현재 해변 마커
                    labelLayer = map.labelManager?.layer
                    labelLayer?.removeAll()
                    val style = styles[Category.BEACH]?.normal ?: return
                    labelLayer!!.addLabel(
                        LabelOptions.from(LatLng.from(d.lat, d.lng))
                            .setStyles(style)
                            .setTexts(LabelTextBuilder().setTexts(d.name))
                            .setRank(1000)
                    )

                    viewModel.fetchSeashoreMarkers(args.seashoreId) // 서버 마커 불러오기 (SCHOOL/SHOP)
                    bindLabelClick(map)          // 마커 클릭 → 커짐(단일)
                    applyCategoryFilter()        // 초기 필터 적용

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            launch { // 서버 마커
                                viewModel.markers.collect { server ->
                                    addServerMarkers(server)
                                    if (selectedCategory == Category.SURF_SHOP ||
                                        selectedCategory == Category.SURF_SCHOOL) {
                                        placeListAdapter.submitList(buildServerPlaceMarkers(selectedCategory!!))
                                    }
                                }
                            }
                            launch { // TourAPI 마커
                                viewModel.hubList.collect { hubList ->
                                    if (labelLayer != null) addTouristMarkers(hubList)
                                    if (selectedCategory == Category.TOURIST) {
                                        placeListAdapter.submitList(buildTourPlaceMarkers())
                                    }
                                }
                            }
                            launch {
                                viewModel.surfing.collect { state ->
                                    when (state) {
                                        is MapViewModel.SurfingUiState.Loaded -> {
                                            // 리스트/기본 시트 숨기고 상세 시트 표시
                                            listSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                                            placeSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                                            bindSurfingSheet(state.detail, state.lessons, state.rentals)

                                            surfSchoolSheet.root.bringToFront()
                                            surfSchoolSheet.root.post {
                                                surfSchoolBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                                            }
                                        }
                                        is MapViewModel.SurfingUiState.Error -> {
                                            Toast.makeText(requireContext(), "상세 불러오기 실패", Toast.LENGTH_SHORT).show()
                                        }
                                        else -> Unit
                                    }
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
     * 서버 마커 추가
     */
    private fun addServerMarkers(items: List<SeashoreMarkerDto>) {
        val layer = labelLayer ?: return

        // 중복 추가 방지: 기존 SHOP/SCHOOL 라벨만 걷어내고 다시 그림
        val toRemove = labelByPlace.filter { it.key.category == Category.SURF_SHOP || it.key.category == Category.SURF_SCHOOL }
        toRemove.forEach { (place, label) ->
            label.remove()
            labelByPlace.remove(place)
            placeByLabel.remove(label)
        }

        items.forEach { m ->
            val category = when (m.type.uppercase()) {
                "SCHOOL" -> Category.SURF_SCHOOL
                "SHOP"   -> Category.SURF_SHOP
                else     -> return@forEach
            }

            val style = styles[category]?.dim ?: return@forEach  // 초기엔 흐리게
            val label = layer.addLabel(
                LabelOptions.from(LatLng.from(m.latitude, m.longitude))
                    .setStyles(style)
                    .setTexts(LabelTextBuilder().setTexts(m.name))
                    .setRank(1000)
            )

            val place = PlaceMarker(
                id = m.id,
                name = m.name,
                lat = m.latitude,
                lng = m.longitude,
                category = category
            )
            labelByPlace[place] = label
            placeByLabel[label] = place
        }

        applyCategoryFilter()
    }

    /**
     * 서핑스쿨 상세 시트 바인딩
     */
    private fun bindSurfingSheet(
        detail: SurfingShopDetailDto,
        lessons: List<LessonDto>,
        rentals: List<RentalDto>
    ) = with(surfSchoolSheet) {

        // 헤더/소개
        tvPlaceName.text = detail.name
        tvAddress.text   = detail.address.orEmpty()
        tvPhone.text     = detail.phone.orEmpty()
        tvIntro.text     = detail.introduce.orEmpty()

        tvAddress.showOrGone(!detail.address.isNullOrBlank())
        tvPhone.showOrGone(!detail.phone.isNullOrBlank())
        tvIntro.showOrGone(!detail.introduce.isNullOrBlank())

        // 이미지 2장까지
        val img1Url = detail.shopImg.getOrNull(0)?.imgUrl
        val img2Url = detail.shopImg.getOrNull(1)?.imgUrl

        if (img1Url.isNullOrBlank()) {
            img1.showOrGone(false)
        } else {
            img1.showOrGone(true)
            Glide.with(img1).load(img1Url).into(img1)
        }

        if (img2Url.isNullOrBlank()) {
            img2.showOrGone(false)
        } else {
            img2.showOrGone(true)
            Glide.with(img2).load(img2Url).into(img2)
        }

        // ===== 레슨 2개 =====
        fun bindLesson(
            root: View,
            title: TextView,
            desc: TextView,
            duration: TextView,
            origin: TextView,
            price: TextView,
            item: LessonDto?
        ) {
            if (item == null) { root.showOrGone(false); return }
            root.showOrGone(true)
            title.text = item.title
            desc.text  = item.contents.orEmpty()
            duration.text = item.classTime?.let { "${it}분" } ?: ""

            val showOrigin = (item.originalPrice ?: 0) > 0 &&
                    item.originalPrice != item.discountedPrice
            origin.text = item.originalPrice.toWon()
            origin.setStrike(showOrigin)
            origin.showOrGone(showOrigin)

            price.text = item.discountedPrice.toWon()
        }

        bindLesson(
            itemLesson1, tvLessonTitle1, tvLessonDesc1, tvLessonDuration1,
            tvLessonOriginPrice1, tvLessonPrice1, lessons.getOrNull(0)
        )
        bindLesson(
            itemLesson2, tvLessonTitle2, tvLessonDesc2, tvLessonDuration2,
            tvLessonOriginPrice2, tvLessonPrice2, lessons.getOrNull(1)
        )

        // ===== 대여 2개 =====
        fun bindRent(
            root: View,
            title: TextView,
            duration: TextView,
            origin: TextView,
            price: TextView,
            item: RentalDto?
        ) {
            if (item == null) { root.showOrGone(false); return }
            root.showOrGone(true)
            title.text = item.name
            duration.text = item.rentalTime?.let { if (it > 0) "${it}분" else "" } ?: ""

            val showOrigin = (item.originalPrice ?: 0) > 0 &&
                    item.originalPrice != item.discountedPrice
            origin.text = item.originalPrice.toWon()
            origin.setStrike(showOrigin)
            origin.showOrGone(showOrigin)

            price.text = item.discountedPrice.toWon()
        }

        bindRent(itemRent1, tvRentTitle1, tvRentDuration1, tvRentOriginPrice1, tvRentPrice1, rentals.getOrNull(0))
        bindRent(itemRent2, tvRentTitle2, tvRentDuration2, tvRentOriginPrice2, tvRentPrice2, rentals.getOrNull(1))

        kakaoRouteBtn.setOnClickListener { openKakaoPlaceByTitle(detail.name) }
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

    private fun openKakaoPlaceByTitle(title: String) {
        try {
            // 카카오맵 앱으로 검색
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("kakaomap://search?q=${Uri.encode(title)}")
            )
            startActivity(intent)
        } catch (e: Exception) {
            // 웹으로 검색
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://map.kakao.com/link/search/${Uri.encode(title)}")
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

                // 리스트/기본 하단시트는 우선 숨김
                listSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                placeSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

                when (place.category) {
                    Category.SURF_SCHOOL, Category.SURF_SHOP -> {
                        val markerId = place.id
                        if (markerId != null) {
                            viewModel.loadSurfing(markerId)
                        } else {
                            Toast.makeText(requireContext(), "잘못된 마커 ID", Toast.LENGTH_SHORT).show()
                        }
                    }

                    else -> {
                        // 기존 동작 유지
                    }
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

        // 마커 데이터 준비
        val listForSheet: List<PlaceMarker> = when (category) {
            Category.TOURIST                         -> buildTourPlaceMarkers()
            Category.SURF_SHOP, Category.SURF_SCHOOL -> buildServerPlaceMarkers(category)
            else                 -> emptyList()
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

    private fun buildServerPlaceMarkers(category: Category): List<PlaceMarker> {
        return viewModel.markers.value
            .asSequence()
            .filter {
                val cat = when (it.type.uppercase()) {
                    "SCHOOL" -> Category.SURF_SCHOOL
                    "SHOP"   -> Category.SURF_SHOP
                    else     -> null
                }
                cat == category
            }
            .map {
                PlaceMarker(
                    id = it.id,
                    name = it.name,
                    lat = it.latitude,
                    lng = it.longitude,
                    category = category
                )
            }
            .toList()
    }

    /* 유틸함수 */
    private fun View.navToForecast() {
        setOnClickListener {
            val id = args.seashoreId
            val direction = MapFragmentDirections.actionMapToForecast(seashoreId = id)
            findNavController().navigate(direction)
        }
    }

    private fun Int?.toWon(): String =
        this?.let { java.text.NumberFormat.getNumberInstance(java.util.Locale.KOREA).format(it) + "원" } ?: ""

    private fun TextView.setStrike(show: Boolean) {
        paintFlags = if (show) paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
        else paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
    }

    private fun View.showOrGone(show: Boolean) { isVisible = show }

    override fun onResume() {
        super.onResume()
        binding.mapview.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapview.pause()
    }
}