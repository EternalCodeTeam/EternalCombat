
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription
import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    `eternalcombat-java`
    `eternalcombat-repositories`
    `eternalcombat-publish-hangar`
    `eternalcombat-publish-modrinth`
    `eternalcombat-runserver`

    id("de.eldoria.plugin-yml.paper") version "0.9.0"
    id("com.gradleup.shadow")
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

    // Paper
    compileOnly("io.papermc.paper:paper-api:${Versions.PAPER_API}")

    // litecommands
    implementation("dev.rollczi:litecommands-bukkit:${Versions.LITE_COMMANDS}")
    implementation("dev.rollczi:litecommands-folia:${Versions.LITE_COMMANDS}")

    // Okaeri configs
    implementation("eu.okaeri:okaeri-configs-serdes-commons:${Versions.OKAERI_CONFIGS_SERDES_COMMONS}")
    implementation("eu.okaeri:okaeri-configs-serdes-bukkit:${Versions.OKAERI_CONFIGS_SERDES_BUKKIT}")

    // XSeries
    implementation("com.github.cryptomorin:XSeries:${Versions.XSERIES}")

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
    implementation("com.eternalcode:multification-paper:${Versions.MULTIFICATION}")
    implementation("com.eternalcode:multification-okaeri:${Versions.MULTIFICATION}")
    compileOnly("com.github.retrooper:packetevents-spigot:${Versions.PACKETS_EVENTS}")
}

paper {
    main = "com.eternalcode.combat.CombatPlugin"
    authors = listOf("EternalCodeTeam")
    apiVersion = "1.19"
    prefix = "EternalCombat"
    name = "EternalCombat"
    generateLibrariesJson = true
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    version = "${project.version}"

    foliaSupported = true

    serverDependencies {
        register("packetevents") {
            required = true
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("Lands") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("WorldGuard") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }

        register("PlaceholderAPI") {
            required = false
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
        }
    }
}

tasks {
    named("generatePaperPluginDescription") {
        notCompatibleWithConfigurationCache("The plugin-yml paper generator reads Task.project during execution.")
    }
}

tasks.shadowJar {
    archiveFileName.set("EternalCombat v${project.version}.jar")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "net/kyori/**",
        "META-INF/**",
        "kotlin/**",
        "javax/**",
        "org/checkerframework/**",
        "com/google/errorprone/**",
        "com/google/gson/**"
    )

    val prefix = "com.eternalcode.combat.libs"
    listOf(
        "eu.okaeri",
        "org.bstats",
        "org.yaml",
        "dev.rollczi.litecommands",
        "com.eternalcode.gitcheck",
        "org.json.simple",
        "com.github.benmanes.caffeine",
        "com.eternalcode.commons",
        "com.eternalcode.multification",
        "com.github.cryptomorin",
        "com.cryptomorin",
    ).forEach { pack ->
        relocate(pack, "$prefix.$pack")
    }
}
