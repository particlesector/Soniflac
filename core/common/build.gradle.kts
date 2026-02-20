plugins {
    alias(libs.plugins.soniflac.android.library)
}

android {
    namespace = "com.particlesector.soniflac.core.common"
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.androidx.core.ktx)
}
