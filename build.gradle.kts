plugins {
    id("java-library")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.eripe14"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")

    implementation("net.kyori:adventure-platform-bukkit:4.1.2")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")

    implementation("dev.rollczi.litecommands:bukkit-adventure:2.5.0")

    implementation("org.panda-lang:panda-utilities:0.5.2-alpha")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:4.0.6")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:4.0.6")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}

bukkit {
    main = "com.eripe14.combatlog.CombatLogPlugin"
    apiVersion = "1.13"
    prefix = "CombatLog"
    name = "EternalCombatLog"
    version = "${project.version}"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    runServer {
        minecraftVersion("1.19.2")
    }
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("EternalCombatLog v${project.version} (MC 1.8.8-1.19x).jar")

    exclude("org/intellij/lang/annotations/**","org/jetbrains/annotations/**","org/checkerframework/**","META-INF/**","javax/**")

    mergeServiceFiles()
    minimize()

    val prefix = "com.eripe14.combatlog.libs"
    listOf(
            "panda.std",
            "panda.utilities",
            "org.panda-lang",
            "net.dzikoysk",
            "net.kyori",
            "eu.okaeri",
            "dev.rollczi.litecommands",
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}