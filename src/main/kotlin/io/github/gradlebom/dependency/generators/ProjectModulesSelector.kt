package io.github.gradlebom.dependency.generators

import org.gradle.api.Project
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin

internal class ProjectModulesSelector(private val excludedProjects: Set<String>) {
    fun selectProjects(project: Project): Sequence<Project> = project.rootProject
        .subprojects
        .asSequence()
        .filter(this::hasMavenPublishPlugin)
        .filter { it.name !in excludedProjects }

    private fun hasMavenPublishPlugin(project: Project): Boolean =
        project.plugins.hasPlugin(MavenPublishPlugin::class.java)
}
