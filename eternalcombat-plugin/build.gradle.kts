import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

plugins {
    `eternalcombat-java`
    `eternalcombat-repositories`

    id("net.minecrell.plugin-yml.bukkit")
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper")
}

configurations.all {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "com.google.code.gson" && requested.name == "gson") {
                useVersion("2.11.0")
                because("WorldGuard requires strictly gson 2.11.0")
            }
        }
    }
}

dependencies {
    implementation(project(":eternalcombat-api"))

    // kyori
    implementation("net.kyori:adventure-platform-bukkit:${Versions.ADVENTURE_PLATFORM_BUKKIT}")
    implementation("net.kyori:adventure-text-minimessage:${Versions.ADVENTURE_API}")
    implementation("net.kyori:adventure-api") {
        version {
            strictly(Versions.ADVENTURE_API)
        }
    }

    // litecommands
    implementation("dev.rollczi:litecommands-bukkit:${Versions.LITE_COMMANDS}")
    implementation("dev.rollczi:litecommands-folia:${Versions.LITE_COMMANDS}")

    // Okaeri configs
    implementation("eu.okaeri:okaeri-configs-serdes-commons:${Versions.OKAERI_CONFIGS_SERDES_COMMONS}")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:${Versions.OKAERI_CONFIGS_SERDES_BUKKIT}")

    // bstats
    implementation("org.bstats:bstats-bukkit:${Versions.B_STATS_BUKKIT}")

    // caffeine
    implementation("com.github.ben-manes.caffeine:caffeine:${Versions.CAFFEINE}")

    implementation("com.eternalcode:eternalcode-commons-adventure:${Versions.ETERNALCODE_COMMONS}")
    implementation("com.eternalcode:eternalcode-commons-bukkit:${Versions.ETERNALCODE_COMMONS}")
    implementation("com.eternalcode:eternalcode-commons-shared:${Versions.ETERNALCODE_COMMONS}")
    implementation("com.eternalcode:eternalcode-commons-folia:${Versions.ETERNALCODE_COMMONS}")
    implementation("com.eternalcode:eternalcode-commons-updater:${Versions.ETERNALCODE_COMMONS}")

    // worldguard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:${Versions.WORLD_GUARD_BUKKIT}")

    // PlaceholderAPI
    compileOnly("me.clip:placeholderapi:${Versions.PLACEHOLDER_API}")
    
    // Lands
    compileOnly("com.github.angeschossen:LandsAPI:${Versions.LANDS_API}")

    // Multification
    implementation("com.eternalcode:multification-bukkit:${Versions.MULTIFICATION}")
    implementation("com.eternalcode:multification-okaeri:${Versions.MULTIFICATION}")
    compileOnly("com.github.retrooper:packetevents-spigot:${Versions.PACKETS_EVENTS}")
    implementation("io.papermc:paperlib:${Versions.PAPERLIB}")
}

bukkit {
    main = "com.eternalcode.combat.CombatPlugin"
    author = "EternalCodeTeam"
    apiVersion = "1.13"
    prefix = "EternalCombat"
    name = "EternalCombat"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    softDepend = listOf(
        "Lands"
    )
    depend = listOf(
        "packetevents",
    )
    version = "${project.version}"

    foliaSupported = true
}

tasks {
    runServer {
        minecraftVersion("1.21.10")

        downloadPlugins.modrinth("WorldEdit", Versions.WORLDEDIT)
        downloadPlugins.modrinth("PacketEvents", Versions.PACKETEVENTS)
        downloadPlugins.modrinth("WorldGuard", Versions.WORLDGUARD)
        downloadPlugins.modrinth("LuckPerms", Versions.LUCKPERMS)
    }

    runPaper.folia.registerTask() {
        minecraftVersion("1.21.8")

        downloadPlugins.modrinth("PacketEvents", Versions.PACKETEVENTS)
    }
}

tasks.shadowJar {
    archiveFileName.set("EternalCombat v${project.version}.jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "kotlin/**",
        "javax/**",
        "org/checkerframework/**",
        "com/google/errorprone/**",
    )

    val prefix = "com.eternalcode.combat.libs"
    listOf(
        "eu.okaeri",
        "net.kyori",
        "org.bstats",
        "org.yaml",
        "dev.rollczi.litecommands",
        "com.eternalcode.gitcheck",
        "org.json.simple",
        "com.github.benmanes.caffeine",
        "com.eternalcode.commons",
        "com.eternalcode.multification",
        "io.papermc.lib"
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}
