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
                id('io.github.gradlebom.generator')
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
                    id('io.github.gradlebom.generator')
                }
                group = 'org.example'
                version = '0.0.1'
                
                bomGenerator {
                    excludedProjects = ['excluded-example-app']
                }
            """.trimIndent()
        )

        createSubproject("example-app")
        createSubproject("excluded-example-app")

        gradleRunner.withArguments(":example-bom:generatePomFileForBomJavaPublication").build()

        val pomContent = (exampleBomProject / "build" / "publications" / "bomJava" / "pom-default.xml")
            .readText()

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

    private fun createSubproject(projectName: String) {
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
}
