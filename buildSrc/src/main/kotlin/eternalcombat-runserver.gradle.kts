import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService

plugins {
    id("xyz.jpenilla.run-paper")
}

tasks {
    val javaToolchains = extensions.getByType<JavaToolchainService>()

    runServer {
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(25))
        })

        minecraftVersion("26.2")
        downloadPlugins.modrinth("WorldEdit", "7.4.6-beta-01")
        downloadPlugins.modrinth("PacketEvents", "2.13.0+spigot")
        downloadPlugins.modrinth("WorldGuard", "7.0.19")
        downloadPlugins.modrinth("LuckPerms", "v5.5.53-bukkit")
        downloadPlugins.modrinth("GriefPrevention", Versions.GRIEF_PREVENTION)
    }
}
