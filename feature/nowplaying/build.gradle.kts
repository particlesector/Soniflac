plugins {
    alias(libs.plugins.soniflac.android.library)
    alias(libs.plugins.soniflac.android.compose)
    alias(libs.plugins.soniflac.android.hilt)
}

android {
    namespace = "com.particlesector.soniflac.feature.nowplaying"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:player"))
    implementation(project(":core:common"))

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
