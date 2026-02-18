plugins {
    id("soniflac.android.library")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.particlesector.soniflac.core.model"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit5)
}
