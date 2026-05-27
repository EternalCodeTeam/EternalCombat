plugins {
    `eternalcombat-java`
    `eternalcombat-repositories`
    `eternalcombat-publish`
    `eternalcombat-java-unit-test`
}

dependencies {
    // Paper api
    compileOnlyApi("io.papermc.paper:paper-api:${Versions.PAPER_API}")
    api("org.jetbrains:annotations:${Versions.JETBRAINS_ANNOTATIONS}")
}
