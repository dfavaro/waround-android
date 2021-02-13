package com.danielefavaro.waround.base

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.danielefavaro.waround.base.util.Constants
import com.danielefavaro.waround.base.util.ViewModelsFactory
import javax.inject.Inject

abstract class BaseFragment : Fragment() {

    @Inject
    lateinit var viewModelsFactory: ViewModelsFactory

    protected fun checkLocationPermissions(function: (Boolean) -> Unit) {
        context?.let { context ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                function.invoke(true)
            } else {
                function.invoke(false)
            }
        }
    }

    protected fun requestLocationPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                permissions,
                Constants.LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }
}