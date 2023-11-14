plugins {
    `java-library`
    checkstyle
}

group = "com.eternalcode"
version = "1.1.1"

checkstyle {
    toolVersion = "10.12.4"

    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")

    maxErrors = 0
    maxWarnings = 0
}

repositories {
    mavenCentral()

    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.eternalcode.pl/releases")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
    maven("https://repo.panda-lang.org/releases")
    maven("https://maven.enginehub.org/repo/")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.compileJava {
    options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
    options.encoding = "UTF-8"
}
