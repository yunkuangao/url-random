import com.github.gradle.node.npm.task.NpmTask

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.7.20"
    id("io.ktor.plugin") version "2.1.2"
    kotlin("plugin.serialization") version "1.7.20"
    id("com.github.node-gradle.node") version "3.4.0"
}

group = "me.yunkuangao.random"
version = "0.0.1"
application {
    mainClass.set("me.yunkuangao.random.ApplicationKt")

    executableDir = ""

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
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

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cors:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.npmInstall {
    nodeModulesOutputFilter {
        exclude("notExistingFile")
    }
}

val buildTaskUsingNpm = tasks.register<NpmTask>("buildNpm") {
    dependsOn(tasks.npmInstall)
    npmCommand.set(listOf("run", "build"))
    inputs.dir("src")
    outputs.dir("${buildDir}/npm-output")
    outputs.upToDateWhen { false }
}

tasks.getByName("distZip").dependsOn(buildTaskUsingNpm)

distributions {
    main {
        contents {
            from("frontend/public") {
                into("frontend/public")
            }
        }
    }
}
