plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    kotlin("multiplatform").apply(false)
    kotlin("plugin.serialization").apply(false)
    id("com.android.application").apply(false)
    id("com.android.library").apply(false)
    id("org.jetbrains.compose").apply(false)
    id("org.jlleitschuh.gradle.ktlint").version("12.2.0").apply(false)
}

buildscript {
    dependencies {
        classpath("com.twitter.compose.rules:ktlint:0.0.26")
    }
}
