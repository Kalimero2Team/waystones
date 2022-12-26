dependencyResolutionManagement{
    versionCatalogs{
        create("libs"){
            // Core
            plugin("shadow","com.github.johnrengelman.shadow").version("7.1.2")

            version("floodgate-api","2.0-SNAPSHOT")
            version("geyser-baseApi","2.1.0-byquanton-SNAPSHOT")
            version("geyser-geyserApi","2.1.0-byquanton-SNAPSHOT")
            version("geyser-geyserCore","2.1.0-byquanton-SNAPSHOT")
            version("sqlite","3.40.0.0")
            version("cloud", "1.8.0")
            version("customblockdata","2.1.0")
            version("morepersistentdatatypes","2.3.1")

            library("floodgate-api","org.geysermc.floodgate","api").versionRef("floodgate-api")
            library("geyser-base-api","org.geysermc","api").versionRef("geyser-baseApi")
            library("geyser-api","org.geysermc.geyser","api").versionRef("geyser-geyserApi")
            library("geyser-core","org.geysermc.geyser","core").versionRef("geyser-geyserCore")
            library("sqlite","org.xerial","sqlite-jdbc").versionRef("sqlite")
            library("customblockdata","com.jeff_media","CustomBlockData").versionRef("customblockdata")
            library("morepersistentdatatypes","com.jeff_media","MorePersistentDataTypes").versionRef("morepersistentdatatypes")

            // Paper
            plugin("paper-run","xyz.jpenilla.run-paper").version("2.0.1")
            plugin("paper-userdev","io.papermc.paperweight.userdev").version("1.4.0")
            plugin("plugin-yml","net.minecrell.plugin-yml.bukkit").version("0.5.2")

            version("paper-api","1.19.3-R0.1-SNAPSHOT")

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
include("waystones-api")
include("waystones-paper")
include("waystones-geyser-extension")
