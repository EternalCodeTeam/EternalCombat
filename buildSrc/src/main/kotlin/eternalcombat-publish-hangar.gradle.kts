import io.papermc.hangarpublishplugin.model.Platforms
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("io.papermc.hangar-publish-plugin")
}

configureSnapshotVersionFromGit()

pluginManager.withPlugin("com.gradleup.shadow") {
    val shadowJarTask = tasks.named<ShadowJar>("shadowJar")

    hangarPublish {
        publications.register("plugin") {
            version.set(project.version.toString())
            channel.set(project.releaseChannel)
            changelog.set(project.changelogText)
            id.set("eternalcombat")
            apiKey.set(providers.environmentVariable("HANGAR_API_TOKEN"))
            platforms {
                register(Platforms.PAPER) {
                    jar.set(shadowJarTask.flatMap { shadowJar -> shadowJar.archiveFile })
                    platformVersions.set(project.paperVersions)
                }
            }
        }
    }
}
