package io.github.gradlebom.pom

import io.github.gradlebom.dependency.IncludedDependency
import org.gradle.api.XmlProvider
import org.gradle.api.publish.maven.MavenPom

internal class PomGenerator(private val includedDependencies: Sequence<IncludedDependency>) {
    fun generate(pom: MavenPom) {
        pom.withXml(this::generateXml)
    }

    private fun generateXml(xmlProvider: XmlProvider) {
        val dependencyGenerator = xmlProvider
            .asNode()
            .let(::PomDependencyManagementNodeGenerator)
            .generateDependencyManagementNode()
            .let(::PomDependencyNodeGenerator)

        includedDependencies.forEach(dependencyGenerator::generateDependencyNode)
    }
}
