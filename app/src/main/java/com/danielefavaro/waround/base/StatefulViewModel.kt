package com.danielefavaro.waround.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.danielefavaro.waround.base.ktx.event
import com.danielefavaro.waround.base.util.Event

/**
 * This is simply a marker interface so that classes that represent events to be dispatched from
 * a [ViewModel] are clearly identifiable and that the associated methods don't deal with the very
 * broad [Any] type.
 */
interface OneTimeEvent

/**
 * This is simply a marker interface so that classes that represent view states to be dispatched
 * from a [ViewModel] are clearly identifiable and that the associated methods don't deal with the
 * very broad [Any] type.
 */
interface ViewState

/**
 * A [ViewModel] base class intended to work together with [StatefulFragment]s that provides:
 * - safe view state management via [LiveData]
 * - one-time event support via [Event] implementation
 *
 * @see StatefulFragment
 * @see StatefulDialogFragment
 */
abstract class StatefulViewModel<VS : ViewState>(initialState: VS) : ViewModel() {

    /**
     * The [LiveData] to be observed by [StatefulFragment] to receive event updates.
     * Observing this also in your [Fragment][androidx.fragment.app.Fragment] implementation
     * will lead to unpredicted behaviors.
     */
    val eventsLiveData: LiveData<Event<OneTimeEvent>> get() = _eventsLiveData
    private val _eventsLiveData = MutableLiveData<Event<OneTimeEvent>>()

    /**
     * The [LiveData] to be observed by [StatefulFragment] to receive state updates.
     * Observing this also in your [Fragment][androidx.fragment.app.Fragment] implementation
     * will lead to unpredicted behaviors.
     */
    val viewStateLiveData: LiveData<VS> get() = _viewStateLiveData
    private val _viewStateLiveData = MutableLiveData<VS>()

    /**
     * The latest view state. If you need to set the state see [setState].
     */
    val viewState: VS get() = _viewStateLiveData.value!!

    init {
        _viewStateLiveData.value = initialState
    }

    /**
     * Sets the new state to be rendered. Executes [update] with the current view state and sets
     * the new state with the updated version.
     */
    protected fun setState(update: (VS) -> VS) {
        _viewStateLiveData.value = update(viewState)
    }

    /**
     * Emits a new [OneTimeEvent].
     */
    protected fun sendEvent(event: OneTimeEvent) {
        _eventsLiveData.event(event)
    }
}
