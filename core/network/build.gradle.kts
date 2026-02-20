plugins {
    alias(libs.plugins.soniflac.android.library)
    alias(libs.plugins.soniflac.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.particlesector.soniflac.core.network"
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.core)
}
