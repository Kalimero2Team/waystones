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
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
    maven("https://repo.byquanton.eu/releases")
    maven("https://repo.opencollab.dev/main")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.get())
    bukkitLibrary(libs.cloud.paper)
    bukkitLibrary(libs.sqlite)
    compileOnly(libs.floodgate.api)
    implementation(libs.anvilgui)
    compileOnly("com.kalimero2.team:claims-api:1.1.5")
    implementation(libs.customblockdata)
    implementation(libs.morepersistentdatatypes)
}

tasks {
    runServer {
        minecraftVersion("1.20.1")

        downloadPlugins {
            url("https://mitochondrium.kalimero2.com/plugins/resource-pack-loader-1.0.0.jar")
        }
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
    authors = listOf("byquanton", "Quantum625")
    softDepend = listOf("floodgate", "claims-paper")
}