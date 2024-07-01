import SwiftUI
import composeApp
import YandexMapsMobile

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate
    @Environment(\.scenePhase) private var scenePhase
    private var lifecycleHolder: LifecycleHolder = LifecycleHolder()

    var body: some Scene {
        WindowGroup {
            GeometryReader { geo in
                ComposeViewControllerToSwiftUI(
                    lifecycle: lifecycleHolder.lifecycle,
                    topSafeArea: Float(geo.safeAreaInsets.top),
                    bottomSafeArea: Float(geo.safeAreaInsets.bottom)
                )
                .ignoresSafeArea()
                .onAppear { LifecycleRegistryExtKt.resume(lifecycleHolder.lifecycle) }
                .onDisappear { LifecycleRegistryExtKt.stop(lifecycleHolder.lifecycle) }
            }
        }
    }
}

class AppDelegate: NSObject, UIApplicationDelegate {

    let MAPKIT_API_KEY = " " // <-- Enter your key !

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        YMKMapKit.setApiKey(MAPKIT_API_KEY)
        YMKMapKit.setLocale("ru_RU")
        return true
    }
}
