plugins {
    id("soniflac.android.feature")
}

android {
    namespace = "com.particlesector.soniflac.feature.radio"
}

dependencies {
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:player"))
    implementation(project(":billing"))

    implementation(libs.coil.compose)

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
}
