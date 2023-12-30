val serializationVersion = "1.3.2"
val ktorVersion = versionCatalog.versions.ktor.get()
val napierVersion = "2.6.1"

plugins {
    kotlin("multiplatform") version versionCatalog.versions.kotlin.get()
//    kotlin("multiplatform")
    //生成序列化代码
    kotlin("plugin.serialization") version versionCatalog.versions.kotlin.get()
}

group = "finance"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    maven { setUrl("https://maven.aliyun.com/repository/public") }
    maven { setUrl("https://maven.aliyun.com/repository/central") }
    maven { setUrl("https://maven.aliyun.com/repository/google") }

    mavenCentral()
    google()
}

kotlin {
    jvm {
        jvmToolchain(11)
        withJava()
        testRuns.named("test") {
            executionTask.configure {
                useJUnitPlatform()
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serializationVersion")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
                implementation("io.ktor:ktor-client-core:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-client-encoding:$ktorVersion")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-serialization:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

//                implementation(project(":crossLibrary"))
//                implementation(project(":cloudData"))
//                implementation(project(":goldMultiplatformLib"))

                implementation("io.github.aakira:napier:$napierVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
            }
        }
        val jvmMain by getting {
            dependencies {
//                implementation(project(":java-lib"))

                implementation("com.google.guava:guava:26.0-android")

                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("com.squareup.okio:okio:3.1.0")

                implementation("com.google.code.gson:gson:latest.release")
                implementation("com.alibaba:fastjson:latest.release")

                implementation("com.google.guava:guava:26.0-android")

                implementation("org.jsoup:jsoup:latest.release")

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")

//                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val jvmTest by getting
    }
}
