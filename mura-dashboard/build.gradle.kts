import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.cyclonedx.bom") version "3.2.2"
    id("org.asciidoctor.jvm.convert") version "4.0.5"
    id("com.github.node-gradle.node") version "7.1.0"
}

group = "com.github.mura-dashboard"
version = "0.0.1-SNAPSHOT"
description = "mura-dashboard"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencyManagement {
    dependencies {
        dependency("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

    compileOnly("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    runtimeOnly("org.postgresql:postgresql")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-restdocs")
    testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")
    testImplementation("org.springframework.boot:spring-boot-starter-security-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter")
    testImplementation("org.testcontainers:testcontainers-postgresql")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}

node {
    version.set("24.14.0")
    download.set(true)
    nodeProjectDir.set(file("frontend"))
}

val npmTest = tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmTest") {
    dependsOn(tasks.named("npmInstall"))
    npmCommand.set(listOf("run", "test"))
    inputs.dir("frontend/src")
    inputs.file("frontend/package.json")
    inputs.file("frontend/vite.config.ts")
    inputs.file("frontend/tsconfig.json")
}

tasks.named("check") {
    dependsOn(npmTest)
}

val npmBuild = tasks.register<com.github.gradle.node.npm.task.NpmTask>("npmBuild") {
    dependsOn(tasks.named("npmInstall"))
    npmCommand.set(listOf("run", "build"))
    inputs.dir("frontend/src")
    inputs.file("frontend/package.json")
    inputs.file("frontend/vite.config.ts")
    inputs.file("frontend/tsconfig.json")
    inputs.file("frontend/tsconfig.build.json")
    inputs.file("frontend/index.html")
    outputs.dir("frontend/dist")
}

tasks.named<Copy>("processResources") {
    dependsOn(npmBuild)
    from("frontend/dist") {
        into("static")
    }
}

tasks.named<BootBuildImage>("bootBuildImage") {
    imageName.set("docker.io/muradashboard/mura-dashboard:latest")

    // to package an internal health check e.g. for docker-compose
    environment.put("BP_HEALTH_CHECKER_ENABLED", "true")
    buildpacks.set(listOf(
        "urn:cnb:builder:paketo-buildpacks/java",
        "docker.io/paketobuildpacks/health-checker:2"))
}