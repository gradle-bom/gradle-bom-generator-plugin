package io.github.gradlebom

import io.github.gradlebom.dependency.generators.IncludedDependenciesGenerators
import io.github.gradlebom.pom.PomGenerator
import io.github.gradlebom.publication.PublicationConfiguratorFactory
import io.github.gradlebom.utils.MavenPublishPluginApplier
import org.gradle.api.Plugin
import org.gradle.api.Project

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

            val publicationConfigurator = PublicationConfiguratorFactory.of(project)
            publicationConfigurator.configurePomWith(pomGenerator)
        }
    }
}
