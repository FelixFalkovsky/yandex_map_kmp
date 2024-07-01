package com.example.yandex_map_kmp

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
actual fun MapContent(places: List<PlaceMarkModel>) {

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            View(context)
        }
    )
}