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
    maven("https://repo.opencollab.dev/main")
    maven("https://hub.jeff-media.com/nexus/repository/jeff-media-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://jitpack.io")
}

dependencies {
    paperDevBundle(libs.versions.paper.api.get())
    bukkitLibrary(libs.cloud.paper)

    api(libs.geyser.base.api)
    api(libs.geyser.api)
    api(libs.geyser.core){
        isTransitive = false
    }

    compileOnly(libs.floodgate.api)
    compileOnly("com.github.Kalimero2Team:claims:7ba760c75b")
    implementation(libs.customblockdata)
    implementation(libs.morepersistentdatatypes)
    implementation(libs.anvilgui)
    implementation(project(":waystones-api"))
}

tasks{
    assemble {
        dependsOn(reobfJar)
    }

    shadowJar{
        fun reloc(pkg: String, name: String) = relocate(pkg, "com.kalimero2.team.claims.paper.shaded.$name")
        reloc("com.jeff_media.customblockdata","customblockdata")
        reloc("com.jeff_media.morepersistentdatatypes","morepersistentdatatypes")
        reloc("net.wesjd.anvilgui","anvilgui")
    }
}

bukkit {
    main = "com.kalimero2.team.waystones.paper.PaperWayStones"
    apiVersion = "1.19"
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
    authors = listOf("byquanton")
    softDepend = listOf("floodgate","claims-paper")
    loadBefore = listOf("Geyser-Spigot")
}