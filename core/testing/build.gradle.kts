plugins {
    id("soniflac.android.library")
}

android {
    namespace = "com.particlesector.soniflac.core.testing"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:player"))
    implementation(project(":billing"))

    api(libs.junit5)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.coroutines.test)
}
