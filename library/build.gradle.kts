plugins {
    id(Android.Plugin.library)
    id(Kotlin.Plugin.ID.android)
    id(Kotlin.Plugin.ID.kapt)
    id(Kotlin.Plugin.ID.parcelize)
    id(Maven.Plugin.public)
}

group = "com.github.D10NG"
version = "0.0.1"

android {
    compileSdk = Project.compile_sdk

    defaultConfig {
        minSdk = Project.min_sdk
        targetSdk = Project.target_sdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = compose_ver
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(Kotlin.stdlib(kotlin_ver))

    // jetpack compose 框架
    implementation(D10NG.DLBasicJetpackComposeApp("1.1.3-RC01"))

    // 单元测试（可选）
    testImplementation(Test.junit("4.13.2"))
    androidTestImplementation(AndroidX.Test.junit("1.1.3"))
    androidTestImplementation(AndroidX.Test.espresso_core("3.4.0"))

    // CameraX
    api("androidx.camera:camera-camera2:1.0.2")
    api("androidx.camera:camera-lifecycle:1.0.2")
    api("androidx.camera:camera-view:1.0.0-alpha32")

    // Zxing
    api("com.google.zxing:core:3.4.1")
}

afterEvaluate {
    publishing {
        publications {
            create("release", MavenPublication::class) {
                from(components.getByName("release"))
            }
        }
    }
}