plugins {
    id("soniflac.android.feature")
}

android {
    namespace = "com.particlesector.soniflac.feature.settings"
}

dependencies {
    implementation(project(":core:player"))
    implementation(project(":core:database"))
    implementation(project(":billing"))

    testImplementation(project(":core:testing"))
    testImplementation(libs.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.coroutines.test)
}
