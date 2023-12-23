plugins {
    id("eternalcombat.java")
    id("com.github.johnrengelman.shadow")

    `maven-publish`
}
dependencies {
    implementation(project(":eternalcombat-api"))
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "eternalcombat-plugin"
            from(project.components["java"])
        }
    }

    repositories {
        mavenLocal()
        maven {
            name = "eternalcodeReleases"
            url = uri("https://repo.eternalcode.pl/releases")
            credentials {
                username = System.getenv("ETERNAL_CODE_MAVEN_USERNAME")
                password = System.getenv("ETERNAL_CODE_MAVEN_PASSWORD")
            }
        }
    }
}
