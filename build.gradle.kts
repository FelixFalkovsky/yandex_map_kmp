plugins {
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.multiplatform).apply(false)
    alias(libs.plugins.android).apply(false)
    alias(libs.plugins.cocoapods).apply(false)
    alias(libs.plugins.compose.core).apply(false)
    alias(libs.plugins.compose.compiler).apply(false)
    alias(libs.plugins.libres).apply(false)
}
