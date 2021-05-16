# Gradle BOM generator plugin

[![Tests](https://github.com/gradle-bom/gradle-bom-generator-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/gradle-bom/gradle-bom-generator-plugin/actions/workflows/ci.yml)

This Gradle plugin generates a [bill of materials (BOM) file]
for multi-module projects. Next BOM can be imported in other projects and efficiently ease working with versioning
multi-project dependency.

## Table of contents

* [Usage](#usage)
    * [Applying the plugin](#applying-the-plugin)
      * [Using Kotlin](#applying-the-plugin-in-kotlin)
      * [Using Groovy](#applying-the-plugin-in-groovy)
    * [Excluding unwanted modules](#excluding-unwanted-modules)
    * [Including external dependencies](#including-external-dependencies)
    * [Importing generated BOM in Gradle project](#importing-generated-bom-in-gradle-project)
      * [Using Kotlin](#importing-bom-using-kotlin)
      * [Using Groovy](#importing-bom-using-groovy)
    * [Importing generated BOM in Maven project](#importing-generated-bom-in-maven-project)
* [License](#license)
* [Copyright](#copyright)

# Usage

## Applying the plugin

Create a new module for a BOM generation with a `build.gradle` file. If you don't know how to create a module for
multi-module project, check out this [official guide].

### Applying the plugin in Kotlin

```kotlin
plugins {
    id("io.github.gradlebom.generator") version "«version»"
}

group = "org.example"
version = "1.0.0"
```

### Applying the plugin in Groovy

```groovy
plugins {
    id 'io.github.gradlebom.generator' version '«version»'
}

group = 'org.example'
version = '1.0.0'
```

Next include the new module in `settings.gradle`. Finally, build whole multi-module project.

## Excluding unwanted modules

You can exclude modules from a BOM generation.

```kotlin
bomGenerator {
    excludedProjects = ["excluded-module"]
}
```

## Including external dependencies

You can add an external dependency that version will be included in a BOM.

```kotlin
bomGenerator {
    includeDependency("org.other", "project", "1.0.0")
    includeDependency("org.other:project:1.0.4")
}
```

## Importing generated BOM in Gradle project

A published BOM can be later used in different projects. See examples below
or checkout official Gradle documentation 
[here](https://docs.gradle.org/current/userguide/platforms.html#sub:bom_import).

### Importing BOM using Groovy

```groovy
dependencies {
    // import a BOM
    implementation platform('org.example:module-bom:1.0.0')
    
    // define dependencies from your multi-module project without versions
    implementation 'org.example:module-data'
    implementation 'org.example:module-web'
    implementation 'org.example:module-logs'
}
```

### Importing BOM using Kotlin

```kotlin
dependencies {
    // import a BOM
    implementation(platform("org.example:module-bom:1.0.0"))
    
    // define dependencies from your multi-module project without versions
    implementation("org.example:module-data")
    implementation("org.example:module-web")
    implementation("org.example:module-logs")
}
```

## Importing generated BOM in Maven project

```xml
<project>
    <dependencyManagement>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>module-bom</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencyManagement>

    <dependencies>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>module-data</artifactId>
        </dependency>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>module-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>module-logs</artifactId>
        </dependency>
    </dependencies>
</project>
```

# License

See [LICENSE](./LICENSE).

# Copyright

Copyright © 2021 Waldemar Panas

[bill of materials (BOM) file]: https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Importing_Dependencies

[official guide]: https://docs.gradle.org/current/userguide/multi_project_builds.html#sec:creating_multi_project_builds