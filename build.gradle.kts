plugins {
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("com.github.ben-manes.versions") version "0.36.0"
}

group = "com.github.hannesbraun"
version = "4.1.2-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/sedmelluq/com.sedmelluq")
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

dependencies {
    implementation(kotlin("bom"))
    implementation(kotlin("stdlib"))
    implementation("net.dv8tion:JDA:4.2.0_227")
    implementation("org.jetbrains.exposed:exposed-core:0.29.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.29.1")
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("com.sedmelluq:lavaplayer:1.3.66")
    implementation("com.sedmelluq:lavaplayer-natives-extra:1.3.13")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("io.ktor:ktor-client-core:1.5.0")
    implementation("io.ktor:ktor-client-cio:1.5.0")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.apache.commons:commons-text:1.9")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.1")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.github.hannesbraun.katarina.KatarinaKt"
    }
}
