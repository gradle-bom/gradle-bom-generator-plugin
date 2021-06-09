package io.github.gradlebom.pom

import groovy.util.Node

internal class PomDependencyManagementNodeGenerator(
    private val rootNode: Node
) {
    fun generateDependencyManagementNode(): Node = rootNode
        .appendNode(DEPENDENCY_MANAGEMENT)
        .appendNode(DEPENDENCIES)

    private companion object {
        const val DEPENDENCY_MANAGEMENT = "dependencyManagement"
        const val DEPENDENCIES = "dependencies"
    }
}
