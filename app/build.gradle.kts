plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.airsimapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.airsimapp"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.okhttp)
    implementation(libs.appcompat.v161)
    implementation(libs.androidx.constraintlayout)
    implementation (libs.socket.socket.io.client)
    implementation (libs.picasso)
    implementation (libs.androidx.camera.core.v130)
    implementation(libs.camera.core.v140alpha04)
    implementation(libs.camera.camera2.v140alpha04)
    // If you want to use the CameraX Lifecycle library
    implementation(libs.camera.lifecycle.v140alpha04)
    implementation (libs.androidx.camera.video)

    implementation(libs.play.services.location) //used for mock gps

    implementation (libs.androidx.camera.view.v140alpha04)
    implementation (libs.androidx.camera.extensions)
  //  implementation(libs.quickbirdstudios.opencv.android)

}