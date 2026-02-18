pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "SoniFlac"

include(":app")
include(":core:common")
include(":core:model")
include(":core:database")
include(":core:network")
include(":core:player")
include(":core:testing")
include(":feature:library")
include(":feature:radio")
include(":feature:nowplaying")
include(":feature:streamstats")
include(":feature:settings")
include(":billing")
