dependencyResolutionManagement{
    versionCatalogs{
        create("libs"){
            // Core
            plugin("shadow", "io.github.goooler.shadow").version("8.1.8") // Fork is required for Java 21

            version("floodgate-api","2.2.2-SNAPSHOT")
            version("geyser-geyserApi","2.2.2-SNAPSHOT")
            version("anvilgui","1.9.6-SNAPSHOT")
            version("sqlite","3.47.0.0")
            version("cloud", "2.0.0-beta.10")

            library("floodgate-api","org.geysermc.floodgate","api").versionRef("floodgate-api")
            library("geyser-api","org.geysermc.geyser","api").versionRef("geyser-geyserApi")
            library("sqlite","org.xerial","sqlite-jdbc").versionRef("sqlite")
            library("anvilgui","net.wesjd","anvilgui").versionRef("anvilgui")

            // Paper
            plugin("paper-run","xyz.jpenilla.run-paper").version("2.3.1")
            plugin("plugin-yml","net.minecrell.plugin-yml.bukkit").version("0.6.0")

            version("paper","26.2.build.123-stable")

            library("paper","io.papermc.paper","paper-api").versionRef("paper")
            library("cloud-paper","org.incendo","cloud-paper").versionRef("cloud")
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
