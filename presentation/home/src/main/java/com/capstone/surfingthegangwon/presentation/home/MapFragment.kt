package com.capstone.surfingthegangwon.presentation.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.capstone.surfingthegangwon.presentation.home.databinding.FragmentMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import java.lang.Exception
import androidx.navigation.fragment.navArgs
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelTextBuilder

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
                }
            }
        )
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
            ),
            Category.FOOD        to StyleBundle(
                normal  = makeStyles(R.drawable.ic_pin_food)!!,
                dim     = makeStyles(R.drawable.ic_pin_food_dim)!!
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


    private fun setupTabs() = with(binding) {
        val tabs = listOf(tabShop, tabSchool, tabTour, tabRestaurant)

        fun select(tab: View) {
            tabs.forEach { it.isSelected = (it === tab) }
            when (tab.id) {
                R.id.tab_shop       -> showMarkers(Category.SURF_SHOP)
                R.id.tab_school     -> showMarkers(Category.SURF_SCHOOL)
                R.id.tab_tour       -> showMarkers(Category.TOURIST)
                R.id.tab_restaurant -> showMarkers(Category.FOOD)
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
private enum class Category { BEACH, SURF_SHOP, SURF_SCHOOL, TOURIST, FOOD }

private data class PlaceMarker(
    val name: String,
    val lat: Double,
    val lng: Double,
    val category: Category
)

private val dummyPlaces = listOf(
    PlaceMarker("죽도해변", 37.9705, 128.7518, Category.BEACH),
    PlaceMarker("죽도 서핑샵 A", 37.98, 128.74, Category.SURF_SHOP),
    PlaceMarker("죽도 서핑샵 B", 37.9809, 128.73, Category.SURF_SHOP),
    PlaceMarker("죽도 서핑스쿨",  37.9712, 128.72, Category.SURF_SCHOOL),
    PlaceMarker("죽도 전망대",    37.9723, 128.76, Category.TOURIST),
    PlaceMarker("죽도 맛집 A",    37.9692, 128.759, Category.FOOD)
)