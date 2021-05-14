package io.github.gradlebom

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

class BomGeneratorPlugin : Plugin<Project> {
    override fun apply(bomProject: Project) {
        require(bomProject != bomProject.rootProject) {
            "Plugin can't be applied to root project. Create a separate subproject for BOM generation."
        }

        applyMavenPublishingPlugin(bomProject)

        val extension = createBomGeneratorExtension(bomProject)

        bomProject.afterEvaluate {
            val excludedProjects = extension.excludedProjects.get() + name
            val includeDependencies = extension.includeDependencies.get()
            val projectSelector = ProjectSelector(excludedProjects)

            extensions.configure<PublishingExtension> {
                publications {
                    create<MavenPublication>(PUBLICATION_NAME) {
                        artifactId = name
                        pom.withXml {
                            val pomGenerator = PomGenerator(this)

                            projectSelector.selectProjects(bomProject)
                                .map(IncludedDependency.Companion::from)
                                .toSet()
                                .plus(includeDependencies)
                                .forEach(pomGenerator::generate)
                        }
                    }
                }
            }
        }
    }

    private fun createBomGeneratorExtension(bomProject: Project) =
        bomProject.extensions.create<BomGeneratorExtension>(BomGeneratorExtension.NAME, bomProject)

    private fun applyMavenPublishingPlugin(bomProject: Project) {
        bomProject.pluginManager.apply(MavenPublishPlugin::class.java)
    }

    private companion object {
        const val PUBLICATION_NAME = "bomJava"
    }
}
