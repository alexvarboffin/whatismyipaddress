plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 22
        // targetSdk = 36
        // versionCode = 2_002_000
        // versionName = "2.2.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

//    buildTypes {
//        register("release") {
//            isMinifyEnabled = false
//            proguardFiles(
//                    getDefaultProguardFile("proguard-android.txt"),
//                    "proguard-rules.txt"
//            )
//        }
//
//        register("debug") {
//            //isDebuggable = true
//        }
//
//        /**
//         * It's my IOT build type for running as builtin app on Android Things.
//         * I use this type of build for testing on huge networks with my Raspberry Pi 3.
//         */
////        register("iot") {
////            initWith(getByName("debug"))
////            //isDebuggable = true
////            applicationIdSuffix = ".iot"
////        }
//    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    namespace = "com.druk.servicebrowser"

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        // compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.rx2dnssd)
    implementation(libs.material3)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.slidingpanelayout)

    implementation(libs.androidx.lifecycle.viewmodel)

    implementation(libs.androidx.cardview)
    implementation(libs.androidx.browser)
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
}
