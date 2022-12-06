plugins {
    id("java-library")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.eternalcode"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.18.1-R0.1-SNAPSHOT")

    // kyori
    implementation("net.kyori:adventure-platform-bukkit:4.1.2")
    implementation("net.kyori:adventure-text-minimessage:4.11.0")

    // litecommands
    implementation("dev.rollczi.litecommands:bukkit-adventure:2.5.0")

    // cdn configs
    implementation("net.dzikoysk:cdn:1.14.1")
}

bukkit {
    main = "com.eternalcode.combatlog.CombatLogPlugin"
    apiVersion = "1.13"
    prefix = "EternalCombatLog"
    name = "EternalCombatLog"
    version = "${project.version}"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("EternalCombatLog v${project.version} (MC 1.8.8-1.19x).jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "org/checkerframework/**",
        "META-INF/**",
        "javax/**"
    )

    mergeServiceFiles()
    minimize()

    val prefix = "com.eternalcode.combatlog.libs"
    listOf(
            "panda.std",
            "panda.utilities",
            "org.panda-lang",
            "net.dzikoysk",
            "net.kyori",
            "dev.rollczi.litecommands",
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}
