import composeApp
import Foundation

class LifecycleHolder : ObservableObject {
    let lifecycle: LifecycleRegistry

    init() {
        lifecycle = LifecycleRegistryKt.LifecycleRegistry()
        lifecycle.onCreate()
    }

    deinit {
        lifecycle.onDestroy()
    }
}
