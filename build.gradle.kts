plugins {
    id("com.github.ben-manes.versions")
}

allprojects {
    repositories {
        mavenLocal()
        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/central") }
        maven { setUrl("https://maven.aliyun.com/repository/google") }

        mavenCentral()
        jcenter()
    }

    apply(plugin = "java")
}

subprojects {
    group = "jgnash"
    version = "3.6.0"
}