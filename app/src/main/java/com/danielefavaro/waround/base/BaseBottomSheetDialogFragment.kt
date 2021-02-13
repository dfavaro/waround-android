package com.danielefavaro.waround.base

import com.danielefavaro.waround.base.util.ViewModelsFactory
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

    @Inject
    lateinit var viewModelsFactory: ViewModelsFactory

}