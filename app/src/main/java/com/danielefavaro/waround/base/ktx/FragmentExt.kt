package com.danielefavaro.waround.base.ktx

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.danielefavaro.waround.base.util.Event
import com.danielefavaro.waround.base.util.EventObserver


fun <T> Fragment.observe(source: LiveData<T>?, block: (T) -> Unit) {
    source?.observe(viewLifecycleOwner, Observer(block))
}

fun <T> Fragment.observeEvent(
    source: LiveData<Event<T>>?,
    consume: Boolean = true,
    block: (T) -> Unit
) {
    source?.observe(viewLifecycleOwner, EventObserver(consume, block))
}

fun Fragment.dpToPx(dp: Float): Int = (dp * resources.displayMetrics.density).toInt()

fun Fragment.pxToDp(px: Int): Int = (px / resources.displayMetrics.density).toInt()

fun Fragment.spToPx(sp: Float): Int = (sp / resources.displayMetrics.scaledDensity).toInt()

fun Fragment.dpToSp(dp: Float): Float = dpToPx(dp) / resources.displayMetrics.scaledDensity
