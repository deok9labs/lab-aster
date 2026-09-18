plugins {
    id("org.springframework.boot")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("aster.jar")
}

dependencies {
    implementation(project(":application"))
    implementation(project(":adapter-in-web"))
    implementation(project(":adapter-out-persistence"))
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-log4j2")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")

    modules {
        module("org.springframework.boot:spring-boot-starter-logging") {
            // 모든 starter의 기본 Logback 의존성을 실행 환경에서 일관되게 Log4j2로 교체한다.
            replacedBy("org.springframework.boot:spring-boot-starter-log4j2", "Use Log4j2 instead of Logback")
        }
    }
}
