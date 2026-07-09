import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.modrinth.minotaur")
}

configureSnapshotVersionFromGit()

pluginManager.withPlugin("com.gradleup.shadow") {
    val shadowJarTask = tasks.named<ShadowJar>("shadowJar")

    modrinth {
        token.set(providers.environmentVariable("MODRINTH_TOKEN"))
        projectId.set("eternalcombat")
        versionNumber.set(project.version.toString())
        versionType.set(if (project.isRelease) "release" else "beta")
        uploadFile.set(shadowJarTask)
        gameVersions.addAll(project.paperVersions)
        loaders.addAll(listOf("paper", "folia", "purpur"))
        changelog.set(project.changelogText)
        syncBodyFrom.set(rootProject.file("README.md").readText())
    }
}
