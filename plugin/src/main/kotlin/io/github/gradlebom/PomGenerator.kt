package io.github.gradlebom

import org.gradle.api.XmlProvider

internal class PomGenerator(xmlProvider: XmlProvider) {
    private val dependencyManagementNode = xmlProvider.createDependencyManagementNode()

    fun generate(dependency: IncludedDependency) {
        dependencyManagementNode.appendNode(DEPENDENCY).apply {
            appendNode(GROUP_ID, dependency.group)
            appendNode(ARTIFACT_ID, dependency.name)
            appendNode(VERSION, dependency.version)
        }
    }

    private fun XmlProvider.createDependencyManagementNode() = asNode()
        .appendNode(DEPENDENCY_MANAGEMENT)
        .appendNode(DEPENDENCIES)

    private companion object {
        const val GROUP_ID = "groupId"
        const val ARTIFACT_ID = "artifactId"
        const val VERSION = "version"

        const val DEPENDENCY_MANAGEMENT = "dependencyManagement"
        const val DEPENDENCIES = "dependencies"
        const val DEPENDENCY = "dependency"
    }
}
