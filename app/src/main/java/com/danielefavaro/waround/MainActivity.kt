package com.danielefavaro.waround

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.danielefavaro.waround.base.util.Constants
import com.danielefavaro.waround.maps.ui.MapsFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as WaroundApp).appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MapsFragment(), MapsFragment.TAG)
                .commitNow()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.LOCATION_PERMISSION_REQUEST_CODE -> {
                supportFragmentManager.findFragmentByTag(MapsFragment.TAG)?.apply {
                    onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}