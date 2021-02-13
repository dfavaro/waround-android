package com.danielefavaro.waround.base.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider

class ViewModelsFactory constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator = creators[modelClass]

        if (creator == null) {
            creators.forEach {
                if (modelClass.isAssignableFrom(it.key)) {
                    creator = it.value
                    return@forEach
                }
            }
        }

        val finalCreator =
            creator ?: throw IllegalArgumentException("Unknown model class $modelClass")

        return finalCreator.get() as T
    }
}