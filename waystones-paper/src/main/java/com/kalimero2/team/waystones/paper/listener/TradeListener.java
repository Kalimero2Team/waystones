package com.kalimero2.team.waystones.paper.listener;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class TradeListener implements Listener {

    private final PaperWayStones plugin;

    public TradeListener(PaperWayStones plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityInteract(PrePlayerAttackEntityEvent event) {
        if (event.getAttacked() instanceof ArmorStand armorStand) {
            if (armorStand.getPersistentDataContainer().has(new NamespacedKey("waystones", "trader"))) {

                Merchant merchant = Bukkit.createMerchant(Component.text("Waystone Händlerin"));
                List<MerchantRecipe> recipes = new ArrayList<>();

                MerchantRecipe wayStone = new MerchantRecipe(plugin.getStatic(), 0, 99, false);

                wayStone.addIngredient(new ItemStack(Material.NETHERITE_INGOT, 2));
                wayStone.addIngredient(new ItemStack(Material.ENDER_EYE, 1));

                recipes.add(wayStone);

                MerchantRecipe portableWayStone = new MerchantRecipe(plugin.getPortable(), 0, 99, false);

                portableWayStone.addIngredient(plugin.getStatic());
                portableWayStone.addIngredient(new ItemStack(Material.NETHER_STAR, 1));

                recipes.add(portableWayStone);

                merchant.setRecipes(recipes);
                event.getPlayer().openMerchant(merchant, true);
            }
        }
    }


}
