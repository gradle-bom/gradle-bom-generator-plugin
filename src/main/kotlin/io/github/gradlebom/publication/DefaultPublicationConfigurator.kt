package io.github.gradlebom.publication

import io.github.gradlebom.pom.PomGenerator
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

internal class DefaultPublicationConfigurator(private val project: Project) : PublicationConfigurator {
    override fun configurePomWith(pomGenerator: PomGenerator) {
        project.extensions.configure<PublishingExtension> {
            publications {
                create<MavenPublication>(PUBLICATION_NAME) {
                    artifactId = project.name
                    pomGenerator.generate(pom)
                }
            }
        }
    }

    private companion object {
        const val PUBLICATION_NAME = "bomJava"
    }
}
