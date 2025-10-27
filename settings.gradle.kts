pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.scijava.org/content/repositories/public/")
        jcenter()
    }
}
dependencyResolutionManagement {
    //repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.scijava.org/content/repositories/public/")

        jcenter()

        maven("https://maven.aliyun.com/nexus/content/repositories/central/")

    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")


include(":app")
rootProject.name="whatismyipaddress"

include(":library")
include(":smilerating")

include(":features:ui")
project(":features:ui").projectDir = File("../WalhallaUI\\features\\ui")

include(":features:wads")
project(":features:wads").projectDir = File("../WalhallaUI\\features\\wads\\")


include(":threader")
project(":threader").projectDir = File("D:\\walhalla\\sdk\\android\\multithreader\\threader\\")

include(":shared")
project(":shared").projectDir = File("../WalhallaUI\\shared")

//D:\walhalla\sdk\android\multithreader\threader
//include(":app:test")

include(":extensiblepageindicator")