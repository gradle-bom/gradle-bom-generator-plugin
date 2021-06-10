package io.github.gradlebom

import io.github.gradlebom.dependency.IncludedDependency
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.setProperty

/**
 * BOM Generator Plugin's extension.
 *
 * This class allows to configure the plugin.
 *
 * @param project The project that is used for generating BOM.
 * @property excludedProjects Projects excluded from generated BOM.
 * @property includeDependencies External dependencies included in generated BOM.
 */
open class BomGeneratorExtension(project: Project) {
    internal val excludedProjects = project.objects.setProperty<String>()
    internal val includeDependencies = project.objects.setProperty<IncludedDependency>()

    /**
     * Adds an external dependency to BOM.
     *
     * @param group the group of the module to be added as a dependency.
     * @param name the name of the module to be added as a dependency.
     * @param version the optional version of the module to be added as a dependency.
     */
    fun includeDependency(group: String, name: String, version: String) {
        includeDependencies.add(
            IncludedDependency(group, name, version)
        )
    }

    /**
     * Adds external dependency to BOM.
     *
     * @param dependencyNotation notation for the dependency to be added.
     */
    fun includeDependency(dependencyNotation: String) {
        val (group, name, version) = dependencyNotation.split(':', limit = 3)
        includeDependencies.add(
            IncludedDependency(group, name, version)
        )
    }

    /**
     * Adds project to be excluded from BOM
     */
    fun excludeProject(projectName: String) {
        excludedProjects.add(projectName)
    }

    internal companion object {
        private const val NAME = "bomGenerator"

        @JvmStatic
        fun of(project: Project): BomGeneratorExtension =
            project.extensions.create(NAME, project)
    }
}
