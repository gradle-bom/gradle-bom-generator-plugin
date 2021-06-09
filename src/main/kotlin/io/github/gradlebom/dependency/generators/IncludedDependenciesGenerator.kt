package io.github.gradlebom.dependency.generators

import io.github.gradlebom.BomGeneratorExtension
import io.github.gradlebom.dependency.IncludedDependency

internal interface IncludedDependenciesGenerator {
    fun generate(extension: BomGeneratorExtension): Sequence<IncludedDependency>
}
