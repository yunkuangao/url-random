import com.github.gradle.node.npm.task.NpmTask

val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("io.ktor.plugin") version "2.1.3"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.github.node-gradle.node") version "3.5.0"
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
    workDir.set(file("${project.projectDir}/.cache/nodejs"))
    npmWorkDir.set(file("${project.projectDir}/.cache/npm"))
    nodeProjectDir.set(file("${project.projectDir}/frontend"))
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
        "${project.projectDir}/frontend/.env",
        "${project.projectDir}/frontend/.prettierrc.js",
        "${project.projectDir}/frontend/rollup.config.js",
        "${project.projectDir}/frontend/package.json",
        "${project.projectDir}/frontend/package-lock.json",
        "${project.projectDir}/frontend/public/global.css",
        "${project.projectDir}/frontend/public/index.html",
        "${project.projectDir}/frontend/public/favicon.png",
    )
    inputs.dir("${project.projectDir}/frontend/scripts")
    inputs.dir("${project.projectDir}/frontend/src")
    outputs.dir("${project.projectDir}/frontend/build")
    copy {
        from("${project.projectDir}/frontend/public")
        into("${project.projectDir}/build/resources/main/static")
    }
}

tasks.getByName("jar").dependsOn(buildTaskUsingNpm)
tasks.getByName("shadowJar").dependsOn(buildTaskUsingNpm)

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
