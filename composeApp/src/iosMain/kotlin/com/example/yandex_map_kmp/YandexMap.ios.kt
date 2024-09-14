package com.example.yandex_map_kmp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi

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
            mapView.setUserLocation(true)
            mapView.addMarkersOnMap(places)
        },
        onRelease = { mapView ->
            mapView.onStop()
        }
    )
}