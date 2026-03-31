pluginManagement {
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

rootProject.name = "VirtualSzafa"

include(":app")
include(":backend")
include(":shared:shared-core")
include(":shared:shared-wardrobe")
include(":shared:shared-outfits")
