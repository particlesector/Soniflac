plugins {
    id("soniflac.android.library")
    id("soniflac.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.particlesector.soniflac.core.database"
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.generateKotlin", "true")
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit5)
    testImplementation(libs.room.testing)
    testImplementation(libs.coroutines.test)
}
