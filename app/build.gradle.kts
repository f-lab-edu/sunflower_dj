plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.ktlint)
}

android {
    namespace = "com.djyoo.sunflower"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.djyoo.sunflower"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        dataBinding = true
        viewBinding = true
    }

    testOptions {
        unitTests {
            // Robolectric이 XML 레이아웃, 에셋 등을 찾을 수 있게 허용
            isIncludeAndroidResources = true
        }
    }
}

ktlint {
    android.set(true) // 안드로이드 권장 스타일 가이드 적용
    ignoreFailures.set(false) // 스타일 위반 시 빌드를 실패시켜 CI에서 감지
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
    }
}

dependencies {

    implementation(libs.androidx.core.ktx.v1170)
    implementation(libs.androidx.appcompat.v171)
    implementation(libs.material)
    implementation(libs.androidx.core.splashscreen)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit.v130)
    androidTestImplementation(libs.androidx.espresso.core.v370)

    // MockK 테스트 라이브러리 추가
    testImplementation(libs.test.mockk)

    // 코루틴 테스트가 필요한 경우를 대비해 추가 권장
    testImplementation(libs.kotlinx.coroutines.test)

    // 로컬 단위 테스트용 Robolectric 추가
    testImplementation(libs.robolectric)

    // UI 요소 검증을 위한 라이브러리
    testImplementation(libs.androidx.junit.v130)
    testImplementation(libs.androidx.espresso.core.v370)

    // UI 를 위한 libs
    implementation(libs.androidx.viewpager2)
    implementation(libs.google.material)
    implementation(libs.androidx.fragment.ktx)
}
