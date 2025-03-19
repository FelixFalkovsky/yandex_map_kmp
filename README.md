# yandex_map_kmp

# YandexMap for KotlinMultiplatform

## Overview
YandexMap is a powerful cross-platform mapping solution built with KotlinMultiplatform, providing seamless integration for both iOS and Android applications. The library offers advanced features including clustering support for efficient handling of multiple map markers.

## Features
- 🗺️ Cross-platform implementation using KotlinMultiplatform
- 📱 Native integration with UIKitView for iOS
- 🤖 Native integration with AndroidView for Android
- 📍 Marker clustering support
- ⚡ High performance map rendering
- 🔄 Smooth platform-specific implementations

## Platform-Specific Implementation

### iOS
Seamlessly integrates with iOS using UIKitView, providing native performance and feel:
```swift
// Example usage in SwiftUI
UIKitView(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val mapView = MapController()
            mapView.onStart()
            mapView
        },
        update = { mapView ->
            mapView.setUserLocation(true)
            mapView.addMarkersOnMap(places)
        },
        onRelease = { mapView ->
            mapView.onStop()
        }
    )
```

### Android
Efficiently implements using AndroidView for native Android experience:
```kotlin
// Example usage in Compose
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
```

## Clustering
The library includes built-in clustering support, automatically grouping nearby markers for improved performance and visual clarity when dealing with large datasets.

## Requirements
- iOS 13.0+
- Android API level 21+
- Kotlin 1.8.0+

## Installation
[Add installation instructions here]

## License
[Add license information here]
