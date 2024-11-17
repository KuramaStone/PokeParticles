plugins {
    id("java")
    id("dev.architectury.loom") version("1.6-SNAPSHOT")
    id("architectury-plugin") version("3.4-SNAPSHOT")
    kotlin("jvm") version ("1.8.10")
}

group = "com.github.kuramastone"
version = "1.0.0"

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    silentMojangMappingsLicense()

    mixin {
        defaultRefmapName.set("mixins.${project.name}.refmap.json")
    }
}

repositories {
    mavenCentral()
    mavenLocal()
    maven(url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://maven.impactdev.net/repository/development/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

val fabricApiVersion: String by project

dependencies {
    minecraft("net.minecraft:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.10:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.9")

    implementation("com.github.kuramastone:BUtilities:1.0.2")

    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    modImplementation("com.cobblemon:fabric:1.5.2+1.20.1")
    setOf(
        "fabric-api-base",
        "fabric-command-api-v1",
        "fabric-lifecycle-events-v1",
        "fabric-networking-api-v1"
    ).forEach {
        // Add each module as a dependency
        modImplementation(fabricApi.module(it, "$fabricApiVersion"))
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}
