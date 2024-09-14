package com.example.yandex_map_kmp


import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import yandex_map_kmp.composeapp.generated.resources.Res
import yandex_map_kmp.composeapp.generated.resources.my_location


@Composable
expect fun MapContent(places: List<PlaceMarkModel>, userLocation: Boolean)

@Composable
fun MapScreen() {

    var isUserLocation by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart,

    ) {
        MapContent(mokoLocatioData, isUserLocation)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 120.dp)
                .padding(horizontal = 16.dp)
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
//            Button(
//                modifier = Modifier.width(50.dp).height(60.dp),
//                colors = ButtonDefaults.buttonColors(Color.Red),
//                onClick = {
//                    isUserLocation = true
//                }
//            ) {
//                Icon(
//                    painterResource(Res.drawable.my_location),
//                    contentDescription = null,
//                    modifier = Modifier.height(25.dp).width(25.dp)
//                )
//            }
        }
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