pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SoniFlac"

include(":app")
include(":billing")
include(":core:common")
include(":core:database")
include(":core:model")
include(":core:network")
include(":core:player")
include(":core:testing")
include(":feature:library")
include(":feature:nowplaying")
include(":feature:radio")
include(":feature:settings")
include(":feature:streamstats")
