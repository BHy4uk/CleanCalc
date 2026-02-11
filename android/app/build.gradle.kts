plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val appId = (project.findProperty("APPLICATION_ID") as String?) ?: "com.neche.cleanCalc"
val versionCodeProp = (project.findProperty("VERSION_CODE") as String?)?.toIntOrNull() ?: 1
val versionNameProp = (project.findProperty("VERSION_NAME") as String?) ?: "1.0.0"

android {
    namespace = "com.neche.cleancalc"
    compileSdk = 35

    defaultConfig {
        applicationId = appId
        minSdk = 24
        targetSdk = 35
        versionCode = versionCodeProp
        versionName = versionNameProp

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        signingConfigs {
            create("release") {
                storeFile = file(project.property("RELEASE_STORE_FILE") as String)
                storePassword = project.property("RELEASE_STORE_PASSWORD") as String
                keyAlias = project.property("RELEASE_KEY_ALIAS") as String
                keyPassword = project.property("RELEASE_KEY_PASSWORD") as String
            }
        }
        release {
            isDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }

    // ВКЛЮЧАЕМ COMPOSE (ключевая часть)
    buildFeatures {
        compose = true
    }

    // СВЯЗКА Kotlin 1.9.24 <-> Compose compiler
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api"
        )
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // Compose BOM (УПРАВЛЯЕТ ВСЕМИ версиями Compose)
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")

    // Activity + Compose
    implementation("androidx.activity:activity-compose:1.9.0")

    // Lifecycle (оставляем только нужное)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:2.8.2")

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    // ВАЖНО: foundation содержит Modifier.weight
    implementation("androidx.compose.foundation:foundation")

    // Material 3 (БЕЗ версии — управляется BOM)
    implementation("androidx.compose.material3:material3")

    // XML launch theme (нужен для старта Activity)
    implementation("com.google.android.material:material:1.11.0")

    // Debug
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
