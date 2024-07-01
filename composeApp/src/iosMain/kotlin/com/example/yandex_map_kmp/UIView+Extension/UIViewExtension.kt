package com.example.yandex_map_kmp.UIView

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.UIGraphicsImageRenderer
import platform.UIKit.UIImage
import platform.UIKit.UIView


/**
 * Конвертирует UIView в UIImage
 * */
@OptIn(ExperimentalForeignApi::class)
fun UIView.asImage(): UIImage {
    val renderer =
        UIGraphicsImageRenderer(bounds = CGRectMake(0.0.toDouble(), 0.0.toDouble(), 50.0.toDouble(), 50.0.toDouble()))
    return renderer.imageWithActions { rendererContext ->
        if (rendererContext != null) {
            layer.renderInContext(rendererContext.CGContext)
        }
    }
}