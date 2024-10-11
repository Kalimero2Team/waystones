plugins {
    `java-library`
}

allprojects{
    group = "com.kalimero2.team"
    version = "2.1.6"
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
