dependencyResolutionManagement{
    versionCatalogs{
        create("libs"){
            // Core
            plugin("shadow", "io.github.goooler.shadow").version("8.1.8") // Fork is required for Java 21

            version("floodgate-api","2.2.2-SNAPSHOT")
            version("geyser-geyserApi","2.2.2-SNAPSHOT")
            version("anvilgui","1.9.6-SNAPSHOT")
            version("sqlite","3.47.0.0")
            version("cloud", "1.8.4")

            library("floodgate-api","org.geysermc.floodgate","api").versionRef("floodgate-api")
            library("geyser-api","org.geysermc.geyser","api").versionRef("geyser-geyserApi")
            library("sqlite","org.xerial","sqlite-jdbc").versionRef("sqlite")
            library("anvilgui","net.wesjd","anvilgui").versionRef("anvilgui")

            // Paper
            plugin("paper-run","xyz.jpenilla.run-paper").version("2.3.0")
            plugin("plugin-yml","net.minecrell.plugin-yml.bukkit").version("0.6.0")

            version("paper","1.20.6-R0.1-SNAPSHOT")

            library("paper","io.papermc.paper","paper-api").versionRef("paper")
            library("cloud-paper","cloud.commandframework","cloud-paper").versionRef("cloud")
        }
    }
}

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

rootProject.name = "waystones"
include("waystones-paper")
include("waystones-geyser-extension")
