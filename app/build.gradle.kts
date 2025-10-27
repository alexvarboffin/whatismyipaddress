import java.text.SimpleDateFormat
import java.util.Date


fun versionCodeDate(): Int {
    return SimpleDateFormat("yyMMdd").format(Date()).toInt()
}
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("com.google.firebase.crashlytics")
    id("com.google.gms.google-services")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.walhalla.whatismyipaddress"

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    bundle {
        density {
            enableSplit = false
        }
        abi {
            enableSplit = false
        }
        language {
            enableSplit = false
        }
    }

    signingConfigs {
        create("config") {
            keyAlias = "whatismyipaddress"
            keyPassword = "@!sfuQ123zpc"
            storeFile = file("keystore/whatismyipaddress.jks")
            storePassword = "@!sfuQ123zpc"
        }
    }

    compileSdk = libs.versions.android.compileSdk.get().toInt()
    buildToolsVersion = libs.versions.android.buildTools.get()

    val versionPropsFile = file("version.properties")

    if (versionPropsFile.canRead()) {
        val code = if (project.hasProperty("enableLoader") &&
            project.properties["enableLoader"].toString().toBoolean()
        ) {
            200400
        } else {
            versionCodeDate()
        }

        defaultConfig {
            buildConfigField("boolean", "ENABLELOADER", "${rootProject.extra["enableLoader"]}")

            vectorDrawables.useSupportLibrary = true
            multiDexEnabled = true
            resConfigs("ru", "en", "uk")

            applicationId = "com.walhalla.whatismyipaddress"
            minSdk = libs.versions.android.minSdk.get().toInt()
            targetSdk = libs.versions.android.targetSdk.get().toInt()
            versionCode = code
            versionName = "1.1.$code"

            setProperty("archivesBaseName", "whatismyipaddress")
            testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"

            externalNativeBuild {
                ndkBuild {
                    cppFlags("")
                }
            }
        }
    } else {
        throw GradleException("Could not read version.properties!")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            //noinspection WrongGradleMethod
            firebaseCrashlytics {
                mappingFileUploadEnabled = false
            }
            signingConfig = signingConfigs.getByName("config")
            versionNameSuffix = "-DEBUG"
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("config")
            isJniDebuggable = false
            //noinspection WrongGradleMethod
            firebaseCrashlytics {
                mappingFileUploadEnabled = true
            }
            isPseudoLocalesEnabled = true
            versionNameSuffix = ".release"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt"
            )
        }
    }

//    lint {
//        isAbortOnError = false
//        disable("InvalidPackage")
//    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/c/Android.mk")
        }
    }
}

//============================================
tasks.register<Copy>("copyAabToBuildFolder") {
    println("mmmmmmmmmmmmmmmmm ${layout.buildDirectory.get()}/outputs/bundle/release")
    println("mmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm")
    val outputDirectory = file("C:/build")
    if (!outputDirectory.exists()) {
        outputDirectory.mkdirs()
    }

    from("${layout.buildDirectory.get()}/outputs/bundle/release") {
        include("*.aab")
    }
    into(outputDirectory)
}

apply(from = "C:\\scripts/copyReports.gradle")
//============================================

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar", "*.aar"), "dir" to "libs")))
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.firebase.ads)
    implementation(libs.play.services.ads)

    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation(project(":features:wads"))
    implementation(project(":features:ui"))
    implementation(project(":library"))
    implementation(project(":threader"))
    implementation(project(":bonjour"))


    implementation("com.github.delight-im:Android-AdvancedWebView:v3.2.1")
    //@@@@implementation("com.github.alexvarboffin:mint-android-app:1.5.2")
    implementation("com.github.ybq:Android-SpinKit:1.4.0")
    implementation(libs.androidx.multidex)
    implementation(libs.google.firebase.crashlytics)
    implementation(libs.google.firebase.analytics)

    // https://mvnrepository.com/artifact/com.github.sujithkanna/smileyrating
    //implementation("com.github.sujithkanna:smileyrating:2.0.0")
    //implementation(project(":smilerating"))
    implementation("com.github.Mk7Lab:SmileyRating:v1.1.3")

    implementation(project(":extensiblepageindicator"))

    // https://mvnrepository.com/artifact/androidx.privacysandbox.ads/ads-adservices
    implementation("androidx.privacysandbox.ads:ads-adservices:1.1.0-beta12")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.daimajia.easing:library:2.4@aar")
    implementation("com.daimajia.androidanimations:library:2.4@aar")
    implementation("com.github.fulvius31:ip-neigh-sdk30:v0.0.2-alpha")
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.gson)
    implementation(libs.androidx.preference.ktx)
    implementation("com.github.stealthcopter:AndroidNetworkTools:0.4.5.3") {
        exclude(group = "com.squareup.okhttp3")
    }

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("org.slf4j:slf4j-api:1.7.32")



    implementation("com.github.bumptech.glide:glide:4.16.0") {
        exclude(group = "com.squareup.okhttp3")
    }
    annotationProcessor(libs.compiler)
    implementation(libs.minidns.hla)
    implementation(libs.onesignal)

    implementation("com.github.andriydruk:rx2dnssd:0.9.17")
}