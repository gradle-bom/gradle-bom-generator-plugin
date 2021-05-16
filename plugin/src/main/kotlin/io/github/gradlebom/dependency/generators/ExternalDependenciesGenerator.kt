package io.github.gradlebom.dependency.generators

import io.github.gradlebom.BomGeneratorExtension
import io.github.gradlebom.dependency.IncludedDependency

internal class ExternalDependenciesGenerator : IncludedDependenciesGenerator {
    override fun generate(extension: BomGeneratorExtension): Sequence<IncludedDependency> {
        return extension.includeDependencies.get().asSequence()
    }
}
