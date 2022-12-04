repositories {
    maven("https://repo.opencollab.dev/main")
    mavenLocal()
}

dependencies {
    api(libs.geyser.base.api)
    api(libs.geyser.api)
    api(libs.geyser.core){
        isTransitive = false
    }
}
