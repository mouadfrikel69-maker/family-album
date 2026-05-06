plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.rork.kin"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.rork.kin"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    // A real release signing config is wired up only when KIN_RELEASE_KEYSTORE is set
    // (e.g. on CI / a signing machine). Without it, the release variant intentionally
    // has no signingConfig so an unsigned release build fails fast — much safer than
    // silently shipping an APK signed with the debug key.
    val releaseKeystore: String? = System.getenv("KIN_RELEASE_KEYSTORE")
        ?: project.findProperty("KIN_RELEASE_KEYSTORE") as String?
    if (releaseKeystore != null) {
        signingConfigs {
            create("release") {
                storeFile = file(releaseKeystore)
                storePassword = (System.getenv("KIN_RELEASE_KEYSTORE_PASSWORD")
                    ?: project.findProperty("KIN_RELEASE_KEYSTORE_PASSWORD") as String?)
                keyAlias = (System.getenv("KIN_RELEASE_KEY_ALIAS")
                    ?: project.findProperty("KIN_RELEASE_KEY_ALIAS") as String?)
                keyPassword = (System.getenv("KIN_RELEASE_KEY_PASSWORD")
                    ?: project.findProperty("KIN_RELEASE_KEY_PASSWORD") as String?)
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // Only assign the release signingConfig if we actually defined one.
            // Otherwise leave it null so an attempt to ship a release build without
            // a signing key fails loudly rather than producing a debug-signed APK.
            signingConfig = signingConfigs.findByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        // minSdk is 24 but the codebase uses java.time everywhere, which is
        // API 26+. Enable core-library desugaring so LocalDate / LocalDateTime
        // / Duration etc. work on API 24/25 instead of crashing at runtime.
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.zxing.core)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.exifinterface)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    debugImplementation(libs.androidx.ui.tooling)
}
