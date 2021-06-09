package io.github.gradlebom.pom

import io.github.gradlebom.dependency.IncludedDependency
import org.gradle.api.XmlProvider

internal class PomGenerator(private val includedDependencies: Sequence<IncludedDependency>) {
    fun generateXml(xmlProvider: XmlProvider) {
        val dependencyGenerator = xmlProvider
            .asNode()
            .let(::PomDependencyManagementNodeGenerator)
            .generateDependencyManagementNode()
            .let(::PomDependencyNodeGenerator)

        includedDependencies.forEach(dependencyGenerator::generateDependencyNode)
    }
}
