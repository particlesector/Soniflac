plugins {
    id("soniflac.android.feature")
}

android {
    namespace = "com.particlesector.soniflac.feature.streamstats"
}

dependencies {
    implementation(project(":core:player"))
    implementation(project(":core:database"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)

    testImplementation(project(":core:testing"))
}
