package io.github.gradlebom

import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class BomGeneratorPluginTest {
    @Test
    internal fun `plugin registers extension`() {
        val rootProject = ProjectBuilder.builder().build()

        // Create a test project and apply the plugin
        val project = ProjectBuilder.builder()
            .withParent(rootProject)
            .build()
        project.plugins.apply("io.github.gradlebom.generator")

        // Verify the result
        val bomGeneratorExtension = project.extensions.findByName("bomGenerator")
        assertTrue(bomGeneratorExtension is BomGeneratorExtension) {
            "Found bomGenerator extension"
        }

        with(bomGeneratorExtension as BomGeneratorExtension) {
            assertEquals(excludedProjects.get(), emptySet<String>()) {
                "Default excludedProjects is empty set"
            }
        }
    }
}
