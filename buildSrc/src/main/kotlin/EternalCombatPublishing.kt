import org.gradle.api.Project
import org.gradle.api.provider.Provider

internal val Project.isRelease: Boolean
    get() = providers.environmentVariable("GITHUB_EVENT_NAME").orNull == "release"

internal val Project.releaseChannel: String
    get() = if (isRelease) "Release" else "Snapshot"

internal val Project.changelogText: Provider<String>
    get() = providers.environmentVariable("CHANGELOG")
        .orElse(providers.exec { commandLine("git", "log", "-1", "--format=%B") }.standardOutput.asText)

internal val Project.paperVersions: List<String>
    get() = property("paperVersion").toString()
        .split(",")
        .map { paperVersion -> paperVersion.trim() }

internal fun Project.configureSnapshotVersionFromGit() {
    val isGithubWorkflow = providers.environmentVariable("GITHUB_RUN_NUMBER").isPresent

    if (!isGithubWorkflow || isRelease) {
        return
    }

    if ("-SNAPSHOT+" in version.toString()) {
        return
    }

    val describeParts = providers.exec { commandLine("git", "describe", "--tags", "--long") }
        .standardOutput.asText.get()
        .trim()
        .split("-")

    val offset = if (describeParts.size >= 3) describeParts[describeParts.size - 2] else "0"
    val baseVersion = version.toString().substringBefore("-SNAPSHOT")

    version = "$baseVersion-SNAPSHOT+$offset"
}
