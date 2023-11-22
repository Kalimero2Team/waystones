package com.kalimero2.team.waystones.paper;

import com.kalimero2.team.waystones.paper.command.CommandManager;
import com.kalimero2.team.waystones.paper.compat.ClaimsIntegration;
import com.kalimero2.team.waystones.paper.compat.FloodgateIntegration;
import com.kalimero2.team.waystones.paper.compat.LegacyConverter;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.listener.ChunkListener;
import com.kalimero2.team.waystones.paper.listener.WayStonesListener;
import com.kalimero2.team.waystones.paper.storage.WaystoneManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

public class PaperWayStones extends JavaPlugin {
    public @Nullable FloodgateIntegration floodgateIntegration;
    public @Nullable ClaimsIntegration claimsIntegration;

    public static WaystoneManager manager;
    public static DisplayManager displayManager;

    @Override
    public void onEnable() {
        //TODO: API
        //WayStonesApiHolder.setApi(this);

        // Claims Compat

        try {
            Class.forName("com.kalimero2.team.claims.api.ClaimsApi");
            claimsIntegration = new ClaimsIntegration();
            getLogger().info("Claims integration enabled");
        } catch (ClassNotFoundException e) {
            claimsIntegration = null;
            getLogger().info("Claims not found, disabling Claims integration");
        }



        // Display Manager

        displayManager = new DisplayManager(this);


        // Storage

        getDataFolder().mkdirs();
        manager = new WaystoneManager(this, new File(getDataFolder(), "waystones.db"));
        manager.load();

        if (!getConfig().getBoolean("did-legacy-conversion", false)) {
            new LegacyConverter(this).convert();
        }


        // Floodgate Compat

        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            floodgateIntegration = new FloodgateIntegration(this);
            getLogger().info("Floodgate integration enabled");
        } catch (ClassNotFoundException e) {
            floodgateIntegration = null;
            getLogger().info("Floodgate not found, disabling Floodgate integration");
        }


        // Commands

        try {
            new CommandManager(this);
        } catch (Exception e) {
            getLogger().warning("Failed to register commands");
        }


        // Event Listeners

        new WayStonesListener( this);
        new ChunkListener(this);
    }

    @Override
    public void onDisable() {

    }

    public WaystoneManager getManager() {
        return manager;
    }
    public DisplayManager getDisplayManager() {
        return displayManager;
    }

    public ItemStack getStatic() {
        ItemStack item = new ItemStack(Material.STONE_BRICK_WALL);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.translatable("waystones.item.static").decoration(TextDecoration.ITALIC, false));
        itemMeta.lore(List.of(Component.translatable("waystones.item.static.description").decoration(TextDecoration.ITALIC, false)));
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.setCustomModelData(22022);
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(this, "static"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(itemMeta);
        return item;
    }

    public ItemStack getPortable() {
        ItemStack item = new ItemStack(Material.STONE_BRICK_WALL);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.displayName(Component.translatable("waystones.item.portable").decoration(TextDecoration.ITALIC, false));
        itemMeta.lore(List.of(Component.translatable("waystones.item.portable.description").decoration(TextDecoration.ITALIC, false)));
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.setCustomModelData(22023);
        PersistentDataContainer dataContainer = itemMeta.getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(this, "portable"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(itemMeta);
        return item;
    }

}
