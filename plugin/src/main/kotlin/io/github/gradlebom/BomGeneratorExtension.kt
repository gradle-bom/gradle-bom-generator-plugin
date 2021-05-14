package io.github.gradlebom

import org.gradle.api.Project
import org.gradle.kotlin.dsl.setProperty

open class BomGeneratorExtension(project: Project) {
    val excludedProjects = project.objects.setProperty<String>()
    var includeDependencies = project.objects.setProperty<IncludedDependency>()
        private set

    internal companion object {
        const val NAME = "bomGenerator"
    }

    fun includeDependency(group: String, name: String, version: String) {
        includeDependencies.add(
            IncludedDependency(group, name, version)
        )
    }

    fun includeDependency(dependency: String) {
        val (group, name, version) = dependency.split(':', limit = 3)
        includeDependencies.add(
            IncludedDependency(group, name, version)
        )
    }
}
