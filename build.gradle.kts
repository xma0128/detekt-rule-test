plugins {
    kotlin("jvm") version "1.9.10"
}

group = "org.example.detekt"
version = "1.0-SNAPSHOT"

dependencies {
    compileOnly("io.gitlab.arturbosch.detekt:detekt-api:1.23.1")

    testImplementation("io.gitlab.arturbosch.detekt:detekt-test:1.23.3")
    testImplementation("io.kotest:kotest-assertions-core:5.6.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    systemProperty("compile-snippet-tests", project.hasProperty("compile-test-snippets"))
}

