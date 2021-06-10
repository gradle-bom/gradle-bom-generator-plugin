package io.github.gradlebom.dependency

import org.gradle.api.Project

/**
 * The value object that holds included dependency data.
 *
 * @property group the group of the module to be added as a dependency.
 * @property name the name of the module to be added as a dependency.
 * @property version the optional version of the module to be added as a dependency.
 */
internal data class IncludedDependency(val group: String, val name: String, val version: String) {
    init {
        require(group.isNotBlank()) {
            "Included dependency group can't be empty"
        }

        require(name.isNotBlank()) {
            "Included dependency name can't be empty"
        }

        require(version.isNotBlank()) {
            "Included dependency name can't be empty"
        }
    }

    internal companion object {
        fun from(project: Project) = IncludedDependency(
            project.group as String, project.name, project.version as String
        )
    }
}
