package io.github.gradlebom

import org.gradle.api.Project
import org.gradle.kotlin.dsl.setProperty

open class BomGeneratorExtension(project: Project) {
    val excludedProjects = project.objects.setProperty<String>()

    internal companion object {
        const val NAME = "bomGenerator"
    }
}
