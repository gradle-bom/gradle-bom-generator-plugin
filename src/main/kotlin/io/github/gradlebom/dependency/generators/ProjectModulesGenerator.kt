package io.github.gradlebom.dependency.generators

import io.github.gradlebom.BomGeneratorExtension
import io.github.gradlebom.dependency.IncludedDependency
import org.gradle.api.Project

internal class ProjectModulesGenerator(private val project: Project) : IncludedDependenciesGenerator {
    override fun generate(extension: BomGeneratorExtension): Sequence<IncludedDependency> {
        val excludedProjects = extension.excludedProjects.get() + project.name
        val projectSelector = ProjectModulesSelector(excludedProjects)
        return projectSelector.selectProjects(project)
            .map(IncludedDependency.Companion::from)
    }
}
