plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.gradleup.shadow:com.gradleup.shadow.gradle.plugin:9.5.1")
    implementation("com.modrinth.minotaur:com.modrinth.minotaur.gradle.plugin:2.9.0")
    implementation("io.papermc.hangar-publish-plugin:io.papermc.hangar-publish-plugin.gradle.plugin:0.1.4")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.4.10")
    implementation("net.minecrell:plugin-yml:0.6.0")
    implementation("xyz.jpenilla:run-task:3.0.2")
}

sourceSets {
    main {
        java.setSrcDirs(emptyList<String>())
        groovy.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.setSrcDirs(emptyList<String>())
        kotlin.setSrcDirs(emptyList<String>())
        groovy.setSrcDirs(emptyList<String>())
        resources.setSrcDirs(emptyList<String>())
    }
}
