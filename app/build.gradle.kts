plugins {
    id("soniflac.android.application")
    id("soniflac.android.application.compose")
    id("soniflac.android.hilt")
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.particlesector.soniflac"

    defaultConfig {
        applicationId = "com.particlesector.soniflac"
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    flavorDimensions += "distribution"
    productFlavors {
        create("gplay") {
            dimension = "distribution"
            buildConfigField("boolean", "IS_FOSS", "false")
        }
        create("foss") {
            dimension = "distribution"
            buildConfigField("boolean", "IS_FOSS", "true")
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:model"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(project(":core:player"))
    implementation(project(":feature:library"))
    implementation(project(":feature:radio"))
    implementation(project(":feature:nowplaying"))
    implementation(project(":feature:streamstats"))
    implementation(project(":feature:settings"))
    implementation(project(":billing"))

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.compose.navigation)
    implementation(libs.hilt.navigation.compose)

    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    testImplementation(libs.junit5)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
}
