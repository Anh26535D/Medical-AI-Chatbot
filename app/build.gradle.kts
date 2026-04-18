import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "edu.hust.medicalaichatbot"
    compileSdk = 36

    defaultConfig {
        applicationId = "edu.hust.medicalaichatbot"
        minSdk = 34
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            val properties = Properties()
            val propertiesFile = project.rootProject.file("local.properties")
            if (propertiesFile.exists()) {
                val input = propertiesFile.inputStream()
                properties.load(input)
                input.close()
            }

            fun getProp(key: String, envName: String, default: String = ""): String {
                val value = properties.getProperty(key) ?: System.getenv(envName) ?: default
                return "\"${value.replace("\"", "\\\"").replace("\n", "\\n")}\""
            }

            buildConfigField("String", "SYSTEM_PROMPT", getProp("AI_SYSTEM_PROMPT", "AI_SYSTEM_PROMPT"))
            buildConfigField("String", "SUMMARY_PROMPT", getProp("AI_SUMMARY_PROMPT", "AI_SUMMARY_PROMPT"))
            buildConfigField("String", "SYMPTOM_CACHE_PROMPT", getProp("AI_SYMPTOM_CACHE_PROMPT", "AI_SYMPTOM_CACHE_PROMPT"))
            buildConfigField("String", "CONTEXT_LOCATION", getProp("AI_CONTEXT_LOCATION", "AI_CONTEXT_LOCATION", "Dưới đây là danh sách các cơ sở y tế/nhà thuốc gần vị trí của tôi nhất: %s"))
            buildConfigField("String", "CONTEXT_SYMPTOMS", getProp("AI_CONTEXT_SYMPTOMS", "AI_CONTEXT_SYMPTOMS", "Các thông tin triệu chứng đã thu thập được: %s. KHÔNG hỏi lại những thông tin này nếu đã rõ ràng."))
            buildConfigField("String", "CONTEXT_SUMMARY", getProp("AI_CONTEXT_SUMMARY", "AI_CONTEXT_SUMMARY", "Tóm tắt bệnh sử trước đó: %s"))
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            val properties = Properties()
            val propertiesFile = project.rootProject.file("local.properties")
            if (propertiesFile.exists()) {
                val input = propertiesFile.inputStream()
                properties.load(input)
                input.close()
            }

            fun getProp(key: String, envName: String, default: String = ""): String {
                val value = properties.getProperty(key) ?: System.getenv(envName) ?: default
                return "\"${value.replace("\"", "\\\"").replace("\n", "\\n")}\""
            }

            buildConfigField("String", "SYSTEM_PROMPT", getProp("AI_SYSTEM_PROMPT", "AI_SYSTEM_PROMPT"))
            buildConfigField("String", "SUMMARY_PROMPT", getProp("AI_SUMMARY_PROMPT", "AI_SUMMARY_PROMPT"))
            buildConfigField("String", "SYMPTOM_CACHE_PROMPT", getProp("AI_SYMPTOM_CACHE_PROMPT", "AI_SYMPTOM_CACHE_PROMPT"))
            buildConfigField("String", "CONTEXT_LOCATION", getProp("AI_CONTEXT_LOCATION", "AI_CONTEXT_LOCATION"))
            buildConfigField("String", "CONTEXT_SYMPTOMS", getProp("AI_CONTEXT_SYMPTOMS", "AI_CONTEXT_SYMPTOMS"))
            buildConfigField("String", "CONTEXT_SUMMARY", getProp("AI_CONTEXT_SUMMARY", "AI_CONTEXT_SUMMARY"))
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.navigation.compose)

    // Paging 3
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.room.paging)

    // Firebase AI Logic (Gemini API for Android)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    implementation(libs.firebase.config)

    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Location
    implementation("com.google.android.gms:play-services-location:21.1.0")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}