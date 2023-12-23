plugins {
    id("eternalcombat.java")
    id("com.github.johnrengelman.shadow")
}
dependencies {
    implementation(project(":eternalcombat-api"))
}

tasks.shadowJar {
    archiveFileName.set("EternalCombat v${project.version}.jar")

    dependsOn("checkstyleMain")
    dependsOn("checkstyleTest")
    dependsOn("test")

    exclude(
        "org/intellij/lang/annotations/**",
        "org/jetbrains/annotations/**",
        "META-INF/**",
        "kotlin/**",
        "javax/**"
    )
}
