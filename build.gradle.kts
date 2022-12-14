import com.github.gradle.node.npm.task.NpmTask

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.7.22"
    id("io.ktor.plugin") version "2.2.1"
    kotlin("plugin.serialization") version "1.7.22"
    id("com.github.node-gradle.node") version "3.5.0"
    id("org.sonarqube") version "3.4.0.2513"
}

group = "me.yunkuangao.random"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    executableDir = ""

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-cors:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

node {
    version.set("16.15.0")
    npmVersion.set("8.10.0")
    npmInstallCommand.set("install")
    distBaseUrl.set("https://nodejs.org/dist")
    download.set(true)
    workDir.set(file(".cache/nodejs"))
    npmWorkDir.set(file(".cache/npm"))
    nodeProjectDir.set(file("frontend"))
}

tasks.npmInstall {
    nodeModulesOutputFilter {
        exclude("notExistingFile")
    }
}

val buildTaskUsingNpm = tasks.register<NpmTask>("buildNpm") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    inputs.files(
        "frontend/.env",
        "frontend/.prettierrc.js",
        "frontend/rollup.config.js",
        "frontend/package.json",
        "frontend/package-lock.json",
        "frontend/public/global.css",
        "frontend/public/index.html",
        "frontend/public/favicon.png",
    )
    inputs.dir("frontend/scripts")
    inputs.dir("frontend/src")
    outputs.dir("frontend/public/build")
}

tasks.clean {
    delete += listOf(
        "frontend/public/build"
    )
}

val frontendCopy = tasks.register<Copy>("frontendCopy") {
    dependsOn(buildTaskUsingNpm)
    from("frontend/public")
    into("build/resources/main/static")
}

tasks.getByName("jar").dependsOn(frontendCopy)
tasks.getByName("shadowJar").dependsOn(frontendCopy)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.getByName<Zip>("distZip").archiveFileName.set("${project.name}.zip")
tasks.getByName<Tar>("distTar").archiveFileName.set("${project.name}.tar")
tasks.getByName<Zip>("shadowDistZip").archiveFileName.set("${project.name}-shadow.zip")
tasks.getByName<Tar>("shadowDistTar").archiveFileName.set("${project.name}-shadow.tar")

tasks.getByName<Jar>("jar").archiveFileName.set("${project.name}.jar")
tasks.getByName<Jar>("shadowJar").archiveFileName.set("${project.name}-all.jar")

sonarqube {
    properties {
        property("sonar.projectKey", "yun-org_url-random_AYUPpSS-upODPxvAYNjM")
        property("sonar.qualitygate.wait", true)
    }
}
