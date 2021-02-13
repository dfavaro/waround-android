package com.danielefavaro.waround.base.ktx

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.danielefavaro.waround.base.util.Event

/**
 * Convenience method to emit an [Event] via [LiveData.setValue].
 */
fun <T> MutableLiveData<Event<T>>.event(content: T) {
    value = Event(content)
}