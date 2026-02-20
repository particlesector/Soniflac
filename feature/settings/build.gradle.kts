plugins {
    alias(libs.plugins.soniflac.android.library)
    alias(libs.plugins.soniflac.android.compose)
    alias(libs.plugins.soniflac.android.hilt)
}

android {
    namespace = "com.particlesector.soniflac.feature.settings"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":billing"))
    implementation(project(":core:database"))
    implementation(project(":core:common"))

    implementation(libs.androidx.lifecycle.viewmodel.compose)
}
