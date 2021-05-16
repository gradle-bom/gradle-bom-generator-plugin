package io.github.gradlebom.pom

import groovy.util.Node
import io.github.gradlebom.dependency.IncludedDependency

internal class PomDependencyNodeGenerator(
    private val dependencyManagementNode: Node
) {
    fun generateDependencyNode(dependency: IncludedDependency) {
        dependencyManagementNode.appendNode(DEPENDENCY).apply {
            appendNode(GROUP_ID, dependency.group)
            appendNode(ARTIFACT_ID, dependency.name)
            appendNode(VERSION, dependency.version)
        }
    }

    private companion object {
        const val GROUP_ID = "groupId"
        const val ARTIFACT_ID = "artifactId"
        const val VERSION = "version"

        const val DEPENDENCY = "dependency"
    }
}
