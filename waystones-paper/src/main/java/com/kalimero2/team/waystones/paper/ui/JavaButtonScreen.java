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

public class JavaButtonScreen implements GenericScreen, Listener {

    protected final PaperWayStones plugin;
    private final Inventory inventory;
    private final HashMap<Integer, Consumer<Player>> slotMapping = new HashMap<>();

    protected JavaButtonScreen(ButtonScreen buttonScreen) {
        this.plugin = buttonScreen.getPlugin();
        Component inventoryOverlay = MiniMessage.miniMessage().deserialize("<white><lang:space.-8><font:klm2:waystones>b</font><reset><lang:space.-170>");
        this.inventory = plugin.getServer().createInventory(null, 9*2, inventoryOverlay.append(buttonScreen.getTitle()));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

        buttonScreen.getButtons().forEach((button, consumer) -> {
            createButton(button.slot(), button.name(), consumer, button.modelData(), button.material());
        });
    }

    protected void createButton(int slot, Component name, Consumer<Player> onClick, int modelData, Material material) {
        ItemStack blankButton = new ItemStack(material);
        ItemMeta itemMeta = blankButton.getItemMeta();
        itemMeta.displayName(name.decoration(TextDecoration.ITALIC, false));
        itemMeta.setCustomModelData(modelData);
        blankButton.setItemMeta(itemMeta);
        setSlot(slot, blankButton, onClick);
    }

    protected void setSlot(int slot, ItemStack item, Consumer<Player> onClick) {
        slotMapping.put(slot, onClick);
        inventory.setItem(slot, item);
    }


    @Override
    public void open(Player player) {
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
