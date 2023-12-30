pluginManagement {
    repositories {
        mavenLocal()
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
//        maven { setUrl("https://maven.aliyun.com/repository/public") }
//        maven { setUrl("https://maven.aliyun.com/repository/central") }
//        maven { setUrl("https://maven.aliyun.com/repository/google") }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "financeCross"