plugins {
    `java-library`
}

dependencies {
    testImplementation("io.papermc.paper:paper-api:${Versions.PAPER_API}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.JUNIT_JUPITER_API}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.JUNIT_JUPITER_PARAMS}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.JUNIT_JUPITER_ENGINE}")
}

tasks.test {
    useJUnitPlatform()
}

sourceSets.test {
    java.setSrcDirs(listOf("test"))
    resources.setSrcDirs(emptyList<String>())
}
