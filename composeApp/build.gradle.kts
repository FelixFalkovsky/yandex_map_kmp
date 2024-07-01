plugins {
    alias(libs.plugins.android)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.compose.core)
    alias(libs.plugins.compose.compiler)
}

kotlin {

    iosArm64()
    iosX64()
    iosSimulatorArm64()

    androidTarget {

    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        version = "1.0"
        ios.deploymentTarget = "16.0"
        podfile = project.file("../iosApp/Podfile")
        pod("YandexMapsMobile") {
            version = "4.6.1-lite"
        }
        framework {
            baseName = "composeApp"
            isStatic = true
            export(libs.arkivanov.decompose)
            export(libs.arkivanov.essenty.lifecycle)
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.material)

            implementation(libs.arkivanov.decompose)
            implementation(libs.arkivanov.decompose.extensions.compose)

            // Decompose
            api(libs.arkivanov.decompose)
            api(libs.arkivanov.essenty.lifecycle)
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.yandex.map.mobile)
        }
    }
}

android {
    namespace = "com.example.yandex_map_kmp"
    compileSdk = 34
    defaultConfig {
        minSdk = 28
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}