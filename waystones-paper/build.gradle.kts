import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    alias(libs.plugins.paper.run)
    alias(libs.plugins.paper.userdev)
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.byquanton.eu/releases")
    maven("https://repo.opencollab.dev/main")
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    paperDevBundle(libs.versions.paper.get())
    bukkitLibrary(libs.cloud.paper)
    bukkitLibrary(libs.sqlite)
    compileOnly(libs.floodgate.api)
    compileOnly("com.kalimero2.team:claims-api:1.1.5")
    implementation(libs.customblockdata)
    implementation(libs.morepersistentdatatypes)
    implementation(project(":waystones-api"))
}

tasks {
    runServer {
        minecraftVersion("1.19.3")
    }

    shadowJar {
        fun reloc(pkg: String, name: String) = relocate(pkg, "com.kalimero2.team.claims.paper.shaded.$name")
        reloc("com.jeff_media.customblockdata", "customblockdata")
        reloc("com.jeff_media.morepersistentdatatypes", "morepersistentdatatypes")
        reloc("net.wesjd.anvilgui", "anvilgui")
    }
}

bukkit {
    main = "com.kalimero2.team.waystones.paper.PaperWayStones"
    apiVersion = "1.19"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    authors = listOf("byquanton")
    softDepend = listOf("floodgate", "claims-paper")
}