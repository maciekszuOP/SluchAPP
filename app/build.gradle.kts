plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example.sluchapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.sluchapp"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.coil.compose)
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation("com.google.firebase:firebase-auth:23.2.1")
    implementation (libs.jetbrains.kotlinx.coroutines.play.services)
    implementation("com.google.maps.android:maps-compose:4.2.0")
    implementation (libs.accompanist.permissions)  // lub najnowsza wersja
    implementation (libs.jetbrains.kotlinx.coroutines.play.services) // dla await()
    implementation ("com.google.android.gms:play-services-location:21.3.0")  // lokalizacja
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    implementation("com.google.firebase:firebase-firestore-ktx:25.1.4") // Firestore
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1") // jeśli nie masz jeszcze
    implementation("com.google.android.gms:play-services-auth:21.3.0") // do logowania Google

    implementation ("androidx.navigation:navigation-compose:2.9.0")
    // (jeśli używasz Compose)
    implementation(libs.androidx.material)
    implementation(libs.androidx.activity.compose.v182)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}