plugins {
    application
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"
}

application {
    mainClass = "org.example.MainKt"
}

tasks.register<JavaExec>("runjar") {
    dependsOn("uberJar")
    classpath = files("build/libs/ttyper.jar")
    jvmArgs = listOf("--enable-native-access=ALL-UNNAMED")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("com.google.code.gson:gson:2.13.2")
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
