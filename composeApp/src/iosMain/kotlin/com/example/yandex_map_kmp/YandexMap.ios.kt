package com.example.yandex_map_kmp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import cocoapods.YandexMapsMobile.YMKMapKit
import cocoapods.YandexMapsMobile.YMKMapView
import cocoapods.YandexMapsMobile.mapKit
import cocoapods.YandexMapsMobile.sharedInstance
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapContent(places: List<PlaceMarkModel>) {

    var mapKit by remember { mutableStateOf<YMKMapKit?>(null) }
    var mapView by remember { mutableStateOf<YMKMapView?>(null) }
    var mapController by remember { mutableStateOf<MapController?>(null) }

    LaunchedEffect(places.isNotEmpty()) {
        mapController?.addMarkersOnMap(places)
    }

    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            YMKMapKit.sharedInstance().onStart()
            mapView = YMKMapView()
            mapKit = YMKMapKit.mapKit()
            mapController = MapController(mapView!!, mapKit!!)
            mapView!!
        }
    )
}