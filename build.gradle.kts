plugins {
    application
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}

group = "se.antonohlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jline:jline:3.30.0")
    implementation("org.jline:jline-terminal-ffm:3.30.0")
    implementation("org.jline:jline-terminal-jna:3.30.0")
    implementation("org.jline:jline-terminal-jni:3.30.0")
    implementation("org.jline:jline-terminal-jansi:3.30.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(24)
}

tasks.register<Jar>("uberJar") {
    archiveFileName = "ttyper.jar"
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    manifest {
        attributes["Main-Class"] = "org.example.MainKt"
    }
    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath
            .get()
            .filter {
                it.name.endsWith("jar")
            }.map { zipTree(it) }
    })
}
