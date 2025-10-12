plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.wandroid.traceroute"

    buildFeatures {
        viewBinding = true
    }

    compileSdk = 36

    defaultConfig {
        minSdk = 21
        targetSdk = 36
        //versionCode = 1
        //versionName = "1.0"

        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        ndk {
            abiFilters += setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")
        }
    }

    buildTypes {
        release {
            // isMinifyEnabled = true
            // proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    externalNativeBuild {
        ndkBuild {
            path = file("src/main/jni/Android.mk")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.runner)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.kotlin.stdlib.jdk8)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    implementation(libs.minidns.hla)
}

//task generateSourcesJar(type: Jar) {
//    group = 'jar'
//    from android.sourceSets.main.java.srcDirs
//    classifier = 'sources'
//}

//task javadoc(type: Javadoc) {
//    source = android.sourceSets.main.java.srcDirs
//    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
//}

//task dokkaJavadoc(type: org.jetbrains.dokka.gradle.DokkaTask) {
//    outputFormat = "javadoc"
//    outputDirectory = javadoc.destinationDir
//}

//task generateJavadoc(type: Jar, dependsOn: dokkaJavadoc) {
//    group = 'jar'
//    classifier = 'javadoc'
//    from javadoc.destinationDir
//}

//artifacts {
//    archives generateJavadoc
//    archives generateSourcesJar
//}

//publish {
//    userOrg = 'angelwangjing'
//    groupId = 'com.wandroid'
//    artifactId = 'traceroute-for-android'
//    publishVersion = '1.0.1'
//    desc = 'traceroute with jni on android'
//    website = 'https://github.com/wangjing53406/traceroute-for-android'
//}