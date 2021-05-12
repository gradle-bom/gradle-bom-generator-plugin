package io.github.gradlebom

import org.gradle.api.Project
import org.gradle.api.Plugin

class GradleBomGeneratorPluginPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Register a task
        project.tasks.register("generator") { task ->
            task.doLast {
                println("Hello from plugin 'io.github.gradlebom.greeting'")
            }
        }
    }
}
