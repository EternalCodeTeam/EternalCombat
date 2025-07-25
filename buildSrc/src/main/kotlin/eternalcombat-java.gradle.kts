plugins {
    `java-library`
}

group = "com.eternalcode"
version = "2.1.1"

tasks.compileJava {
    options.compilerArgs = listOf("-Xlint:deprecation", "-parameters")
    options.encoding = "UTF-8"
    options.release = 17
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}
