import com.vanniktech.maven.publish.SonatypeHost

plugins {
    // Apply the Java plugin to enable Java compilation and JAR packaging
    java
}

/**
 * Configure source sets so Gradle knows where to find Java source files.
 * By default, Gradle uses src/main/java, but this project uses src/.
 */
sourceSets {
    main {
        java {
            srcDirs("src")
        }
    }
}

/**
 * Define repositories used to resolve external dependencies.
 */
repositories {
    // Maven Central hosts Batik and most common Java libraries
    mavenCentral()
}

/**
 * Project dependencies.
 */
dependencies {
    // Compile-time only dependency on Processing core (not bundled in output JAR)
    compileOnly(project(":core"))

    // Apache Batik for SVG processing/rendering support
    implementation("org.apache.xmlgraphics:batik-all:1.19")
}

/**
 * Custom task to package the library for Processing.
 * This assembles:
 * - the compiled JAR
 * - library.properties
 * - example sketches
 * - runtime dependencies
 * into a Processing-compatible library folder.
 */
tasks.register<Copy>("createLibrary") {
    // Ensure the JAR is built before copying files
    dependsOn("jar")

    // Output directory: build/library
    into(layout.buildDirectory.dir("library"))

    // Copy Processing metadata and example sketches
    from(layout.projectDirectory) {
        include("library.properties")
        include("examples/**")
    }

    // Copy runtime dependencies into the library folder
    from(configurations.runtimeClasspath) {
        into("library")
    }

    // Copy and rename the main JAR to match Processing library conventions
    from(tasks.jar) {
        into("library")
        rename { "svg.jar" }
    }
}

publishing {
    repositories {
        maven {
            name = "App"
            url = uri(project(":app").layout.buildDirectory.dir("resources-bundled/common/repository").get().asFile.absolutePath)
        }
    }
}

mavenPublishing {
    coordinates("$group.core", name, version.toString())
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = true)

    signAllPublications()

    pom {
        name.set("Processing SVG")
        description.set("Processing SVG")
        url.set("https://processing.org")
        licenses {
            license {
                name.set("LGPL")
                url.set("https://www.gnu.org/licenses/lgpl-2.1.html")
            }
        }
        developers {
            developer {
                id.set("steftervelde")
                name.set("Stef Tervelde")
            }
            developer {
                id.set("benfry")
                name.set("Ben Fry")
            }
        }
        scm {
            url.set("https://github.com/processing/processing4")
            connection.set("scm:git:git://github.com/processing/processing4.git")
            developerConnection.set("scm:git:ssh://git@github.com/processing/processing4.git")
        }
    }
}
