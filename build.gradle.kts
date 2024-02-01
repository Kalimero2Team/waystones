plugins {
    `java-library`
    id("com.github.ben-manes.versions") version "0.49.0"
}

allprojects{
    group = "com.kalimero2.team"
    version = "2.0.4"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

subprojects{
    apply{
        plugin("java-library")
    }
    tasks{
        compileJava{
            options.encoding = "UTF-8"
        }
    }
}
