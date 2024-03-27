import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    alias(libs.plugins.paper.run)
    alias(libs.plugins.plugin.yml)
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
    maven("https://repo.kalimero2.com/releases")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
}

dependencies {
    bukkitLibrary(libs.cloud.paper)
    bukkitLibrary(libs.sqlite)
    implementation(libs.anvilgui)
    compileOnly(libs.paper)
    compileOnly(libs.floodgate.api)
    compileOnly("com.kalimero2.team:claims-api:2.0.6")
}

tasks {
    runServer {
        minecraftVersion("1.20.1")

        downloadPlugins {
            url("https://mitochondrium.kalimero2.com/plugins/resource-pack-loader-1.0.0.jar")
        }
    }

    shadowJar {
        fun reloc(pkg: String, name: String) = relocate(pkg, "com.kalimero2.team.waystones.paper.shaded.$name")
        reloc("net.wesjd.anvilgui", "anvilgui")
    }
}

bukkit {
    main = "com.kalimero2.team.waystones.paper.PaperWayStones"
    apiVersion = "1.20"
    load = BukkitPluginDescription.PluginLoadOrder.POSTWORLD
    authors = listOf("byquanton", "nanoflux")
    softDepend = listOf("floodgate", "claims-paper","resource-pack-loader")
}