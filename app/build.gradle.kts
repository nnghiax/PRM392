
plugins {
    alias(libs.plugins.android.application)

    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.prm392app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.prm392app"
        minSdk = 24
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase dependencies with BoM
    implementation(platform("com.google.firebase:firebase-bom:33.1.0"))
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-firestore") // Firestore
    implementation("com.google.firebase:firebase-auth") // Authentication
    implementation("com.google.firebase:firebase-storage") // Storage
    implementation("com.google.firebase:firebase-database") // Realtime Database (nếu cần)


    // Navigation dependencies (loại bỏ trùng lặp)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // AndroidX dependencies
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.recyclerview) // Sử dụng catalog thay vì version cụ thể

    // Loại bỏ các dòng trùng lặp hoặc không cần thiết
    // implementation("androidx.recyclerview:recyclerview:1.3.2") // Đã thay bằng libs.recyclerview
    // implementation("androidx.cardview:cardview:1.0.0") // Thêm lại nếu cần CardView

    // Test dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}

