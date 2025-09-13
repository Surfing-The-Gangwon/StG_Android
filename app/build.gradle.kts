import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    alias(libs.plugins.hilt)
}

fun getApiKey(key: String): String = gradleLocalProperties(rootDir, providers).getProperty(key)

android {
    namespace = "com.capstone.surfingthegangwon"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.capstone.surfingthegangwon"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        buildConfigField("String", "KAKAO_REST_API_KEY", "\"${getApiKey("KAKAO_REST_API_KEY")}\"")
//        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"${getApiKey("KAKAO_NATIVE_APP_KEY")}\"")

        buildConfigField("String", "KAKAO_REST_API_KEY", "\"${getApiKey("KAKAO_REST_API_KEY")}\"")
        buildConfigField("String", "KAKAO_API_KEY", "\"${getApiKey("KAKAO_API_KEY")}\"")

        manifestPlaceholders["SERVICE_BASE_URL"] = getApiKey("SERVICE_BASE_URL")
        manifestPlaceholders["KAKAO_NATIVE_APP_KEY"] = getApiKey("KAKAO_NATIVE_APP_KEY")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"${getApiKey("KAKAO_NATIVE_APP_KEY")}\"")
    }

    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(project(":presentation:main"))
    implementation(project(":presentation:home"))
    implementation(project(":presentation:together"))
    implementation(project(":domain:home"))
    implementation(project(":domain:together"))
    implementation(project(":data:home"))
    implementation(project(":data:together"))
    implementation(project(":core:navigation"))
    implementation(project(":core:resource"))
    implementation(project(":core:ui"))
    implementation(project(":presentation:login"))

    implementation(libs.hilt)
    implementation(libs.kakao.sdk.all)
    kapt(libs.hilt.compiler)
    implementation(libs.kakao.sdk.all)
    implementation(libs.kakao.map)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}