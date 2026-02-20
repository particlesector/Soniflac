plugins {
    alias(libs.plugins.soniflac.android.library)
}

android {
    namespace = "com.particlesector.soniflac.core.testing"
}

dependencies {
    // Expose testing dependencies as api() so any module that depends on
    // core:testing automatically gets these on its test classpath.
    api(libs.junit5.api)
    api(libs.junit5.engine)
    api(libs.mockk)
    api(libs.turbine)
    api(libs.coroutines.test)

    implementation(project(":core:model"))
}
