plugins {
    `java-library`
    checkstyle

    id("maven-publish")
}

group = "com.eternalcode"
version = "1.1.1"

checkstyle {
    toolVersion = "10.12.4"

    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")

    maxErrors = 0
    maxWarnings = 0
}

repositories {
    mavenCentral()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.eternalcode.pl/releases") }
    maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
}

dependencies {
    // Spigot api
    compileOnlyApi("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")

    // kyori
    api("net.kyori:adventure-platform-bukkit:4.3.1")
    api("net.kyori:adventure-text-minimessage:4.14.0")

    // litecommands
    api("dev.rollczi.litecommands:bukkit-adventure:2.8.9")

    // Okaeri configs
    api("eu.okaeri:okaeri-configs-yaml-bukkit:5.0.0-beta.5")
    api("eu.okaeri:okaeri-configs-serdes-commons:5.0.0-beta.5")
    api("eu.okaeri:okaeri-configs-serdes-bukkit:5.0.0-beta.5")

    // Panda utilities
    api("org.panda-lang:panda-utilities:0.5.2-alpha")

    // GitCheck
    api("com.eternalcode:gitcheck:1.0.0")

    // commons
    api("commons-io:commons-io:2.14.0")

    // bstats
    api("org.bstats:bstats-bukkit:3.0.2")

    // worldguard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")

    // tests
    testImplementation("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

// Publish

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "eternalcombat-api"
            from(project.components["java"])
        }
    }
}

publishing {
    repositories {
        mavenLocal()

        maven {
            name = "eternalcodeReleases"
            url = uri("https://repo.eternalcode.pl/releases")

            credentials {
                username = System.getenv("ETERNAL_CODE_MAVEN_USERNAME")
                password = System.getenv("ETERNAL_CODE_MAVEN_PASSWORD")
            }
        }
    }
}

