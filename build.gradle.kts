import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    checkstyle

    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.1.0"
}

group = "com.eternalcode"
version = "1.1.1"

checkstyle {
    toolVersion = "10.12.2"

    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")

    maxErrors = 0
    maxWarnings = 0
}

repositories {
    mavenCentral()
    mavenLocal()

    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://repo.eternalcode.pl/releases") }
    maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
    maven { url = uri("https://repo.panda-lang.org/releases") }
    maven { url = uri("https://maven.enginehub.org/repo/") }
}

dependencies {
    // Spigot api
    compileOnly("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")

    // kyori
    implementation("net.kyori:adventure-platform-bukkit:4.3.0")
    implementation("net.kyori:adventure-text-minimessage:4.14.0")

    // litecommands
    implementation("dev.rollczi.litecommands:bukkit-adventure:2.8.9")

    // Okaeri configs
    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:5.0.0-beta.5")
    implementation("eu.okaeri:okaeri-configs-serdes-commons:5.0.0-beta.5")

    // Panda utilities
    implementation("org.panda-lang:panda-utilities:0.5.2-alpha")

    // GitCheck
    implementation("com.eternalcode:gitcheck:1.0.0")

    // commons
    implementation("commons-io:commons-io:2.13.0")

    // bstats
    implementation("org.bstats:bstats-bukkit:3.0.2")

    // worldguard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9")

    // tests
    testImplementation("org.spigotmc:spigot-api:1.19.3-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

bukkit {
    main = "com.eternalcode.combat.CombatPlugin"
    author = "EternalCodeTeam"
    apiVersion = "1.13"
    prefix = "EternalCombat"
    name = "EternalCombat"
    softDepend = listOf("WorldGuard")
    version = "${project.version}"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
    options.encoding = "UTF-8"
}

tasks {
    runServer {
        minecraftVersion("1.19.4")
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("EternalCombat v${project.version}.jar")

    dependsOn("checkstyleMain")
    dependsOn("checkstyleTest")
    dependsOn("test")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "kotlin/**",
        "javax/**"
    )

    val prefix = "com.eternalcode.combat.libs"
    listOf(
        "panda.std",
        "panda.utilities",
        "org.panda-lang",
        "eu.okaeri",
        "net.kyori",
        "org.bstats",
        "dev.rollczi.litecommands",
        "com.eternalcode.gitcheck",
        "org.json.simple",
        "commons-io"
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}
