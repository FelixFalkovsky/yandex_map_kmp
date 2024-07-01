package com.example.yandex_map_kmp.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.example.yandex_map_kmp.MapScreen
import platform.Foundation.NSUserDefaults
import platform.UIKit.UIViewController

@Suppress("unused", "FunctionName")
fun MainViewController(
    userDefaults: NSUserDefaults,
    lifecycle: LifecycleRegistry,
    topSafeArea: Float,
    bottomSafeArea: Float
): UIViewController {

    return ComposeUIViewController {
        val density = LocalDensity.current

        val topSafeAreaDp = with(density) { topSafeArea.toDp() }
        val bottomSafeAreaDp = with(density) { bottomSafeArea.toDp() }
        val safeArea = PaddingValues(top = topSafeAreaDp + 10.dp, bottom = bottomSafeAreaDp)

        CompositionLocalProvider(LocalSafeArea provides safeArea) {
            MapScreen()
        }
    }
}

private val LocalSafeArea = compositionLocalOf { PaddingValues(0.dp) }