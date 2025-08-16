import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ktlint)
    kotlin("plugin.serialization") version "2.1.0"
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-kapt")
}

android {
    namespace = "com.on.turip"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.on.turip"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 35
        versionCode = 1
        versionName = libs.versions.versionName.get()
        versionCode =
            libs.versions.versionCode
                .get()
                .toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val baseUrl: String = gradleLocalProperties(rootDir, providers).getProperty("base_url")
        buildConfigField(
            "String",
            "BASE_URL",
            "\"$baseUrl\"",
        )
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = ".debug"
            manifestPlaceholders["appName"] = "튜립.debug"
        }

        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            signingConfig = signingConfigs.getByName("debug")
            manifestPlaceholders["appName"] = "튜립"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    testImplementation(libs.assertj.core)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    // coroutine
    implementation(libs.kotlinx.coroutines.core)
    testImplementation(libs.kotlinx.coroutines.test)
    // aac viewmodel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    // retrofit2
    implementation(libs.retrofit)
    implementation(libs.retrofit2.kotlinx.serialization.converter)
    // kotlinx.serialization
    implementation(libs.kotlinx.serialization.json)
    // okhttp
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)
    // coil
    implementation(libs.coil)
    // WebView
    implementation(libs.androidx.webkit)
    // Timber
    implementation(libs.timber)
    // firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.installations)
    // datastore
    implementation(libs.androidx.datastore.preferences)
    // Room
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    // viewpager2
    implementation(libs.androidx.viewpager2)
    // livedata
    implementation(libs.androidx.lifecycle.livedata.ktx)
}
