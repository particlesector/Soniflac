plugins {
    alias(libs.plugins.soniflac.android.library)
    alias(libs.plugins.soniflac.android.hilt)
}

android {
    namespace = "com.particlesector.soniflac.billing"

    // Billing module declares the same distribution dimension so gplay/foss
    // implementations are automatically selected to match the app variant.
    flavorDimensions += "distribution"
    productFlavors {
        create("gplay") { dimension = "distribution" }
        create("foss") { dimension = "distribution" }
    }
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Google Play Billing — gplay flavor only
    "gplayImplementation"(libs.billing)
}
