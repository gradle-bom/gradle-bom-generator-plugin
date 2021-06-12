package io.github.gradlebom.publication

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

internal object PublicationConfiguratorFactory {
    fun of(project: Project): PublicationConfigurator =
        project.the<PublishingExtension>().publications.withType<MavenPublication>().let { publications ->
            return if (publications.size > 0) {
                ExplicitPublicationConfigurator(publications)
            } else {
                DefaultPublicationConfigurator(project)
            }
        }
}
