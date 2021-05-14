package io.github.gradlebom

import org.gradle.api.Project

data class IncludedDependency(val group: String, val name: String, val version: String) {
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

    companion object {
        fun from(project: Project) = IncludedDependency(
            project.group as String, project.name, project.version as String
        )
    }
}
