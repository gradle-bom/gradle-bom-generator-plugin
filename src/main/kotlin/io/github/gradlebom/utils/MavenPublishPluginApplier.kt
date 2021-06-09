package io.github.gradlebom.utils

import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

internal object MavenPublishPluginApplier {
    fun applyPlugin(project: Project) {
        project.pluginManager.apply(MavenPublishPlugin::class.java)
    }
}
