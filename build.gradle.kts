ext.apply {
    set("minSdkVersion", 21)
    set("kotlin_version", "2.0.0")
    set("okHttpVersion", "4.12.0")
    set("enableLoader", false)

    //okHttpVersion = "3.12.12" //last 19 support version
    //okHttpVersion = "3.14.9"


    //minimum Java 8+ or Android API 21+.
//    RETROFIT_VERSION = "2.11.0"


    //    okHttpVersion = "4.9.3"


}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    //id("org.jetbrains.dokka-android")
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.android.library) apply false
    //alias(libs.plugins.composeCompiler) apply false
}
//buildscript {
//
//    repositories {
//        google()
//        maven { url "https://maven.aliyun.com/nexus/content/repositories/central/" }
//        mavenCentral()
//        mavenLocal()
//        //noinspection JcenterRepositoryObsolete
//        jcenter()
//    }
//    dependencies {
//        classpath "com.android.tools.build:gradle:8.2.2"
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10"
//        classpath "org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.17"
//        //classpath "com.novoda:bintray-release:0.8.1"
//        classpath "com.google.gms:google-services:4.4.2"
//        classpath "com.google.firebase:firebase-crashlytics-gradle:3.0.2"
//    }
//}

//task clean (type: Delete) {
//    delete rootProject . buildDir
//}
