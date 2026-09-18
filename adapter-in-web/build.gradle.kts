dependencies {
    implementation(project(":application"))
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.1.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
