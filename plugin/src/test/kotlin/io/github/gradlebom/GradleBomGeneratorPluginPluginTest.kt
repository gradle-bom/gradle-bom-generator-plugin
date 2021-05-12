package io.github.gradlebom

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class GradleBomGeneratorPluginPluginTest {
    @Test
    fun `plugin registers task`() {
        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.github.gradlebom.generator")

        // Verify the result
        assertNotNull(project.tasks.findByName("generator"))
    }
}
