package com.example.yandex_map_kmp

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.yandex_map_kmp.moko.PlaceMarkModel
import com.example.yandex_map_kmp.moko.mokoLocatioData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import yandex_map_kmp.composeapp.generated.resources.Res
import yandex_map_kmp.composeapp.generated.resources.location
import yandex_map_kmp.composeapp.generated.resources.my_location
import yandex_map_kmp.composeapp.generated.resources.run

@Composable
expect fun MapContent(places: List<PlaceMarkModel>, userLocation: Flow<Unit?>)

@Composable
fun MapScreen() {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        MapHolder(
            content = { myLocationClickFlow ->
                MapContent(mokoLocatioData, myLocationClickFlow)
            }
        )
    }
}

@Composable
private fun MapHolder(
    content: @Composable (Flow<Unit?>) -> Unit
) {
    val myLocationStateFlow = MutableSharedFlow<Unit?>(1)

    content(myLocationStateFlow)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        AnimatedVisibility(true) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
//                TextButton(
//                    modifier = Modifier.weight(1f),
//                    onClick = { },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//                ) {
//                    Text(
//                        text = "Start",
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = Color.White
//                    )
//                }
                IconButton(
                    onClick = { myLocationStateFlow.tryEmit(Unit) },
                    enabled = true,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        disabledContainerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.clip(CircleShape)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.location),
                        contentDescription = stringResource(Res.string.run)
                    )
                }
            }
        }
    }
}