plugins {
    `eternalcombat-java`
    `eternalcombat-repositories`
    `eternalcombat-publish`
    `eternalcombat-java-unit-test`
}

dependencies {
    // Spigot api
    compileOnlyApi("org.spigotmc:spigot-api:${Versions.SPIGOT_API}")

    // kyori
    api("net.kyori:adventure-platform-bukkit:${Versions.ADVENTURE_PLATFORM_BUKKIT}")
    api("net.kyori:adventure-text-minimessage:${Versions.ADVENTURE_TEXT_MINIMESSAGE}")

    // litecommands
    api("dev.rollczi.litecommands:bukkit-adventure:${Versions.LITE_COMMANDS}")

    // Okaeri configs
    api("eu.okaeri:okaeri-configs-yaml-bukkit:${Versions.OKAERI_CONFIGS_YAML_BUKKIT}")
    api("eu.okaeri:okaeri-configs-serdes-commons:${Versions.OKAERI_CONFIGS_SERDES_COMMONS}")
    api("eu.okaeri:okaeri-configs-serdes-bukkit:${Versions.OKAERI_CONFIGS_SERDES_BUKKIT}")

    // Panda utilities
    api("org.panda-lang:panda-utilities:${Versions.PANDA_UTILITIES}")

    // GitCheck
    api("com.eternalcode:gitcheck:${Versions.GIT_CHECK}")

    // commons
    api("commons-io:commons-io:${Versions.APACHE_COMMONS}")

    // bstats
    api("org.bstats:bstats-bukkit:${Versions.B_STATS_BUKKIT}")

    // caffeine
    api("com.github.ben-manes.caffeine:caffeine:${Versions.CAFFEINE}")

    api("com.eternalcode:eternalcode-commons-bukkit:${Versions.ETERNALCODE_COMMONS}")
    api("com.eternalcode:eternalcode-commons-adventure:${Versions.ETERNALCODE_COMMONS}")

    // worldguard
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:${Versions.WORLD_GUARD_BUKKIT}")

    // PlaceholderAPI
    compileOnlyApi("me.clip:placeholderapi:${Versions.PLACEHOLDER_API}")

}
