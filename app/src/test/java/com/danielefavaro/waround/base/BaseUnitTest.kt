package com.danielefavaro.waround.base

import android.location.Location
import android.location.LocationManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.danielefavaro.waround.domain.LocationRepository
import com.danielefavaro.waround.maps.di.DaggerMapsComponentTest
import com.danielefavaro.waround.maps.di.MapsComponentTest
import com.danielefavaro.waround.maps.domain.MapsRepository
import com.danielefavaro.waround.maps.ui.viewmodel.MapsViewModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule
import javax.inject.Inject

@ExperimentalCoroutinesApi
open class BaseUnitTest {

    val component: MapsComponentTest = DaggerMapsComponentTest.create()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutineRule = TestCoroutineRule()

    @Inject
    lateinit var mapsRepository: MapsRepository

    @Inject
    lateinit var mockWebServer: MockWebServer

    val mockedLocation = Location(LocationManager.GPS_PROVIDER)
    var locationRepository: LocationRepository = mockk()
    lateinit var mapsViewModel: MapsViewModel

    @Before
    open fun setUp() {
        component.inject(this)
        //mockWebServer.start()

        mockStuff()

        mapsViewModel = MapsViewModel(mapsRepository, locationRepository)
        mapsViewModel = spyk(mapsViewModel)
    }

    @After
    open fun tearDown() {
        mockWebServer.shutdown()
    }

    private fun mockStuff() {
        val mockedLocationFlow = flow {
            emit(mockedLocation)
        }
        coEvery { locationRepository.onLocationAvailability() } returns Any()
        coEvery { locationRepository.requestLocationUpdates() } returns mockedLocationFlow
        coEvery { locationRepository.getOneShotLocation() } returns mockedLocation
    }
}