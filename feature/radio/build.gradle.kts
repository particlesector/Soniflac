plugins {
    alias(libs.plugins.soniflac.android.library)
    alias(libs.plugins.soniflac.android.compose)
    alias(libs.plugins.soniflac.android.hilt)
}

android {
    namespace = "com.particlesector.soniflac.feature.radio"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:common"))

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
