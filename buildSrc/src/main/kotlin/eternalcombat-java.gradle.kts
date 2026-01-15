plugins {
    `java-library`
}

group = "com.eternalcode"
version = "2.4.0-SNAPSHOT"

tasks.compileJava {
    options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
    options.encoding = "UTF-8"
    options.release = 21
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
