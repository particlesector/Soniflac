plugins {
    id("soniflac.android.feature")
}

android {
    namespace = "com.particlesector.soniflac.feature.nowplaying"
}

dependencies {
    implementation(project(":core:player"))

    implementation(libs.coil.compose)

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
}
