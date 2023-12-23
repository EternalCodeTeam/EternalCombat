plugins {
    id("eternalcombat.java")

    id("net.minecrell.plugin-yml.bukkit")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

dependencies {
    // Spigot api
    compileOnlyApi(libs.spigotApi)

    // kyori
    api(libs.adventurePlatformBukkit)
    api(libs.adventureTextMinimessage)

    // litecommands
    api(libs.liteCommands)

    // Okaeri configs
    api(libs.okaeriConfigsYamlBukkit)
    api(libs.okaeriConfigsSerdesCommons)
    api(libs.okaeriConfigsSerdesBukkit)

    // Panda utilities
    api(libs.pandaUtilities)

    // GitCheck
    api(libs.gitCheck)

    // commons
    api(libs.apacheCommons)

    // bstats
    api(libs.bStatsBukkit)

    // worldguard
    compileOnly(libs.worldGuardBukkit)

    // tests
    testImplementation(libs.spigotApi)
    testImplementation(libs.jUnitJupiterApi)
    testImplementation(libs.jUnitJupiterParams)
    testRuntimeOnly(libs.jUnitJupiterEngine)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
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

tasks {
    runServer {
        minecraftVersion("1.19.4")
    }
}

tasks.shadowJar {
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
        "org.apache.commons",
        "javassist"
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}
