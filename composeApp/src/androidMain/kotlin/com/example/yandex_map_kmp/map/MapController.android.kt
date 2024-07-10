package com.example.yandex_map_kmp.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PointF
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import com.example.yandex_map_kmp.PlaceMarkModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKit
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.*
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.*
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import com.yandex.runtime.ui_view.ViewProvider
import yandex_map_kmp.composeapp.generated.resources.Res
import yandex_map_kmp.composeapp.generated.resources.location

class MapController(
    private val context: Context
) : UserLocationObjectListener,
    ClusterListener,
    ClusterTapListener,
    MapObjectTapListener,
    LocationListener,
    CameraListener {

    var mapView: MapView
    private var mapKit: MapKit
    private var locationManager: LocationManager? = null
    private var userLocationAccuracyCirceColor: Int? = null
    private var userLocationIconProvider: ImageProvider? = null
    private var userLocationLayer: UserLocationLayer? = null
    private var collection: ClusterizedPlacemarkCollection? = null
    private lateinit var checkLocationPermission: ActivityResultLauncher<Array<String>>
    private val placesSymbols: HashMap<PlacemarkMapObject, PlaceMarkModel> = HashMap()
    private var routeStartLocation = Point(0.0, 0.0)
    private var followUserLocation = false

    var onClickListener: ((Boolean) -> Unit)? = null
    var clusterClick: ((IntArray) -> Unit)? = null

    init {
        MapKitFactory.initialize(this.context)
        mapKit = MapKitFactory.getInstance()
        mapView = MapView(context)

        mapKit.onStart()
        locationManager = mapKit.createLocationManager()
        collection = mapView.mapWindow.map.mapObjects.addClusterizedPlacemarkCollection(this)
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer?.setObjectListener(this)
        mapView.mapWindow.map.addCameraListener(this)
    }

    fun onStart() {
        mapView.onStart()
    }

    fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        locationManager?.unsubscribe(this)
        mapView.mapWindow.map.mapObjects.removeTapListener(this)
    }

    private fun userInterface(enabled: Boolean) {
        followUserLocation = enabled
        setMyLocationEnabled(enabled)
    }

    /**
     * Установить местоположения пользователя.
     */
    private fun setMyLocationEnabled(enabled: Boolean) {
        userLocationLayer?.isVisible = enabled
        userLocationLayer?.isHeadingEnabled = enabled
        cameraUserPosition(enabled)
        subscribeToLocationUpdate(enabled)
    }

    override fun onObjectAdded(userLocationView: UserLocationView) {
        setAnchor()

        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                this.context,
                Res.drawable.location.hashCode()
            )
        )
        userLocationView.pin.useCompositeIcon()

        userLocationIconProvider?.let { imageProvider ->
            userLocationView.arrow.setIcon(imageProvider)
            userLocationView.pin.setIcon(imageProvider)
        }

        userLocationAccuracyCirceColor?.let(userLocationView.accuracyCircle::setFillColor)
    }

    /**
     * Установить маркеры объектов на карту
     */
    fun addMarkersOnMap(places: List<PlaceMarkModel>) {
        collection?.clear()
        val addedPlaceMarks =
            collection?.addEmptyPlacemarks(
                places.map {
                    Point(
                        it.latitude,
                        it.longitude
                    )
                }
            )

        addedPlaceMarks?.forEachIndexed { index, placeMark ->
            val placeMarkItem = places[index]
            placeMark.userData = placeMarkItem
            getClusterItemIcon().let {
                placeMark.setView(it)
            }
            placesSymbols[placeMark] = places[index]
        }

        // Устанавливаем позицию камеры по усредненным значениям
        addedPlaceMarks?.elementAtOrNull(places.size / 2)?.let {
            moveTo(Point(it.geometry.latitude, it.geometry.longitude))
        }

        collection?.addTapListener(this)
        collection?.clusterPlacemarks(42.0, 35)
    }

    /**
     * Подписаться на обновление местоположения пользователя.
     */
    fun checkPermission(enabled: Boolean) {
        if (checkSelfPermission(
                this.context,
                ACCESS_FINE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED ||
            checkSelfPermission(
                this.context,
                ACCESS_COARSE_LOCATION
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            userInterface(enabled)
        } else {
            checkLocationPermission.launch(arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION))
        }
    }

    /**
     *
     */
    private fun cameraUserPosition(enabled: Boolean) {
        if (userLocationLayer?.cameraPosition() != null && enabled) {
            routeStartLocation = userLocationLayer?.cameraPosition()!!.target
            moveTo(routeStartLocation)
        }
    }

    /**
     * Подписаться на обновление местоположения пользователя.
     */
    private fun subscribeToLocationUpdate(enabled: Boolean) {
        if (enabled) {
            locationManager?.subscribeForLocationUpdates(
                0.0,
                1000.0.toLong(),
                1.0,
                false,
                FilteringMode.OFF,
                this
            )
        }
    }

    /**
     * Собрать пины в класстер
     */
    override fun onClusterAdded(cluster: Cluster) {
        val clusterIcon = getClusterIcon(cluster.placemarks.map { it.userData as PlaceMarkModel })
        clusterIcon.let(cluster.appearance::setView)
        cluster.addClusterTapListener(this)
    }

    /**
     * Действие по тапу на кластер
     */
    override fun onClusterTap(cluster: Cluster): Boolean {
        val places = cluster.placemarks.map { (it.userData as PlaceMarkModel).id }.toIntArray()
        clusterClick?.invoke(places)
        onClickListener?.invoke(false)
        return true
    }

    /**
     * Действие на пине
     */
    override fun onMapObjectTap(mapObject: MapObject, point: Point): Boolean {
        val iDs = mutableListOf<Int>()
        if (mapObject is PlacemarkMapObject) {
            placesSymbols[mapObject]?.let {
                iDs.add(it.id)
            }
        }
        clusterClick?.let { it(iDs.toIntArray()) }
        return true
    }

    override fun onCameraPositionChanged(
        map: Map,
        cPos: CameraPosition,
        cUpd: CameraUpdateReason,
        finish: Boolean
    ) {
        if (finish) {
            if (followUserLocation) {
                setAnchor()
                followUserLocation = false
            }
        } else {
            if (!followUserLocation) {
                noAnchor()
            }
        }
    }

    private fun setAnchor() {
        userLocationLayer?.setAnchor(
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.5).toFloat()),
            PointF((mapView.width * 0.5).toFloat(), (mapView.height * 0.5).toFloat())
        )
        followUserLocation = false
    }

    private fun noAnchor() {
        userLocationLayer?.resetAnchor()
    }

    private fun moveTo(point: Point) {
        mapView.mapWindow?.map?.move(
            CameraPosition(
                point,
                12.toFloat(),
                0.toFloat(),
                0.toFloat()
            ),
            Animation(Animation.Type.SMOOTH, 1f),
            null
        )
    }

    /**
     * Установить иконку кластера объектов на карте
     * @param cluster - список объектов на карте
     */
    @SuppressLint("InflateParams")
    private fun getClusterIcon(cluster: List<PlaceMarkModel>): ViewProvider {
        val textView =
            LayoutInflater.from(context).inflate(Res.drawable.location.hashCode(), null).apply {
                (this as? TextView)?.text = cluster.size.toString()
                setBackgroundResource(Res.drawable.location.hashCode())
            }
        return ViewProvider(textView)
    }

    /**
     * Установить иконку кластера объектов на карте
     */
    @SuppressLint("InflateParams")
    private fun getClusterItemIcon() = ViewProvider(
        LayoutInflater.from(context).inflate(Res.drawable.location.hashCode(), null)
    )

    override fun onObjectRemoved(p0: UserLocationView) = Unit

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) = Unit

    override fun onLocationUpdated(location: Location) {
        routeStartLocation = location.position
    }

    override fun onLocationStatusUpdated(status: LocationStatus) = Unit
}