package com.example.yandex_map_kmp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MapContent(places: List<PlaceMarkModel>)

@Composable
fun MapScreen() {

    Row(

    ) {
        
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MapContent(mokoLocatioData)
    }
}


data class PlaceMarkModel(
    var id: Int,
    val latitude: Double,
    val longitude: Double
)

val mokoLocatioData: List<PlaceMarkModel> = listOf(
    PlaceMarkModel(0, 55.7558, 37.6173),
    PlaceMarkModel(1, 55.7311, 37.6556),
    PlaceMarkModel(2, 55.7411, 37.6256),
    PlaceMarkModel(3, 55.7451, 37.6296),
    PlaceMarkModel(4, 55.7251, 37.6396),
    PlaceMarkModel(5, 55.7051, 37.6496)
)