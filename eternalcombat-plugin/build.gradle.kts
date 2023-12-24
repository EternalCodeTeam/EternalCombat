plugins {
    id("eternalcombat.java")

    id("net.minecrell.plugin-yml.bukkit")
    id("com.github.johnrengelman.shadow")
    id("xyz.jpenilla.run-paper")
}

dependencies {
    implementation(project(":eternalcombat-api"))
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
        "javassist",
        "com.github.benmanes.caffeine",
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}
