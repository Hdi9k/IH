plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "com.example.weibo_huangqiushi"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weibo_huangqiushi"
        minSdk = 26
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)


//    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.12")

    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("org.greenrobot:eventbus:3.2.0")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    implementation ("io.github.cymchad:BaseRecyclerViewAdapterHelper4:4.1.4")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation  ("io.github.scwang90:refresh-layout-kernel:2.1.0")      //核心必须依赖
    implementation  ("io.github.scwang90:refresh-header-classics:2.1.0")    //经典刷新头
    implementation  ("io.github.scwang90:refresh-footer-classics:2.1.0")    //经典加载
    implementation ("cn.jzvd:jiaozivideoplayer:7.7.0")
    implementation ("io.github.FlyJingFish.OpenImage:OpenImageGlideLib:2.2.1")

//    implementation ("com.amazonaws:aws-android-sdk-s3:2.64.0")
//    implementation ("com.amazonaws:aws-android-sdk-SERVICE:2.64.0")
//    implementation ("com.amazonaws:aws-android-sdk-mobile-client:2.64.0")


//    implementation ("com.github.CarGuo.GSYVideoPlayer:GSYVideoPlayer:v8.6.0-release-jitpack")
//    implementation ("com.github.CarGuo.GSYVideoPlayer:gsyVideoPlayer-java:v8.6.0-release-jitpack")
}