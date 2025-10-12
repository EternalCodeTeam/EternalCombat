plugins {
    `java-library`
}

repositories {
    maven("https://repo.eternalcode.pl/releases")
    maven("https://repo.eternalcode.pl/snapshots")

    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
    maven("https://repo.panda-lang.org/releases")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.codemc.io/repository/maven-releases/")
    maven("https://jitpack.io/")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots/")
}
