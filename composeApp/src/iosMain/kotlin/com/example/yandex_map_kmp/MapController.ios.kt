package com.example.yandex_map_kmp

import cocoapods.YandexMapsMobile.*
import com.example.yandex_map_kmp.UIView.asImage
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPointMake
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIColor
import platform.UIKit.UIView

private const val DEFAULT_CLUSTER_RADIUS = 42.0
private const val DEFAULT_MIN_ZOOM = 35
private const val COMFORTABLE_ZOOM_LEVEL = 15f
private const val COMMON_ZOOM_LEVEL = 10f
private const val DESIRED_ACCURACY = 0.0
private const val MINIMAL_TIME: Long = 1000
private const val MINIMAL_DISTANCE = 1.0
private const val USE_IN_BACKGROUND = false

@OptIn(ExperimentalForeignApi::class)
class MapController : UIView(frame = CGRectMake(.0, .0, .0, .0)),
    YMKUserLocationObjectListenerProtocol,
    YMKMapObjectTapListenerProtocol,
    YMKClusterTapListenerProtocol,
    YMKClusterListenerProtocol,
    YMKMapObjectCollectionListenerProtocol,
    YMKMapCameraListenerProtocol,
    YMKLocationDelegateProtocol
{

    private val mapKit = YMKMapKit.mapKit()
    private var mapView = YMKMapView()
    private val animation = YMKAnimation.animationWithType(YMKAnimationType.YMKAnimationTypeSmooth, 2.0.toFloat())
    private var placesSymbols: HashMap<YMKPlacemarkMapObject, PlaceMarkModel> = HashMap()
    private var collection: YMKClusterizedPlacemarkCollection? = null
    private var userLocationLayer: YMKUserLocationLayer? = null
    private var locationManager: YMKLocationManager? = null
    private var userLocationAccuracyCirceColor: Int? = null
    private var userLocationIconProvider: YRTViewProvider? = null
    private var myLocation: YMKPoint? = null

    var clusterClick: ((IntArray) -> Unit)? = null
    var isUserLocationDisable: ((Boolean) -> Unit)? = null

    private var followUserLocation = true

    init {
        addSubview(mapView)
        locationManager = mapKit.createLocationManager()
        mapView.mapWindow?.let {
            userLocationLayer = mapKit.createUserLocationLayerWithMapWindow(it)
            it.map.isRotateGesturesEnabled()
        }
    }

    fun onStart() {
        YMKMapKit.sharedInstance().onStart()
    }

    fun onStop() {
        locationManager?.unsubscribeWithLocationListener(this)
        mapView.mapWindow?.map?.mapObjects?.removeTapListenerWithTapListener(this)
        YMKMapKit.sharedInstance().onStop()
    }

    /**
     * Set the icon to display the user on the map
     */
    fun setUserLocation(enabled: Boolean) {
        userLocationLayer?.setVisibleWithOn(enabled)
        userLocationLayer?.setHeadingEnabled(enabled)
        subscribeToLocationUpdate(enabled)
    }

    //Move the focus to the user on the map
    fun myLocation() {
        val zoom = mapView.mapWindow?.map?.cameraPosition?.zoom ?: COMFORTABLE_ZOOM_LEVEL
        moveTo(myLocation, zoom)
    }

    /**
     * Creates the user's location label
     */
    override fun onObjectAddedWithView(view: YMKUserLocationView) {
        view.pin.useCompositeIcon()

        userLocationIconProvider?.let { imageProvider ->
            view.arrow.setViewWithView(imageProvider)
            view.pin.setViewWithView(imageProvider)
        }

        userLocationAccuracyCirceColor?.let {
            view.accuracyCircle.strokeWidth = 1.0.toFloat()
            view.accuracyCircle.fillColor = UIColor.blueColor.colorWithAlphaComponent(0.1)
        }
    }

    /**
     * Install the cluster pins on the card
     */
    fun addMarkersOnMap(places: List<PlaceMarkModel>) {
        collection?.clear()

        val addedPlaceMarks: MutableList<YMKPlacemarkMapObject> = mutableListOf()

        places.forEachIndexed { _, placeMarkModel ->
            collection?.let {
                addedPlaceMarks.add(
                    it.addPlacemarkWithPoint(
                        YMKPoint.pointWithLatitude(
                            placeMarkModel.latitude,
                            placeMarkModel.longitude
                        )
                    )
                )
            }
        }

        addedPlaceMarks.forEachIndexed { index, placeMark ->
            val placeMarkItem = places[index]
            placeMark.userData = placeMarkItem
            placeMark.setIconWithImage(uiView("$index", 35.0, 35.0, 12.0).asImage())
            placesSymbols[placeMark] = places[index]
        }

        // Устанавливаем позицию камеры по усредненным значениям
        addedPlaceMarks.elementAtOrNull(places.size / 2)?.let {
            moveTo(YMKPoint.pointWithLatitude(it.geometry.latitude, it.geometry.longitude))
        }

        collection?.addTapListenerWithTapListener(this)
        collection?.clusterPlacemarksWithClusterRadius(60.0, 35.0.toULong())
    }

    /**
     * Adding a gesture to a cluster
     * */
    override fun onClusterAddedWithCluster(cluster: YMKCluster) {
        cluster.appearance.setIconWithImage(
            uiView(
                "${cluster.size}",
                30.0,
                30.0,
                15.0
            ).asImage()
        )
        cluster.addClusterTapListenerWithClusterTapListener(this)
    }

    /**
     * Subscribe to update the user's location.
     */
    private fun subscribeToLocationUpdate(enabled: Boolean) {
        if (enabled) {
            locationManager?.subscribeForLocationUpdatesWithDesiredAccuracy(
                DESIRED_ACCURACY,
                MINIMAL_TIME,
                MINIMAL_DISTANCE,
                USE_IN_BACKGROUND,
                YMKLocationFilteringMode.YMKLocationFilteringModeOff,
                this
            )
        }
    }

    override fun onClusterTapWithCluster(cluster: YMKCluster): Boolean {
        val places = cluster.placemarks as List<YMKPlacemarkMapObject>
        val placesIDs = places.map { (it.userData as PlaceMarkModel).id }.toIntArray()
        clusterClick?.invoke(placesIDs)
        isUserLocationDisable?.invoke(false)
        return true
    }

    override fun onMapObjectTapWithMapObject(mapObject: YMKMapObject, point: YMKPoint): Boolean {
        val pointIDs = mutableListOf<Int>()
        if (mapObject is YMKPlacemarkMapObject) {
            placesSymbols[mapObject]?.let {
                pointIDs.add(it.id)
            }
        }
        clusterClick?.let { it(pointIDs.toIntArray()) }
        return true
    }

    private fun moveTo(point: YMKPoint?, zoom: Float = COMFORTABLE_ZOOM_LEVEL) {
        point?.let {
            mapView.mapWindow?.map?.moveWithCameraPosition(
                YMKCameraPosition.cameraPositionWithTarget(it, zoom, 0.0f, 0.0f),
                animation,
                null
            )
        }
    }

    override fun onCameraPositionChangedWithMap(
        map: YMKMap,
        cameraPosition: YMKCameraPosition,
        cameraUpdateReason: YMKCameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            if (followUserLocation) {
                setAnchor()
                followUserLocation = false
            }
        } else {
            if (!followUserLocation) {
                userLocationLayer?.resetAnchor()
            }
        }
    }

    private fun setAnchor() {
        userLocationLayer?.setAnchorWithAnchorNormal(
            CGPointMake((mapView.mapWindow!!.width() * 0.5), (mapView.mapWindow!!.height() * 0.5)),
            CGPointMake((mapView.mapWindow!!.width() * 0.5), (mapView.mapWindow!!.height() * 0.5))
        )
    }

    override fun onObjectRemovedWithView(view: YMKUserLocationView) {}

    override fun onObjectUpdatedWithView(view: YMKUserLocationView, event: YMKObjectEvent) {}

    override fun onMapObjectAddedWithMapObject(mapObject: YMKMapObject) {}

    override fun onMapObjectRemovedWithMapObject(mapObject: YMKMapObject) = Unit

    override fun onLocationStatusUpdatedWithStatus(status: YMKLocationStatus) = Unit

    override fun onLocationUpdatedWithLocation(location: YMKLocation) {
        myLocation = location.position
    }
}
