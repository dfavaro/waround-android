package com.danielefavaro.waround.maps.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.danielefavaro.waround.R
import com.danielefavaro.waround.WaroundApp
import com.danielefavaro.waround.base.OneTimeEvent
import com.danielefavaro.waround.base.StatefulFragment
import com.danielefavaro.waround.base.ktx.collapse
import com.danielefavaro.waround.databinding.MapsFragmentBinding
import com.danielefavaro.waround.maps.ui.adapter.RouteAdapter
import com.danielefavaro.waround.maps.ui.model.DirectionUI
import com.danielefavaro.waround.maps.ui.model.MapsFragmentEvents
import com.danielefavaro.waround.maps.ui.model.MapsFragmentViewState
import com.danielefavaro.waround.maps.ui.model.MapsUI
import com.danielefavaro.waround.maps.ui.viewmodel.MapsViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.PolyUtil
import kotlinx.android.synthetic.main.bottomsheet_direction_layout.*
import kotlinx.android.synthetic.main.maps_fragment.*


private const val MAP_STREET_ZOOM = 15f
private const val MAP_CITY_ZOOM = 10f
private const val MAP_ROUTE_PADDING = 200
private const val USER_MARKER_TAG = "USER_MARKER_TAG"

class MapsFragment : StatefulFragment<MapsFragmentViewState, MapsViewModel>(),
    OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    GoogleMap.OnCameraMoveStartedListener {

    override val viewModel: MapsViewModel by activityViewModels { viewModelsFactory }

    private val routeAdapter = RouteAdapter()
    private var directionBottomSheetBehavior: BottomSheetBehavior<*>? = null
    private var googleMap: GoogleMap? = null

    // user marker
    private var userMarker: Marker? = null

    // nearby articles markers
    private val nearbyArticlesMarkerList: MutableList<Marker> = mutableListOf()

    // selected article route
    private var routeMarkerOriginDestination: Pair<Marker, Marker>? = null
    private var routePolyline: Polyline? = null

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            // Do nothing
        }

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            when (bottomSheet.id) {
                bottomsheetDirectionLayout.id -> when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        googleMap?.setPadding(0, 0, 0, 0)
                        viewModel.onRouteEnd()
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        routeAnimateCamera(bottomSheet.height)
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        routeAnimateCamera(
                            directionBottomSheetBehavior?.peekHeight ?: bottomSheet.height
                        )
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap.apply {
            setMinZoomPreference(MAP_CITY_ZOOM)
            setOnMarkerClickListener(this@MapsFragment)
            setOnCameraMoveStartedListener(this@MapsFragment)
        }
        viewModel.onStartLocationFlow()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = MapsFragmentBinding.inflate(inflater, container, false).apply {
        viewmodel = this@MapsFragment.viewModel
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        directionRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = routeAdapter
        }

        directionBottomSheetBehavior = BottomSheetBehavior.from(bottomsheetDirectionLayout)
        directionBottomSheetBehavior?.addBottomSheetCallback(bottomSheetCallback)
    }

    override fun onAttach(context: Context) {
        (activity?.application as? WaroundApp)?.appComponent?.mapsFractory()?.create()?.inject(this)
        super.onAttach(context)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        viewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun updateUi(state: MapsFragmentViewState) {
        myLocationButton.visibility =
            if (state.isLoading || state.locationCameraUpdates) View.GONE else View.VISIBLE
    }

    override fun onEvent(event: OneTimeEvent) {
        when (event) {
            is MapsFragmentEvents.OnNearbyArticles -> {
                updateNearbyArticlesPosition(event.nearbyArticles)
            }
            is MapsFragmentEvents.OnGenericError -> {
                context?.let {
                    Toast.makeText(it, getString(R.string.generic_error), Toast.LENGTH_SHORT).show()
                }
            }
            is MapsFragmentEvents.OnUserLocation -> {
                updateUserPosition(event.userLocation, moveCamera = event.moveCamera)

                // get nearby articles
                viewModel.onNearbyArticlesRequest(
                    event.userLocation,
                    googleMap?.projection,
                    googleMap?.cameraPosition
                )
            }
            is MapsFragmentEvents.OnLocationPermissionGranted -> {
                // show user position into the map
                viewModel.requestLocationUpdates()
            }
            is MapsFragmentEvents.OnLocationPermissionDenied -> {
                // show somewhere position into the map instead of denied access to the app
                viewModel.getOneShotLocation()
            }
            is MapsFragmentEvents.OnLocationPermissionRequest -> checkLocationPermissions { enabled: Boolean ->
                if (enabled) viewModel.onLocationPermissionGranted()
                else requestLocationPermissions()
            }
            is MapsFragmentEvents.OnMarkerClick -> (event.marker.tag as? Long)?.let { tag ->
                ArticleDetailFragment.newInstance(tag)
                    .show(childFragmentManager, ArticleDetailFragment.TAG)
            }
            is MapsFragmentEvents.OnRouteStart -> {
                startRouteUI(event.directionUI)
                routeAdapter.setRoute(event.directionUI.routeList)
                directionBottomSheetBehavior?.collapse(
                    resources.getDimension(R.dimen.default_bottomsheet_peek_height).toInt()
                )
            }
            is MapsFragmentEvents.OnRouteEnd -> endRouteUI()
        }
    }

    override fun onCameraMoveStarted(reason: Int) {
        when (reason) {
            GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE -> {
                viewModel.onCameraGestureMove()
            }
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        viewModel.onMarkerClick(marker)
        return true
    }

    private fun startRouteUI(directionUI: DirectionUI) {
        userMarker?.remove()
        nearbyArticlesMarkerList.forEach { it.remove() }
        nearbyArticlesMarkerList.clear()

        setArticleRoute(directionUI)
    }

    private fun endRouteUI() {
        routePolyline?.remove()
        routeMarkerOriginDestination?.first?.remove()
        routeMarkerOriginDestination?.second?.remove()

        // TODO improve
        // next location request will update userMarker and nearby articles
        userMarker = null
    }

    // static direction
    // TODO make it dynamically using userMarker instead and move some logic when new location comes up
    private fun setArticleRoute(directionUI: DirectionUI) {
        val startLatLng = LatLng(directionUI.startLat, directionUI.startLng)
        val startMarkerOptions: MarkerOptions = MarkerOptions().apply {
            position(startLatLng)
            title(directionUI.startName)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_person_pin_black_48))
        }
        val endLatLng = LatLng(directionUI.endLat, directionUI.endLng)
        val endMarkerOptions: MarkerOptions = MarkerOptions().apply {
            position(endLatLng)
            title(directionUI.endName)
            icon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_auto_stories_black_24))
        }

        val origin = googleMap?.addMarker(startMarkerOptions)
        val destination = googleMap?.addMarker(endMarkerOptions)
        if (origin != null && destination != null) {
            routeMarkerOriginDestination = Pair(origin, destination)
        }

        val polylineOptions = drawRoute(context)
        val pointsList = PolyUtil.decode(directionUI.overviewPolyline)
        for (point in pointsList) {
            polylineOptions?.add(point)
        }

        routePolyline = googleMap?.addPolyline(polylineOptions)
    }

    fun drawRoute(context: Context?): PolylineOptions? {
        context ?: return null
        val polylineOptions = PolylineOptions()
        polylineOptions.width(resources.getDimension(R.dimen.default_route_polyline_width))
        polylineOptions.geodesic(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            polylineOptions.color(context.resources.getColor(R.color.purple_200, context.theme))
        } else {
            polylineOptions.color(context.resources.getColor(R.color.purple_200))
        }
        return polylineOptions
    }

    private fun routeAnimateCamera(bottomPadding: Int) {
        googleMap?.setPadding(0, 0, 0, bottomPadding)

        routeMarkerOriginDestination?.let { routeMarkerOriginDestination ->
            val bounds: LatLngBounds = LatLngBounds.Builder().apply {
                include(routeMarkerOriginDestination.first.position)
                include(routeMarkerOriginDestination.second.position)
            }.build()

            googleMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_ROUTE_PADDING))
        }
    }

    private fun updateNearbyArticlesPosition(nearbyArticles: List<MapsUI>) {
        // TODO https://developers.google.com/maps/documentation/android-sdk/utility/marker-clustering
        nearbyArticles.forEach { article ->
            googleMap?.addMarker(MarkerOptions().position(LatLng(article.lat, article.lon)))
                ?.apply {
                    tag = article.pageid
                    title = article.title
                    setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_auto_stories_black_24))
                }?.let {
                    nearbyArticlesMarkerList.add(it)
                }
        }
    }

    private fun updateUserPosition(location: LatLng, moveCamera: Boolean) {
        userMarker?.apply {
            position = location
        } ?: run {
            userMarker = googleMap?.addMarker(MarkerOptions().position(location))?.apply {
                tag = USER_MARKER_TAG
                setIcon(BitmapDescriptorFactory.fromResource(R.drawable.baseline_person_pin_black_48))
            }
        }

        if (moveCamera) animateCamera(location)
    }

    private fun animateCamera(location: LatLng) {
        val cameraPosition: CameraPosition = CameraPosition.builder().apply {
            zoom(MAP_STREET_ZOOM)
            target(location)
        }.build()
        googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }

    companion object {
        const val TAG = "MapsFragment"
    }
}