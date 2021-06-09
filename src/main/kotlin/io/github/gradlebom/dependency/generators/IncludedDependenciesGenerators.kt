package io.github.gradlebom.dependency.generators

import org.gradle.api.Project

internal object IncludedDependenciesGenerators {
    fun of(project: Project): Sequence<IncludedDependenciesGenerator> =
        sequenceOf(
            ProjectModulesGenerator(project),
            ExternalDependenciesGenerator()
        )
}
