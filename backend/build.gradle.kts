import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test

plugins {
    id("org.springframework.boot") version "4.1.1" apply false
}

allprojects {
    group = "com.deok9labs"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    dependencies {
        add("implementation", platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
        add("testImplementation", platform("org.springframework.boot:spring-boot-dependencies:4.1.1"))
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
