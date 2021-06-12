package io.github.gradlebom

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi

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
        // given a sample settings.gradle
        projectDir.withSettingsGradle(
            """
            rootProject.name = 'sample'
            """.trimIndent()
        )

        // and the plugin applied to build.gradle in the root project
        projectDir.withBuildGradle(
            """
            plugins {
                id('io.github.gradlebom.generator-plugin')
            }
            group = 'org.example'
            version = '0.0.1'
            
            // use this instead of --dry-run to get the tasks in the result verification
            // tasks.all { enabled = false }
            """.trimIndent()
        )

        // when gradle build is run
        val result = gradleRunner.buildAndFail()

        // then it should fail & have log message in output
        assertTrue(
            result.output.contains(
                """
                Plugin can't be applied to root project. Create a separate subproject for BOM generation.
                """.trimIndent()
            )
        )
    }

    @Test
    internal fun `should generate BOM file with default publication`() {
        // given setting.gradle file with multiple subprojects
        projectDir.withSettingsGradle(
            """
                include(':example-bom')
                include(':example-app')
                include(':excluded-example-app')
            """.trimIndent()
        )

        // and configured example-bom project with the plugin and excluded subproject
        val exampleBomProject = projectDir.createBomSubProject(
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

        // and configured subprojects
        projectDir.createSubproject("example-app")
        projectDir.createSubproject("excluded-example-app")

        // when gradle generate pom is run
        gradleRunner.runGeneratePom()

        val pom = readPomFileFrom(exampleBomProject)

        // then example-bom have proper artifactId, groupId & version
        // and excluded subproject is not present
        // and subproject that is not excluded is in dependency management
        assertAll(
            { pom.verifyArtifactId("example-bom") },
            { pom.verifyGroupId("org.example") },
            { pom.verifyVersion("0.0.1") },
            {
                pom.verifyDependencyManagementNode(1) { elements ->
                    elements.selectFirst("dependency")
                        .verifyDependency(
                            heading = "dependency",
                            groupId = "org.example",
                            artifactId = "example-app",
                            version = "0.0.1"
                        )
                }
            }
        )
    }

    @Test
    internal fun `should generate BOM file with explicit publication configuration`() {
        // given settings.gradle with multiple subprojects
        projectDir.withSettingsGradle(
            """
                include(':example-bom')
                include(':example-app')
            """.trimIndent()
        )

        // and example-bom subproject have explicitly configured publication
        val exampleBomProject = projectDir.createBomSubProject(
            """
                plugins {
                    id('io.github.gradlebom.generator-plugin')
                }
                group = 'org.example'
                version = '0.0.1'
                
                publishing {
                    publications {
                        maven(MavenPublication) {
                            artifactId = 'my-example-bom'
                        }
                    }
                }
            """.trimIndent()
        )

        // and configured example-app subproject
        projectDir.createSubproject("example-app")

        // when gradle generate pom is run for explicit configuration
        gradleRunner.runGeneratePom("maven")

        val pom = readPomFileFrom(exampleBomProject, "maven")

        // then artifactId is from explicit configuration
        // and dependencyManagement node is properly configured
        assertAll(
            { pom.verifyArtifactId("my-example-bom") },
            { pom.verifyGroupId("org.example") },
            { pom.verifyVersion("0.0.1") },
            {
                pom.verifyDependencyManagementNode(1) { elements ->
                    elements.selectFirst("dependency")
                        .verifyDependency(
                            heading = "dependency",
                            groupId = "org.example",
                            artifactId = "example-app",
                            version = "0.0.1"
                        )
                }
            }
        )
    }

    @Test
    internal fun `should generate BOM file with included dependencies`() {
        // given settings.gradle file
        projectDir.withSettingsGradle(
            """
                include(':example-bom')
            """.trimIndent()
        )

        // and example-bom build.gradle with extra dependencies included
        val exampleBomProject = projectDir.createBomSubProject(
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

        // when gradle generate pom is run
        gradleRunner.runGeneratePom()

        val pom = readPomFileFrom(exampleBomProject)

        // then extra dependencies are included in dependencyManagement node
        pom.verifyDependencyManagementNode(2) { elements ->
            assertAll(
                {
                    elements.selectFirst("dependency")
                        .verifyDependency(
                            heading = "first dependency",
                            groupId = "org.other",
                            artifactId = "project",
                            version = "1.0.0"
                        )
                },
                {
                    elements.selectSecond("dependency")
                        .verifyDependency(
                            heading = "second dependency",
                            groupId = "org.different",
                            artifactId = "json",
                            version = "1.0.2"
                        )
                }
            )
        }
    }
}
