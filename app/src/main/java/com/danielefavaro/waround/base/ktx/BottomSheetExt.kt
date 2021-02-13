package com.danielefavaro.waround.base.ktx

import com.google.android.material.bottomsheet.BottomSheetBehavior

fun BottomSheetBehavior<*>.expand() = apply {
    state = BottomSheetBehavior.STATE_EXPANDED
}

fun BottomSheetBehavior<*>.collapse(peekHeight: Int?) = apply {
    setPeekHeight(peekHeight ?: getPeekHeight())
    state = BottomSheetBehavior.STATE_COLLAPSED
}

fun BottomSheetBehavior<*>.hide() = apply {
    state = BottomSheetBehavior.STATE_HIDDEN
}