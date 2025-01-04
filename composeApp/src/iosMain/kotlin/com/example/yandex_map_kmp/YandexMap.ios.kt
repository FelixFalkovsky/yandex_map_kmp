package com.example.yandex_map_kmp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import com.example.yandex_map_kmp.moko.PlaceMarkModel
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapContent(
    places: List<PlaceMarkModel>,
    userLocation: Flow<Unit?>
) {
    val coroutineScope = rememberCoroutineScope()
    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val mapView = MapController()
            mapView.onStart()
            mapView
        },
        update = { mapView ->
            mapView.setUserLocation(true)
            mapView.onMapObjectData(places)
            userLocation
                .filterNotNull()
                .onEach { mapView.myLocation() }
                .launchIn(coroutineScope)
        },
        onRelease = { mapView ->
            mapView.onStop()
        }
    )
}