package io.github.gradlebom

import io.github.gradlebom.dependency.IncludedDependency
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.setProperty

open class BomGeneratorExtension(project: Project) {
    val excludedProjects = project.objects.setProperty<String>()
    var includeDependencies = project.objects.setProperty<IncludedDependency>()
        private set

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

    companion object {
        private const val NAME = "bomGenerator"

        @JvmStatic
        fun of(project: Project): BomGeneratorExtension =
            project.extensions.create(NAME, project)
    }
}
