package io.github.gradlebom

import org.gradle.testkit.runner.GradleRunner
import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.readText
import kotlin.io.path.writeText

@ExperimentalPathApi
internal class BomGeneratorPluginFunctionalTest {
    @TempDir
    lateinit var projectDir: Path

    private lateinit var gradleRunner: GradleRunner

    @BeforeEach
    internal fun setup() {
        gradleRunner = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .forwardOutput()
    }

    @Test
    internal fun `must not be applied to root project`() {
        (projectDir / "settings.gradle").writeText(
            """
            rootProject.name = 'sample'
            """.trimIndent()
        )

        (projectDir / "build.gradle").writeText(
            """
            plugins {
                id('io.github.gradlebom.generator-plugin')
            }
            group = 'org.example'
            version = '0.0.1'
            
            // use this instead of --dry-run to get the tasks in the result verification
            tasks.all { enabled = false }
            """.trimIndent()
        )

        val result = gradleRunner.buildAndFail()

        assertTrue(
            result.output.contains(
                """
                Plugin can't be applied to root project. Create a separate subproject for BOM generation.
                """.trimIndent()
            )
        )
    }

    @Test
    internal fun `should generate BOM file`() {
        // Setup the test build
        (projectDir / "settings.gradle").writeText(
            """
                include(':example-bom')
                include(':example-app')
                include(':excluded-example-app')
            """.trimIndent()
        )

        val exampleBomProject = (projectDir / "example-bom").createDirectories()
        (exampleBomProject / "build.gradle").writeText(
            """
                plugins {
                    id('io.github.gradlebom.generator-plugin')
                }
                group = 'org.example'
                version = '0.0.1'
                
                bomGenerator {
                    excludeProject('excluded-example-app')
                }
            """.trimIndent()
        )

        `create a subproject`("example-app")
        `create a subproject`("excluded-example-app")

        gradleRunner.withArguments(":example-bom:generatePomFileForBomJavaPublication").build()

        val pomContent = `read a POM file from`(exampleBomProject)

        val pom = Jsoup.parse(pomContent)

        with(pom.select("dependencyManagement")) {
            assertTrue(isNotEmpty()) {
                "'dependencyManagement' node exists"
            }

            with(select("dependency")) {
                assertEquals(size, 1) {
                    "has single 'dependency' node"
                }

                with(first()) {
                    assertEquals(selectFirst("groupId").text(), "org.example") {
                        "has correct 'groupId'"
                    }
                    assertEquals(selectFirst("artifactId").text(), "example-app") {
                        "has correct 'artifactId'"
                    }
                    assertEquals(selectFirst("version").text(), "0.0.1") {
                        "has correct 'version'"
                    }
                }
            }
        }
    }

    @Test
    internal fun `should generate BOM file with included dependencies`() {
        // Setup the test build
        (projectDir / "settings.gradle").writeText(
            """
                include(':example-bom')
            """.trimIndent()
        )

        val exampleBomProject = (projectDir / "example-bom").createDirectories()
        (exampleBomProject / "build.gradle").writeText(
            """
                plugins {
                    id('io.github.gradlebom.generator-plugin')
                }
                group = 'org.example'
                version = '0.0.1'
                
                bomGenerator {
                    includeDependency('org.other', 'project', '1.0.0')
                    includeDependency('org.different:json:1.0.2')
                }
            """.trimIndent()
        )

        gradleRunner.withArguments(":example-bom:generatePomFileForBomJavaPublication").build()

        val pomContent = `read a POM file from`(exampleBomProject)

        val pom = Jsoup.parse(pomContent)

        with(pom.select("artifactId").first()) {
            assertEquals(text(), "example-bom")
        }

        with(pom.select("groupId").first()) {
            assertEquals(text(), "org.example")
        }

        with(pom.select("version").first()) {
            assertEquals(text(), "0.0.1")
        }

        with(pom.select("dependencyManagement")) {
            assertTrue(isNotEmpty()) {
                "'dependencyManagement' node exists"
            }

            with(select("dependency")) {
                assertEquals(size, 2) {
                    "has two 'dependency' nodes"
                }

                with(first()) {
                    assertEquals(selectFirst("groupId").text(), "org.other") {
                        "first dependency has correct 'groupId'"
                    }
                    assertEquals(selectFirst("artifactId").text(), "project") {
                        "first dependency has correct 'artifactId'"
                    }
                    assertEquals(selectFirst("version").text(), "1.0.0") {
                        "first dependency has correct 'version'"
                    }
                }

                with(get(1)) {
                    assertEquals(selectFirst("groupId").text(), "org.different") {
                        "second dependency has correct 'groupId'"
                    }
                    assertEquals(selectFirst("artifactId").text(), "json") {
                        "second dependency has correct 'artifactId'"
                    }
                    assertEquals(selectFirst("version").text(), "1.0.2") {
                        "second dependency has correct 'version'"
                    }
                }
            }
        }
    }

    private fun `create a subproject`(projectName: String) {
        val exampleAppProject = (projectDir / projectName).createDirectories()
        (exampleAppProject / "build.gradle").writeText(
            """
                plugins {
                    id('java-library')
                    id('maven-publish')
                }
                group = 'org.example'
                version = '0.0.1'
                publishing {
                    publications {
                        mavenJava(MavenPublication) {
                            from(components.java)
                        }
                    }
                }
            """.trimIndent()
        )
    }

    private fun `read a POM file from`(path: Path): String =
        (path / "build" / "publications" / "bomJava" / "pom-default.xml")
            .readText()
}
