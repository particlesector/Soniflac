plugins {
    alias(libs.plugins.soniflac.android.library)
    alias(libs.plugins.soniflac.android.hilt)
}

android {
    namespace = "com.particlesector.soniflac.core.player"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:database"))

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.session)
    implementation(libs.media3.ui)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
}
