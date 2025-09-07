package com.capstone.surfingthegangwon.presentation.home

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.surfingthegangwon.presentation.home.databinding.FragmentMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import java.lang.Exception
import androidx.navigation.fragment.navArgs

class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()

    private val args: MapFragmentArgs by navArgs()

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
        Log.d("arieum", "Received beach name: $beachName")

        setupTabs()

        binding.mapview.start(
            object : MapLifeCycleCallback() {
                override fun onMapDestroy() {
                    // 리소스 정리 필요 시
                }
                override fun onMapError(error: Exception) {
                    // 에러 처리
                }
            },
            object : KakaoMapReadyCallback() {
                override fun onMapReady(kakaoMap: KakaoMap) {
                    // 지도 준비 완료 후 작업
                }
            }
        )
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

    private fun showMarkers(category: Category) {
        // 카테고리에 해당하는 마커만 표시하는 로직 구현
        Log.d("MapFragment", "Showing markers for category: $category")
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

private enum class Category { SURF_SHOP, SURF_SCHOOL, TOURIST, FOOD }

private data class PlaceMarker(
    val name: String,
    val lat: Double,
    val lng: Double,
    val category: Category
)

private val dummyPlaces = listOf(
    PlaceMarker("죽도 서핑샵 A", 37.9699, 128.7515, Category.SURF_SHOP),
    PlaceMarker("죽도 서핑샵 B", 37.9706, 128.7522, Category.SURF_SHOP),
    PlaceMarker("죽도 서핑스쿨",  37.9712, 128.7530, Category.SURF_SCHOOL),
    PlaceMarker("죽도 전망대",    37.9723, 128.7540, Category.TOURIST),
    PlaceMarker("죽도 맛집 A",    37.9692, 128.7508, Category.FOOD)
)
