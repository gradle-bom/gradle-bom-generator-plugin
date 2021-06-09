package io.github.gradlebom

import io.github.gradlebom.dependency.generators.IncludedDependenciesGenerators
import io.github.gradlebom.pom.PomGenerator
import io.github.gradlebom.utils.MavenPublishPluginApplier
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

/**
 * BOM Generator Plugin.
 *
 * Generates BOM project for multi-module project with all modules
 * excluding [BomGeneratorExtension.excludedProjects]
 * and including [BomGeneratorExtension.includeDependencies].
 *
 */
class BomGeneratorPlugin : Plugin<Project> {
    override fun apply(bomProject: Project) {
        require(bomProject != bomProject.rootProject) {
            "Plugin can't be applied to root project. Create a separate subproject for BOM generation."
        }

        MavenPublishPluginApplier.applyPlugin(bomProject)
        val extension = BomGeneratorExtension.of(bomProject)
        val includedDependenciesGenerators = IncludedDependenciesGenerators.of(bomProject)

        bomProject.afterEvaluate {
            val pomGenerator = includedDependenciesGenerators
                .flatMap { it.generate(extension) }
                .distinct()
                .let(::PomGenerator)

            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>(PUBLICATION_NAME) {
                        artifactId = name
                        pom.withXml(pomGenerator::generateXml)
                    }
                }
            }
        }
    }

    private companion object {
        const val PUBLICATION_NAME = "bomJava"
    }
}
