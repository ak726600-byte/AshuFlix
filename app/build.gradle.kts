plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.ashu.flix"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ashu.flix"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    signingConfigs {
        if (System.getenv("ASHU_KEYSTORE") != null) {
            create("ashu") {
                storeFile = file(System.getenv("ASHU_KEYSTORE"))
                storePassword = System.getenv("ASHU_STORE_PASSWORD")
                keyAlias = System.getenv("ASHU_KEY_ALIAS")
                keyPassword = System.getenv("ASHU_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            // Uses the CI key if provided, otherwise the debug key so the APK is still installable.
            signingConfig = signingConfigs.findByName("ashu") ?: signingConfigs.getByName("debug")
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
