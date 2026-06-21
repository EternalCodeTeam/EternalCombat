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
        downloadPlugins.modrinth("FastAsyncWorldEdit", Versions.FAST_ASYNC_WORLD_EDIT)
        downloadPlugins.modrinth("PacketEvents", Versions.PACKETEVENTS)
        downloadPlugins.modrinth("WorldGuard", Versions.WORLDGUARD)
        downloadPlugins.modrinth("LuckPerms", Versions.LUCKPERMS)
    }
}
