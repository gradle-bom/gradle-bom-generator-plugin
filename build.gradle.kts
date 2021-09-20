plugins {
    // Apply the Java Gradle plugin development plugin to add support for developing Gradle plugins
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "0.16.0"

    // Apply the Kotlin JVM plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.4.31"
    // Can't have 1.5.0 until it's fixed https://github.com/gradle/gradle/issues/15020

    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.17.1"

    id("org.jetbrains.dokka") version "1.4.32"
}

group = "io.github.gradlebom"
version = "1.0.1.RC3"

val readableName = "BOM Generator Plugin"
description = "Gradle plugin for generating a bill of materials (BOM) file for multi-module projects."
val repoUrl = "https://github.com/gradle-bom/gradle-bom-generator-plugin"

pluginBundle {
    description = project.description
    website = repoUrl
    vcsUrl = repoUrl
    tags = listOf("bom", "maven", "dependencies", "multi-module")

    mavenCoordinates {
        groupId = project.group.toString()
        artifactId = project.name
        version = project.version.toString()
    }
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()

    // Add kotlinx-html for detekt
    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.4.31"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Use the JUnit 5 test library.
    testImplementation(platform("org.junit:junit-bom:5.7.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.jsoup:jsoup:1.14.1")
}

gradlePlugin {
    // Define the plugin
    @kotlin.Suppress("UNUSED_VARIABLE")
    val bomGenerator by plugins.creating {
        id = "io.github.gradlebom.generator-plugin"
        displayName = readableName
        implementationClass = "io.github.gradlebom.BomGeneratorPlugin"
    }
}

// Add a source set for the functional test suite
val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

gradlePlugin.testSourceSets(functionalTestSourceSet)
configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

// Add a task to run the functional tests
val functionalTest by tasks.registering(Test::class) {
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
}

tasks.withType(Test::class.java) {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.check {
    // Run the functional tests as part of `check`
    dependsOn(functionalTest)
}
