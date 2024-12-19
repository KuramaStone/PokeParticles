plugins {
    id("java")
    id("dev.architectury.loom") version("1.7-SNAPSHOT")
    id("architectury-plugin") version ("3.4-SNAPSHOT")
    id("com.github.johnrengelman.shadow") version ("8.1.0")
    kotlin("jvm") version ("1.8.10")
}

group = "com.github.kuramastone"
version = "1.1.1-1.20.1"

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

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}


val fabricApiVersion: String by project
val bUtilitiesVersion: String by project

dependencies {
    minecraft("net.minecraft:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.10:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.9")
    modImplementation("com.cobblemon:fabric:1.5.2+1.20.1")
    //modImplementation(files("libs/Cobblemon-fabric-1.6.0b8390+1.21.1-HEAD-c75c5e4.jar"))

    include("com.github.kuramastone:BUtilities-Core:$bUtilitiesVersion")
    implementation("com.github.kuramastone:BUtilities-Core:$bUtilitiesVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")

    modRuntimeOnly("net.fabricmc.fabric-api:fabric-api:$fabricApiVersion")
    setOf(
        "fabric-api-base",
        "fabric-command-api-v1",
        "fabric-lifecycle-events-v1",
        "fabric-networking-api-v1"
    ).forEach {
        // Add each module as a dependency
        modImplementation(fabricApi.module(it, fabricApiVersion))
    }

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}