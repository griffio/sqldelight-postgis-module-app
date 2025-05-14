plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.grammarKitComposer)
    id("maven-publish")
}

repositories {
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
    mavenCentral()
    gradlePluginPortal()
    maven("https://cache-redirector.jetbrains.com/download-pgp-verifier")
    // Grazie
    maven("https://cache-redirector.jetbrains.com/packages.jetbrains.team/maven/p/grazi/grazie-platform-public")
}

grammarKit {
    intellijRelease.set(libs.versions.intellij)
}

dependencies {
    implementation(libs.sqldelight.dialect.api)
    implementation(libs.sqldelight.postgresql.dialect)
    implementation(libs.sqldelight.compiler.env)
    implementation("net.postgis:postgis-jdbc:2024.1.0")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            // You can customize the coordinates if needed
            groupId = "griffio"
            artifactId = "postgis"
            version = "1.0.1"
        }
    }
}
