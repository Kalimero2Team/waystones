package com.kalimero2.team.waystones.paper.ui;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class SmallInventoryScreen implements GenericScreen, Listener {

    protected final PaperWayStones plugin;
    private final Inventory inventory;
    private final HashMap<Integer, Consumer<Player>> slotMapping = new HashMap<>();

    public SmallInventoryScreen(PaperWayStones plugin, Component title) {
        this.plugin = plugin;
        Component inventoryOverlay = MiniMessage.miniMessage().deserialize("<white><lang:space.-8><font:klm2:waystones>b</font><reset><lang:space.-170>");
        // Implement title updating sometime?
        this.inventory = plugin.getServer().createInventory(null, 9*2, inventoryOverlay.append(title));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    protected Inventory getInventory() {
        return inventory;
    }

    protected void setSlot(int slot, ItemStack item, Consumer<Player> onClick) {
        slotMapping.put(slot, onClick);
        inventory.setItem(slot, item);
    }

    /**
     * Creates a blank button
     * @param slot the slot to put the button in
     * @param name the name of the button
     * @param onClick the action to perform when the button is clicked
     */
    protected void createButton(int slot, Component name, Consumer<Player> onClick) {
        createButton(slot, name, onClick, 0);
    }

    /**
     * Creates a button with the specified model data.
     * @param slot the slot to put the button in
     * @param name the name of the button
     * @param onClick the action to perform when the button is clicked
     * @param modelData the model data of the button
     */
    protected void createButton(int slot, Component name, Consumer<Player> onClick, int modelData) {
        createButton(slot, name, onClick, modelData, Material.PAPER);
    }


    /**
     * Creates a button with the specified model data and material.
     * @param slot the slot to put the button in
     * @param name the name of the button
     * @param onClick the action to perform when the button is clicked
     * @param modelData the model data of the button
     * @param material the material of the button
     */
    protected void createButton(int slot, Component name, Consumer<Player> onClick, int modelData, Material material) {
        ItemStack blankButton = new ItemStack(material);
        ItemMeta itemMeta = blankButton.getItemMeta();
        itemMeta.displayName(name.decoration(TextDecoration.ITALIC, false));
        itemMeta.setCustomModelData(modelData);
        blankButton.setItemMeta(itemMeta);
        setSlot(slot, blankButton, onClick);
    }

    @Override
    public void show(Player player) {
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != inventory) return;
        event.setCancelled(true);

        if (event.getWhoClicked() instanceof Player player) {
            Consumer<Player> onClick = slotMapping.get(event.getSlot());
            if (onClick != null) {
                onClick.accept(player);
            }
        }
    }

    @EventHandler
    public void onItemMove(InventoryMoveItemEvent event){
        if (event.getDestination() == inventory) {
            event.setCancelled(true);
        }
    }



}
