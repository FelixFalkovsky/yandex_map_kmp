package com.example.yandex_map_kmp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapScreen() {

    UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            UIView()
        }
    )
    
}