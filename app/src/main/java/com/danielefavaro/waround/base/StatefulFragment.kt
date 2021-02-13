package com.danielefavaro.waround.base

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import com.danielefavaro.waround.base.ktx.observe
import com.danielefavaro.waround.base.ktx.observeEvent

abstract class StatefulFragment<VS : ViewState, VM : StatefulViewModel<VS>> : BaseFragment() {

    /**
     * The correct [ViewModel][androidx.lifecycle.ViewModel] instance for this
     * [Fragment][androidx.fragment.app.Fragment].
     */
    abstract val viewModel: VM

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe(viewModel.viewStateLiveData) { updateUi(it) }
        observeEvent(viewModel.eventsLiveData) { onEvent(it) }
    }

    /**
     * Renders the view state. Called when the view state changes and the UI should be
     * updated accordingly.
     *
     * Must be implemented in a way so that previous view states do not affect the current
     * state of the UI. In other words, the same view state being set must always result
     * in the same state for the displayed UI.
     *
     * @param state The view state.
     */
    abstract fun updateUi(state: VS)

    /**
     * Handles one-time events emitted by the [ViewModel][androidx.lifecycle.ViewModel].
     *
     * @param event A new event being emitted.
     */
    open fun onEvent(event: OneTimeEvent) {}
}
