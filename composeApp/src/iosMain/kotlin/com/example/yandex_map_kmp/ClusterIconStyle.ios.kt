package com.example.yandex_map_kmp

import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreGraphics.CGRectMake
import platform.UIKit.NSLayoutConstraint
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIColor
import platform.UIKit.UIFont
import platform.UIKit.UILabel
import platform.UIKit.UIView

@OptIn(ExperimentalForeignApi::class)
fun uiView(name: String, with: Double, height: Double): UIView {
    val size = CGRectMake(0.0, 0.0, with, height)

    val text = UILabel(size)
    text.text = name
    text.textColor = UIColor.whiteColor
    text.font = UIFont.boldSystemFontOfSize(15.0)
    text.textAlignment = NSTextAlignmentCenter
    text.translatesAutoresizingMaskIntoConstraints = false

    val view = UIView(size)
    view.layer.backgroundColor = UIColor.redColor.CGColor
    view.layer.cornerRadius = 12.0
    view.translatesAutoresizingMaskIntoConstraints = true

    view.addSubview(text)

    NSLayoutConstraint.activateConstraints(
        listOf(
            text.centerXAnchor.constraintEqualToAnchor(view.centerXAnchor, constant = 10.0),
            text.centerYAnchor.constraintEqualToAnchor(view.centerYAnchor, constant = 10.0)
        )
    )

    return view
}