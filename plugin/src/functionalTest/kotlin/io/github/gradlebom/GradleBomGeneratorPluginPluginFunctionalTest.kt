package io.github.gradlebom

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div

@ExperimentalPathApi
class GradleBomGeneratorPluginPluginFunctionalTest {
    @TempDir
    lateinit var projectDir: Path

    @Test
    fun `can run task`() {
        // Setup the test build
        val projectDir = (projectDir / "build" / "functionalTest").toFile()
        projectDir.mkdirs()
        projectDir.resolve("settings.gradle").writeText("")
        projectDir.resolve("build.gradle").writeText(
            """
            plugins {
                id('io.github.gradlebom.generator')
            }
        """
        )

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("greeting")
        runner.withProjectDir(projectDir)
        val result = runner.build()

        // Verify the result
        assertTrue(result.output.contains("Hello from plugin 'io.github.gradlebom.generator'"))
    }
}
