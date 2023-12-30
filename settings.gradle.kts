// extract plugin versions from gradle.properties
val testFxVersion: String by settings
val monocleVersion: String by settings

pluginManagement {
//    repositories {
//        mavenLocal()
//        maven("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
//        maven { setUrl("https://maven.aliyun.com/repository/public") }
//        maven { setUrl("https://maven.aliyun.com/repository/central") }
//        maven { setUrl("https://maven.aliyun.com/repository/google") }
//        mavenCentral()
//    }

    val javafxPluginVersion: String by settings
    val versionsPluginVersion: String by settings
    val macAppBundleVersion: String by settings

    plugins {
        id("org.openjfx.javafxplugin") version javafxPluginVersion
        id("com.github.ben-manes.versions") version versionsPluginVersion
//        id("edu.sc.seis.macAppBundle") version macAppBundleVersion
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("versionCatalog") {
            version("kotlin", "1.9.21")
            version("ktor", "2.3.6")

            version("jetBrainsCompose", "1.5.11")
        }
    }
}

//enableFeaturePreview("VERSION_ORDERING_V2")

rootProject.name = "jgnash"

include(
    "jgnash-bayes",
    "jgnash-resources",
    "jgnash-core",
    "jgnash-convert",
    "jgnash-plugin",
    "jgnash-fx",
    "jgnash-report-core",
    "jgnash-fx-test-plugin",
    "mt940",
    "jgnash-tests",
    "financeCross"
)