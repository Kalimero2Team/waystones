dependencyResolutionManagement{
    versionCatalogs{
        create("libs"){
            // Core
            plugin("shadow","com.github.johnrengelman.shadow").version("7.1.2")

            version("floodgate-api","2.2.0-SNAPSHOT")
            version("geyser-baseApi","2.1.0-byquanton-SNAPSHOT")
            version("geyser-geyserApi","2.1.0-byquanton-SNAPSHOT")
            version("geyser-geyserCore","2.1.0-byquanton-SNAPSHOT")
            version("sqlite","3.40.0.0")
            version("cloud", "1.8.0")
            version("customblockdata","2.2.0")
            version("morepersistentdatatypes","2.4.0")
            version("anvilgui","1.6.6-SNAPSHOT")
            version("protocollib","4.7.0")

            library("floodgate-api","org.geysermc.floodgate","api").versionRef("floodgate-api")
            library("geyser-base-api","org.geysermc","api").versionRef("geyser-baseApi")
            library("geyser-api","org.geysermc.geyser","api").versionRef("geyser-geyserApi")
            library("geyser-core","org.geysermc.geyser","core").versionRef("geyser-geyserCore")
            library("sqlite","org.xerial","sqlite-jdbc").versionRef("sqlite")
            library("customblockdata","com.jeff_media","CustomBlockData").versionRef("customblockdata")
            library("morepersistentdatatypes","com.jeff_media","MorePersistentDataTypes").versionRef("morepersistentdatatypes")
            library("protocollib","com.comphenix.protocol","ProtocolLib").versionRef("protocollib")
            library("anvilgui","net.wesjd","anvilgui").versionRef("anvilgui")

            // Paper
            plugin("paper-run","xyz.jpenilla.run-paper").version("2.2.0")
            plugin("paper-userdev","io.papermc.paperweight.userdev").version("1.5.5")
            plugin("plugin-yml","net.minecrell.plugin-yml.bukkit").version("0.6.0")

            version("paper","1.20.1-R0.1-SNAPSHOT")

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
// include("waystones-geyser-extension")
