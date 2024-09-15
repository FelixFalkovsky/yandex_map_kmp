package com.example.yandex_map_kmp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.yandex_map_kmp.map.MapController

@Composable
actual fun MapContent(places: List<PlaceMarkModel>, userLocation: Boolean) {

    val isLocationEnabled by remember { mutableStateOf(false) }
    var mapController by remember { mutableStateOf<MapController?>(null) }

    LaunchedEffect(isLocationEnabled) {
        mapController?.checkPermission(isLocationEnabled)
    }

    LaunchedEffect(places.isNotEmpty()) {
        mapController?.addMarkersOnMap(places)
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            mapController = MapController(context)
            mapController?.onStart()
            mapController?.mapView!!
        },
        onRelease = {
            mapController?.onStop()
        }
    ) {
        /*Action*/
    }
}