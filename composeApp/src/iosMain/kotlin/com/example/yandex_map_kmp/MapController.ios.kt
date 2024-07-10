package com.example.yandex_map_kmp

import cocoapods.YandexMapsMobile.*
import com.example.yandex_map_kmp.UIView.asImage
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGPointMake
import platform.UIKit.UIColor
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class MapController : NSObject(), YMKUserLocationObjectListenerProtocol, YMKMapObjectTapListenerProtocol, YMKClusterTapListenerProtocol,
    YMKClusterListenerProtocol, YMKMapObjectCollectionListenerProtocol, YMKMapCameraListenerProtocol, YMKLocationDelegateProtocol {

    private var mapKit: YMKMapKit = YMKMapKit()
    private var mapView = YMKMapView()
    private val animation = YMKAnimation.animationWithType(YMKAnimationType.YMKAnimationTypeSmooth, 2.0.toFloat())
    private var placesSymbols: HashMap<YMKPlacemarkMapObject, PlaceMarkModel> = HashMap()
    private var routeStartLocation: YMKPoint = YMKPoint.pointWithLatitude(0.0, 0.0)
    private var collection: YMKClusterizedPlacemarkCollection? = null
    private var userLocationLayer: YMKUserLocationLayer? = null
    private var locationManager: YMKLocationManager? = null
    private var userLocationAccuracyCirceColor: Int? = null
    private var userLocationIconProvider: YRTViewProvider? = null

    var clusterClick: ((IntArray) -> Unit)? = null
    var isUserLocationDisable: ((Boolean) -> Unit)? = null

    private var followUserLocation = false

    init {
        mapView.mapWindow?.let {
            collection = it.map.mapObjects.addClusterizedPlacemarkCollectionWithClusterListener(this)
            it.map.mapObjects.addTapListenerWithTapListener(this)
            userLocationLayer = mapKit.createUserLocationLayerWithMapWindow(it)
            it.map.isRotateGesturesEnabled()
        }
        locationManager = mapKit.createLocationManager()
    }

    fun onStart() {
        YMKMapKit.sharedInstance().onStart()
        mapView = YMKMapView()
        mapKit = YMKMapKit.mapKit()
    }

    fun onStop() {
        locationManager?.unsubscribeWithLocationListener(this)
        mapView.mapWindow?.map?.mapObjects?.removeTapListenerWithTapListener(this)
        YMKMapKit.sharedInstance().onStop()
    }

    /**
     * Установить иконку отображения пользователя на карте
     */
    fun setUserLocation(enabled: Boolean) {
        if (enabled) {
            followUserLocation = true
            userLocationLayer?.setVisibleWithOn(true)
            userLocationLayer?.isHeadingEnabled()
            userLocationLayer?.setObjectListenerWithObjectListener(this)
            userLocationLayer?.isAnchorEnabled()
            cameraUserPosition()
            subscribeToLocationUpdate(true)
        }
    }

    private fun cameraUserPosition() {
        if (userLocationLayer?.cameraPosition() != null) {
            routeStartLocation = userLocationLayer?.cameraPosition()!!.target
            moveTo(routeStartLocation)
        }
    }

    /**
     * Создает метку локации пользователя
     */
    override fun onObjectAddedWithView(view: YMKUserLocationView) {
        setAnchor()

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
     * Установить пины кластера на карту
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
     * Добавление жеста на класстер
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
     * Подписаться на обновление местоположения пользователя.
     */
    private fun subscribeToLocationUpdate(enabled: Boolean) {
        if (enabled) {
            locationManager?.subscribeForLocationUpdatesWithDesiredAccuracy(
                0.0,
                1000.0.toLong(),
                1.0,
                false,
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

    private fun moveTo(point: YMKPoint) {
        mapView.mapWindow?.map?.moveWithCameraPosition(
            YMKCameraPosition.cameraPositionWithTarget(
                point,
                12.toFloat(),
                0.toFloat(),
                0.toFloat()
            ),
            animation = animation,
            cameraCallback = null
        )
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
        routeStartLocation = location.position
    }
}
