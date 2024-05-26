plugins {
    id("java")
    `maven-publish`
    id("net.minecrell.plugin-yml.paper") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.plex"
version = "1.0-SNAPSHOT"
description = "Medina"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    compileOnly(libs.paperApi)
    implementation(libs.bundles.bstats) { isTransitive = false }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    jar {
        enabled = false
    }

    shadowJar {
        archiveBaseName.set("Medina")
        archiveClassifier.set("")
        relocate("org.bstats", "dev.plex.medina")
    }
}

paper {
    name = "Medina"
    version = project.version.toString()
    main = "dev.plex.medina.Medina"
    apiVersion = "1.20"
    foliaSupported = true
    authors = listOf("Telesphoreo")
    description = "A sophisticated reporting plugin for Minecraft"
    website = "https://plex.us.org"
    generateLibrariesJson = true
}