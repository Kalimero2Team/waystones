package com.kalimero2.team.waystones.paper.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerData {

    private Player player;


    private static String pluginID = "waystones";
    private static NamespacedKey favoritesKey = new NamespacedKey(pluginID, "favorites");
    private static NamespacedKey sortModeKey = new NamespacedKey(pluginID, "sort");

    public PlayerData(Player player) {
        this.player = player;
        PersistentDataContainer data = player.getPersistentDataContainer();
        if (!data.has(favoritesKey)) {
            data.set(favoritesKey, PersistentDataType.INTEGER_ARRAY, new int[0]);
        }
        if (!data.has(sortModeKey)) {
            data.set(sortModeKey, PersistentDataType.INTEGER, 0);
        }
    }



    // Favorites

    public int[] favorites() {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return data.get(favoritesKey, PersistentDataType.INTEGER_ARRAY);
    }

    public void favorites(int[] favorites) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(favoritesKey, PersistentDataType.INTEGER_ARRAY, favorites);
    }

    public void addFavorite(int id) {
        int[] favorites = favorites();
        favorites = Arrays.copyOf(favorites, favorites.length + 1);
        favorites[favorites.length - 1] = id;
        favorites(favorites);
    }

    public void removeFavorite(int id) {
        int[] favorites = favorites();
        int[] newFavorites = new int[favorites.length-1];
        for (int i = 0, k = 0; i < favorites.length; i++) {
            if (favorites[i] == id) {
                continue;
            }
            newFavorites[k++] = favorites[i];
        }
        favorites(newFavorites);
    }



    // Sort Mode

    public SortMode sortMode() {
        PersistentDataContainer data = player.getPersistentDataContainer();
        return SortMode.valueByNumber(data.get(sortModeKey, PersistentDataType.INTEGER));
    }

    public void sortMode(int mode) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(sortModeKey, PersistentDataType.INTEGER, mode);
    }

    public void sortMode(SortMode mode) {
        PersistentDataContainer data = player.getPersistentDataContainer();
        data.set(sortModeKey, PersistentDataType.INTEGER, mode.ordinal());
    }
}
