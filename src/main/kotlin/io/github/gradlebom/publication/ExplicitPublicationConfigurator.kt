package io.github.gradlebom.publication

import io.github.gradlebom.pom.PomGenerator
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.publish.maven.MavenPublication

internal class ExplicitPublicationConfigurator(
    private val publications: NamedDomainObjectCollection<MavenPublication>
) : PublicationConfigurator {
    override fun configurePomWith(pomGenerator: PomGenerator) {
        publications.configureEach {
            pomGenerator.generate(pom)
        }
    }
}
