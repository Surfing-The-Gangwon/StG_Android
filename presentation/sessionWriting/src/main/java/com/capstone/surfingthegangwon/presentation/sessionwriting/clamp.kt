package com.capstone.surfingthegangwon.presentation.sessionwriting

import androidx.recyclerview.widget.RecyclerView

fun clamp(index: Int, size: Int) = when {
    size <= 0 -> RecyclerView.NO_POSITION
    index < 0 -> 0
    index >= size -> size - 1
    else -> index
}
