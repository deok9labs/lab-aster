plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":application"))
    implementation(project(":adapter-in-web"))
    implementation(project(":adapter-out-logging"))
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
