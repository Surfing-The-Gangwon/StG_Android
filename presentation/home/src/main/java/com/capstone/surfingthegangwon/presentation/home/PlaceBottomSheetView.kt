package com.capstone.surfingthegangwon.presentation.home

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.doOnLayout
import com.capstone.surfingthegangwon.presentation.home.databinding.BottomsheetPlaceViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView

data class PlaceUiModel(
    val title: String,
    val address: String,
    val phone: String? = null,
    val lat: Double,
    val lng: Double,
)

class PlaceBottomSheetView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : MaterialCardView(context, attrs) {

    private val binding: BottomsheetPlaceViewBinding =
        BottomsheetPlaceViewBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var behavior: BottomSheetBehavior<PlaceBottomSheetView>
    private var allowExpand = false

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        behavior = BottomSheetBehavior.from(this)
        behavior.isHideable = false
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED

        // 접힘 높이를 콘텐츠 높이로 고정
        binding.bsContent.doOnLayout {
            behavior.peekHeight = it.height
        }

        // 기본은 확장 잠금
        unlock()
    }

    override fun onDetachedFromWindow() {
        // 콜백 제거해서 누수 방지
        if (::behavior.isInitialized) behavior.removeBottomSheetCallback(lockCb)
        super.onDetachedFromWindow()
    }

    fun bind(model: PlaceUiModel, onRouteClick: ((PlaceUiModel) -> Unit)? = null) = with(binding) {
        bsPlaceTitle.text = model.title
        bsAddress.text = model.address

        bsPhone.text = model.phone.orEmpty()
        bsPhone.visibility = if (model.phone.isNullOrBlank()) View.GONE else View.VISIBLE

        bsKakaoRouteBtn.setOnClickListener { onRouteClick?.invoke(model) }

        // 콘텐츠 갱신 후 peekHeight 재적용
        bsContent.doOnLayout {
            behavior.peekHeight = it.height
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    /** 드래그/확장 잠금 (현재 요구사항) */
    fun lock() {
        allowExpand = false
        if (::behavior.isInitialized) behavior.addBottomSheetCallback(lockCb)
    }

    /** 확장 허용 (탭 화면에서 호출) */
    fun unlock() {
        allowExpand = true
        if (::behavior.isInitialized) behavior.removeBottomSheetCallback(lockCb)
    }

    private val lockCb = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (!allowExpand) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED,
                    BottomSheetBehavior.STATE_HALF_EXPANDED,
                    BottomSheetBehavior.STATE_DRAGGING,
                    BottomSheetBehavior.STATE_SETTLING ->
                        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}
    }
}