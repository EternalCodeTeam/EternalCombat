plugins {
    checkstyle
    `java-library`
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "checkstyle")

    group = "com.eternalcode"
    version = "1.1.2"

    repositories {
        mavenCentral()

        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
        maven { url = uri("https://repo.eternalcode.pl/releases") }
        maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
        maven { url = uri("https://repo.panda-lang.org/releases") }
        maven { url = uri("https://maven.enginehub.org/repo/") }
    }
}

checkstyle {
    toolVersion = "10.12.4"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    maxErrors = 0
    maxWarnings = 0
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.compileJava {
    options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
    options.encoding = "UTF-8"
}
