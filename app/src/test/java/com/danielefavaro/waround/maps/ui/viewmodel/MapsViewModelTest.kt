package com.danielefavaro.waround.maps.ui.viewmodel

import com.danielefavaro.waround.base.BaseUnitTest
import com.danielefavaro.waround.base.ktx.assertEvents
import com.danielefavaro.waround.base.util.Result
import com.danielefavaro.waround.data.entities.ArticlesModel
import com.danielefavaro.waround.data.entities.ImageModel
import com.danielefavaro.waround.data.entities.PageModel
import com.danielefavaro.waround.maps.ui.model.MapsFragmentEvents
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MapsViewModelTest : BaseUnitTest() {

    @Test
    fun `check getNearbyArticles response`() = runBlocking {
        val result: Result<ArticlesModel> = mapsRepository.getNearbyArticles(0.0, 0.0, 0.0)
        assert(result is Result.Success)
    }

    @Test
    fun `check getPageDetail response`() = runBlocking {
        val result: Result<PageModel> = mapsRepository.getPageDetail(0)
        assert(result is Result.Success)
    }

    @Test
    fun `check getImageInfo response`() = runBlocking {
        val result: Result<ImageModel> = mapsRepository.getImageInfo("")
        assert(result is Result.Success)
    }

    @Test
    fun `check onStartLocationFlow workflow - permission granted`() = runBlocking {
        assert(mapsViewModel.viewState.isLoading)

        mapsViewModel.eventsLiveData.assertEvents(
            listOf(
                MapsFragmentEvents.OnLocationPermissionRequest,
                MapsFragmentEvents.OnLocationPermissionGranted,
                MapsFragmentEvents.OnUserLocation(
                    LatLng(
                        mockedLocation.latitude,
                        mockedLocation.longitude
                    )
                )
            )
        ) {
            mapsViewModel.onStartLocationFlow()
            mapsViewModel.onLocationPermissionGranted()
            mapsViewModel.requestLocationUpdates()
        }

        assert(!mapsViewModel.viewState.isLoading)
    }

    @Test
    fun `check onStartLocationFlow workflow - permission denied`() = runBlocking {
        assert(mapsViewModel.viewState.isLoading)

        mapsViewModel.eventsLiveData.assertEvents(
            listOf(
                MapsFragmentEvents.OnLocationPermissionRequest,
                MapsFragmentEvents.OnLocationPermissionDenied,
                MapsFragmentEvents.OnUserLocation(
                    LatLng(
                        mockedLocation.latitude,
                        mockedLocation.longitude
                    )
                )
            )
        ) {
            mapsViewModel.onStartLocationFlow()
            mapsViewModel.onLocationPermissionDenied()
            mapsViewModel.getOneShotLocation()
        }

        assert(!mapsViewModel.viewState.isLoading)
    }
}