package io.github.gradlebom.publication

import io.github.gradlebom.pom.PomGenerator

internal interface PublicationConfigurator {
    fun configurePomWith(pomGenerator: PomGenerator)
}
