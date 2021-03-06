package io.github.gradlebom

import org.gradle.testkit.runner.GradleRunner
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertAll
import java.nio.file.Path
import java.util.Locale
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.div
import kotlin.io.path.readText
import kotlin.io.path.writeText

internal fun Elements.selectFirst(cssQuery: String) = select(cssQuery)[0]
internal fun Elements.selectSecond(cssQuery: String) = select(cssQuery)[1]

internal fun Document.verifyArtifactId(artifactId: String) {
    assertEquals(artifactId, selectFirst("artifactId")?.text()) {
        "has proper artifactId"
    }
}

internal fun Document.verifyGroupId(groupId: String) {
    assertEquals(groupId, selectFirst("groupId")?.text()) {
        "has proper groupId"
    }
}

internal fun Document.verifyVersion(version: String) {
    assertEquals(version, selectFirst("version")?.text()) {
        "has proper version"
    }
}

internal fun Document.verifyDependencyManagementNode(nodes: Int, verifyNodes: (Elements) -> Unit) {
    select("dependencyManagement").let { elements ->
        assertAll(
            "'dependencyManagement' node",
            {
                assertTrue(elements.isNotEmpty()) {
                    "exists"
                }
            },
            {
                assertEquals(nodes, elements.select("dependency").size) {
                    "has $nodes 'dependency' nodes"
                }
            },
            { verifyNodes(elements) }
        )
    }
}

internal fun Element.verifyDependency(heading: String, groupId: String, artifactId: String, version: String) {
    assertAll(
        heading,
        {
            assertEquals(groupId, selectFirst("groupId")?.text()) {
                "has correct 'groupId'"
            }
        },
        {
            assertEquals(artifactId, selectFirst("artifactId")?.text()) {
                "has correct 'artifactId'"
            }
        },
        {
            assertEquals(version, selectFirst("version")?.text()) {
                "has correct 'version'"
            }
        }
    )
}

@ExperimentalPathApi
internal fun readPomFileFrom(path: Path, publicationName: String = "bomJava"): Document =
    (path / "build" / "publications" / publicationName / "pom-default.xml")
        .readText()
        .let(Jsoup::parse)

@ExperimentalPathApi
internal fun Path.createSubproject(projectName: String) {
    val exampleAppProject = (this / projectName).createDirectories()
    (exampleAppProject / "build.gradle").writeText(
        """
            plugins {
                id('java-library')
                id('maven-publish')
            }
            group = 'org.example'
            version = '0.0.1'
            publishing {
                publications {
                    mavenJava(MavenPublication) {
                        from(components.java)
                    }
                }
            }
        """.trimIndent()
    )
}

@ExperimentalPathApi
internal fun Path.withBuildGradle(buildGradleContent: String) {
    (this / "build.gradle").writeText(buildGradleContent)
}

@ExperimentalPathApi
internal fun Path.withSettingsGradle(buildGradleContent: String) {
    (this / "settings.gradle").writeText(buildGradleContent)
}

@ExperimentalPathApi
internal fun Path.createBomSubProject(buildGradleContent: String): Path =
    (this / "example-bom").createDirectories()
        .also { exampleBomProject ->
            exampleBomProject.withBuildGradle(buildGradleContent)
        }

internal fun GradleRunner.runGeneratePom(name: String = "bomJava") {
    name.capitalize(Locale.getDefault())
        .let {
            withArguments(":example-bom:generatePomFileFor${it}Publication").build()
        }
}
