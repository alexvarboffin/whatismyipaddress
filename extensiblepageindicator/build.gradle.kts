plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
    //kotlin("android") version "2.1.0"
}


android {

    namespace = "com.merhold.extensiblepageindicator"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
//        versionCode = versionCode0
//        versionName = versionName0
    }

    buildTypes {
        getByName("debug") {
            // Конфигурация для debug
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            consumerProguardFiles("consumer-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    //implementation(libs.androidx.core.ktx)
    // implementation(project(":features:ui"))
    // implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    // implementation("androidx.appcompat:appcompat:1.2.0-alpha01")
}

//afterEvaluate {
//    println("@@@@ Configuring publishing tasks... @@@@")
//
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                groupId = "com.walhalla.threader"
//                artifactId = "threader-android-app"
//                //version = versionName0
//
//                if (project.plugins.hasPlugin("com.android.library")) {
//                    from(components["release"])
//                } else {
//                    from(components["java"])
//                }
//
//                val aarPath = if (buildDir.toString().startsWith("/home/jitpack/build/toasty/build")) {
//                    "$buildDir/toasty-threader.aar"
//                } else {
//                    "$buildDir/outputs/aar/threader-release.aar"
//                }
//                artifact(aarPath)
//
//                pom {
//                    description.set("First release")
//                }
//            }
//        }
//
//        repositories {
//            mavenLocal()
//        }
//    }
//
//    tasks.named("build") {
//        dependsOn(tasks.named("publishToMavenLocal"))
//    }
//
//    tasks.named("publishToMavenLocal") {
//        dependsOn(tasks.named("build"))
//    }
//}