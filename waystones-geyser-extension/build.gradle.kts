repositories {
    maven("https://repo.opencollab.dev/maven-releases")
    maven("https://repo.opencollab.dev/maven-snapshots")
    maven("https://repo.kalimero2.com/snapshots")
    mavenLocal()
    mavenCentral()
}

dependencies {
    api(libs.geyser.api)
    api(libs.geyser.core){
        isTransitive = false
    }
}
