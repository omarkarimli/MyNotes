buildscript {
    repositories {
        google()
    }
    dependencies {
        val navVersion = "2.8.1"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
    }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false

//    id("com.google.devtools.ksp") version "2.0.20-1.0.24" apply false
//    id("com.google.devtools.ksp") version "1.9.24-1.0.20" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}