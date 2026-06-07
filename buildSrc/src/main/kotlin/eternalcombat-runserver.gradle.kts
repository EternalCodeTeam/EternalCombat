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

        minecraftVersion("26.1.2")
        downloadPlugins.modrinth("FastAsyncWorldEdit", "2.15.2")
        downloadPlugins.modrinth("PacketEvents", "2.12.2+spigot")
        downloadPlugins.modrinth("WorldGuard", "7.0.17")
        downloadPlugins.modrinth("LuckPerms", "v5.5.53-bukkit")
    }
}
