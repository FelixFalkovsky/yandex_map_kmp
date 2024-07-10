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
actual fun MapContent(
    places: List<PlaceMarkModel>,
    userLocation: Boolean
) {

    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val mapView = MapController()
            mapView.onStart()
            mapView
        },
        update = { mapView ->
            mapView.setUserLocation(userLocation)
            mapView.addMarkersOnMap(places)
        },
        onRelease = { mapView ->
            mapView.onStop()
        }
    )
}