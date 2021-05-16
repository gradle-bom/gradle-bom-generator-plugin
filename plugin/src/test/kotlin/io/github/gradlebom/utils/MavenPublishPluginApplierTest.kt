package io.github.gradlebom.utils

import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class MavenPublishPluginApplierTest {
    @Test
    internal fun `should apply MavenPublishPlugin to a project`() {
        val project = ProjectBuilder.builder().build()

        MavenPublishPluginApplier.applyPlugin(project)

        assertTrue(project.plugins.hasPlugin(MavenPublishPlugin::class.java))
    }
}
