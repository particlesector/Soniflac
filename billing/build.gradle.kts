plugins {
    id("soniflac.android.library")
    id("soniflac.android.hilt")
}

android {
    namespace = "com.particlesector.soniflac.billing"

    flavorDimensions += "distribution"
    productFlavors {
        create("gplay") {
            dimension = "distribution"
        }
        create("foss") {
            dimension = "distribution"
        }
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    "gplayImplementation"(libs.billing)

    testImplementation(libs.junit5)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.turbine)
}
