import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    checkstyle

    id("net.minecrell.plugin-yml.bukkit") version "0.5.2"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("xyz.jpenilla.run-paper") version "2.0.1"
}

group = "com.eternalcode"
version = "1.0.0"

checkstyle {
    toolVersion = "10.7.0"

    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")

    maxErrors = 0
    maxWarnings = 0
}

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://repo.eternalcode.pl/releases") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
}

dependencies {
    // Spigot api
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")

    // kyori
    implementation("net.kyori:adventure-platform-bukkit:4.2.0")
    implementation("net.kyori:adventure-text-minimessage:4.12.0")

    // litecommands
    implementation("dev.rollczi.litecommands:bukkit-adventure:2.8.3")

    // cdn configs
    implementation("net.dzikoysk:cdn:1.14.3")

    // GitCheck
    implementation("com.eternalcode:gitcheck:1.0.0")

    // tests
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
}

bukkit {
    main = "com.eternalcode.combat.CombatPlugin"
    author = "EternalCodeTeam"
    apiVersion = "1.13"
    prefix = "EternalCombat"
    name = "EternalCombat"
    version = "${project.version}"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
    options.encoding = "UTF-8"
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("EternalCombat v${project.version} (MC 1.8.8-1.19x).jar")

    dependsOn("checkstyleMain")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "javax/**"
    )

    mergeServiceFiles()
    minimize()

    val prefix = "com.eternalcode.combat.libs"
    listOf(
        "panda.std",
        "panda.utilities",
        "org.panda-lang",
        "net.dzikoysk",
        "net.kyori",
        "dev.rollczi.litecommands",
        "com.eternalcode.gitcheck",
        "org.json.simple",
        "kotlin"
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}
