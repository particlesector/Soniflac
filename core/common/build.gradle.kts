plugins {
    id("soniflac.android.library")
}

android {
    namespace = "com.particlesector.soniflac.core.common"
}

dependencies {
    implementation(libs.javax.inject)

    testImplementation(libs.junit5)
    testImplementation(libs.coroutines.test)
}
