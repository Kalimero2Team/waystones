package com.kalimero2.team.waystones.paper.storage;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.PlayerWaystoneCombo;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.Visibility;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;

public class WaystoneManager {

    private final PaperWayStones plugin;
    private final Storage storage;


    public WaystoneManager(PaperWayStones plugin, File file) {
        this.plugin = plugin;
        storage = new Storage(plugin, file);
    }


    public void load() {
        plugin.getLogger().info("Loading data into cache...");
        for (Category category : storage.getCategories()) {
            categories.put(category.id(), category);
        }
        for (StoredWaystone waystone : storage.getWaystones()) {
            waystones.put(waystone.id(), waystone);
        }
        for (StoredWaystone waystone : storage.getWaystones()) {
            waystoneLocations.put(waystone.location(), waystone);
        }
        for (StoredWaystone waystone : storage.getWaystones()) {
            waystoneNames.put(waystone.name(), waystone);
        }
        plugin.getLogger().info("Loaded " + waystones.size() + " waystones into cache.");
    }


    //
    // Cache Mechanics
    //

    /**
     * Deletes all cache data of a player.
     * Needs to be run, when a player leaves the server.
     */
    public void removePlayerCache(Player player) {
        book.remove(player);
        sortModes.remove(player);
        favorites.remove(player);
    }

    /**
     * A function to get an object from a HashMap or load the object from the database if it's not in the cache yet.
     * @param key The key in the HashMap
     * @param cache The HashMap to store the object in
     * @param function The function to load the object from the database, if it's not in the cache.
     * @param K The class of the Key in the HashMap
     * @param T The class of the Object in the HashMap
     * @return The object stored in the cache / the database
     */
    private <K, T> T getOrCreateCacheObject(K key, @NotNull HashMap<K, T> cache, Function<K, T> function) {
        T object = cache.get(key);
        if (object == null) {
            object = function.apply(key);
            if (object != null) cache.put(key, object);
        }
        return object;
    }

    /**
     * A function to get a list of objects from a HashMap or load the list from the database if it's not in the cache yet.
     * @param key The key in the HashMap
     * @param cache The HashMap to store the list in
     * @param function The function to load the list from the database, if it's not in the cache.
     * @param K The class of the Key in the HashMap
     * @param T The class of the Object in the list in the HashMap
     * @return The list of objects stored in the cache / the database
     */
    private <K, T> List<T> getOrCreateCacheList(K key, HashMap<K, List<T>> cache, Function<K, List<T>> function) {
        List<T> list = cache.get(key);
        if (list == null) {
            list = function.apply(key);
            if (list != null) cache.put(key, list);
        }
        return list;
    }

    /**
     * A function to get a list of objects from a HashMap or load the list from the database if it's not in the cache yet.
     * @param primaryKey The primary key in the HashMap
     * @param secondaryKey The secondary key in the HashMap
     * @param cache The HashMap to store the list in
     * @param function The function to load the list from the database, if it's not in the cache.
     * @param K The class of the primary key in the HashMap
     * @param S The class of the secondary key in the HashMap
     * @param T The class of the object in the list in the HashMap
     * @return The list of objects stored in the cache / the database
     */
    private <K, S, T> List<T> getOrCreateCacheListMap(@NotNull K primaryKey, @NotNull S secondaryKey, @NotNull HashMap<K, HashMap<S, List<T>>> cache, @NotNull BiFunction<K, S, List<T>> function) {
        HashMap<S, List<T>> map = cache.computeIfAbsent(primaryKey, k -> new HashMap<>());
        List<T> list = map.get(secondaryKey);
        if (list == null) {
            list = function.apply(primaryKey, secondaryKey);
            if (list != null) {
                cache.put(primaryKey, map);
                map.put(secondaryKey, list);
            }
        }
        return list;
    }


    //
    // Waystones
    //

    private final HashMap<UUID, StoredWaystone> waystones = new HashMap<>();
    private final HashMap<Location, StoredWaystone> waystoneLocations = new HashMap<>();
    private final HashMap<String, StoredWaystone> waystoneNames = new HashMap<>();


    /**
     * Returns all waystones
     */
    public Collection<StoredWaystone> getWaystones() {
        return waystones.values();
    }

    /**
     * Returns all waystones in the given world
     */
    public List<StoredWaystone> getWaystones(World world) {
        return getWaystones(world.getUID());
    }

    public List<StoredWaystone> getWaystones(UUID world) {
        List<StoredWaystone> waystones = new ArrayList<>();
        for (StoredWaystone waystone : this.waystones.values()) {
            if (waystone.world().equals(world)) waystones.add(waystone);
        }
        return waystones;
    }


    /**
     * Returns all waystones in the given chunk
     * @param chunk the chunk
     * @return a list of waystones
     */
    public List<StoredWaystone> getWaystones(Chunk chunk) {
        List<StoredWaystone> waystones = new ArrayList<>();
        for (StoredWaystone waystone : this.waystones.values()) {
            if (waystone.location().getChunk().equals(chunk)) waystones.add(waystone);
        }
        return waystones;
    }

    /**
     * Returns the waystone with the given id
     * @return null if there is no waystone with the given id
     */
    public StoredWaystone getWaystone(UUID id) {
        return getOrCreateCacheObject(id, waystones, storage::getWaystone);
    }

    /**
     * Returns the waystone at the given location
     * @return null if there is no waystone at the given location
     */
    public StoredWaystone getWaystone(Location location) {
        return getOrCreateCacheObject(location.toBlockLocation(), waystoneLocations, storage::getWaystone);
    }

    /**
     * Returns the waystone with the given name
     * @return null if there is no waystone with the given name
     */
    public StoredWaystone getWaystone(String name) {
        return getOrCreateCacheObject(name, waystoneNames, storage::getWaystone);
    }

    /**
     * Checks if a name is free
     * @return true if the name is free, false if the name is already taken
     */
    public boolean isNameUsed(String newName) {
        return getWaystone(newName) != null;
    }

    /**
     * Create a new Waystone
     * @param name The name of the waystone
     * @param owner The owner of the waystone
     * @param location The location, where the waystone stands
     * @return null if the name is already taken, else the created waystone
     */
    public StoredWaystone createWaystone(String name, UUID owner, int visibility, int category, Location location) {
        if (isNameUsed(name)) return null;
        storage.addWaystone(name, owner, visibility, category, location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getUID());
        return getWaystone(location);
    }


    /**
     * Removes the waystone with the given ID
     * @param id the ID of the waystone to be removed
     * @return true if the waystone was removed successfully, false if the waystone does not exist
     */
    public boolean removeWaystone(UUID id) {
        boolean removed = false;
        if (storage.getWaystone(id) != null) {
            storage.removeWaystone(id);
            removed = true;
        }
        return removed;
    }

    /**
     * Rename a waystone
     * @param id the ID of the waystone to be renamed
     * @param newName the new name of the waystone
     * @return true if the waystone was renamed successfully, false if the newName is already taken
     */
    public boolean renameWaystone(UUID id, String newName) {
        if (isNameUsed(newName)) return false;
        storage.renameWaystone(id, newName);
        waystones.put(id, storage.getWaystone(id));
        waystoneLocations.put(waystones.get(id).location(), waystones.get(id));
        waystoneNames.put(waystones.get(id).name(), waystones.get(id));
        return true;
    }



    //
    // Editing Waystones
    //

    /**
     * Update a waystone
     */
    public void updateWaystone(StoredWaystone waystone) {
        storage.updateWaystone(waystone);
        waystones.put(waystone.id(), waystone);
        waystoneLocations.put(waystone.location(), waystone);
        waystoneNames.put(waystone.name(), waystone);
    }

    /**
     * Change the visibility of a waystone
     * @param id The ID of the waystone
     * @param visibility the new visibility of the waystone
     */
    public void setVisibility(UUID id, Visibility visibility) {
        storage.setVisibility(id, visibility);
        StoredWaystone waystone = storage.getWaystone(id);
        waystones.put(id, waystone);
        waystoneLocations.put(waystone.location(), waystone);
        waystoneNames.put(waystone.name(), waystone);
    }



    //
    // Waystone List for Player
    //

    private final HashMap<Player, HashMap<UUID, List<StoredWaystone>>> book = new HashMap<>();

    public List<StoredWaystone> getWaystones(Player player) {
        return getOrCreateCacheListMap(player, player.getWorld().getUID(), book, this::internalGetWaystones);
    }
    public List<StoredWaystone> getWaystones(Player player, UUID world) {
        return getOrCreateCacheListMap(player, world, book, this::internalGetWaystones);
    }

    /**
     * Returns all waystones, whose name contains the given string
     * @return null if there is no waystone, whose name contains the given string
     */
    public List<StoredWaystone> getWaystones(UUID world, String term) {
        List<StoredWaystone> result = new ArrayList<>();
        for (StoredWaystone waystone : waystones.values()) {
            if (waystone.name().toLowerCase().contains(term.toLowerCase()) && waystone.world().equals(world)) result.add(waystone);
        }
        return result;
    }

    private List<StoredWaystone> internalGetWaystones(Player player, UUID world) {
        List<UUID> favIds = getFavorites(player);
        List<StoredWaystone> result = sort(getSortMode(player), getWaystones(world));
        List<StoredWaystone> favs = new ArrayList<>();
        for (StoredWaystone waystone : result) {
            if (favIds.contains(waystone.id())) favs.add(waystone);
        }
        result.removeAll(favs);
        result.addAll(0, favs);
        return result;
    }



    //
    // Categories
    //

    private final HashMap<Integer, Category> categories = new HashMap<>();

    public Collection<Category> getCategories() {
        return categories.values();
    }

    public Category getCategory(int id) {
        return getOrCreateCacheObject(id, categories, storage::getCategory);
    }

    public Category getCategory(String name) {
        for (Category category : storage.getCategories()) {
            if (category.name().equalsIgnoreCase(name)) return category;
        }
        return null;
    }

    public Category addCategory(String name, boolean publicCategory) {
        if (categoryExists(name)) return null;
        storage.addCategory(name, publicCategory);
        return getCategory(name);
    }

    private boolean categoryExists(String name) {
        for (Category category : storage.getCategories()) {
            if (category.name().equalsIgnoreCase(name)) return true;
        }
        return false;
    }



    //
    // Sort Modes
    //

    private final HashMap<Player, SortMode> sortModes = new HashMap<>();

    public SortMode getSortMode(Player player) {
        return getOrCreateCacheObject(player, sortModes, storage::getSortMode);
    }

    public void setSortMode(Player player, SortMode sortMode) {
        sortModes.put(player, sortMode);
        storage.setSortMode(player, sortMode);
        if (book.containsKey(player)) book.get(player).clear();
    }

    public List<StoredWaystone> sort(SortMode mode, List<StoredWaystone> list) {
        switch (mode) {
            case ALPHABETICAL -> list.sort(Comparator.comparing(StoredWaystone::name));
            case ALPHABETICAL_DESCENDING -> list.sort(Comparator.comparing(StoredWaystone::getName).reversed());
            case NUMERIC -> list.sort(Comparator.comparing(StoredWaystone::id));
            case NUMERIC_DESCENDING -> list.sort(Comparator.comparing(StoredWaystone::id).reversed());
            case POPULARITY -> list.sort(Comparator.comparing(StoredWaystone::getUses).reversed());
            case POPULARITY_ASCENDING -> list.sort(Comparator.comparing(StoredWaystone::uses));
        }
        return list;
    }


    //
    // Favorites
    //

    private final HashMap<Player, List<UUID>> favorites = new HashMap<>();

    public List<UUID> getFavorites(Player player) {
        return getOrCreateCacheList(player, favorites, storage::getFavorites);
    }

    public void addFavorite(Player player, UUID id) {
        getFavorites(player).add(id);
        storage.addFavorite(player, id);
    }

    public void removeFavorite(Player player, UUID id) {
        getFavorites(player).remove(id);
        storage.addFavorite(player, id);
    }


    //
    // Access
    //

    private final HashMap<UUID, List<OfflinePlayer>> access = new HashMap<>();

    /**
     * Gets the list of players who have access to a waystone
     * @param id the ID of the waystone
     * @return the list of players
     */
    public List<OfflinePlayer> getAccess(UUID id) {
        return getOrCreateCacheList(id, access, storage::getAccessList);
    }


    /**
     * Adds a player's access to a waystone. Does nothing if player already has access to waystone
     * @param player the player
     * @param id the ID of the waystone
     */
    public void addAccess(OfflinePlayer player, UUID id) {
        getAccess(id).add(player);
        storage.addAccess(player, id);
    }

    /**
     * Removes a player's access to a waystone
     * @param player the player
     * @param id the ID of the waystone
     */
    public void removeAccess(OfflinePlayer player, UUID id) {
        getAccess(id).remove(player);
        storage.addAccess(player, id);
    }

    /**
     * Checks whether a player has access to a waystone
     * @param player the player
     * @param id the ID of the waystone
     * @return true if player has access
     */
    public boolean hasAccess(OfflinePlayer player, UUID id) {
        return getAccess(id).contains(player);
    }



    //
    // Force Mode
    //

    private final List<Player> forceMode = new ArrayList<>();


    /**
     * Checks whether the player is in force mode
     * @param player the Player
     * @return true if player is in force mode
     */
    public boolean forceMode(Player player) {
        return forceMode.contains(player);
    }


    /**
     * Sets the force mode for the player to on/off
     * @param active true to activate, false to deactivate
     */
    public void forceMode(Player player, boolean active) {
        if (active && !forceMode.contains(player)) {
            forceMode.add(player);
        }
        else forceMode.remove(player);
    }

    /**
     * Toggles force mode for the player
     * @param player the Player
     * @return state of force mode AFTER it was toggled
     */
    public boolean toggleForceMode(Player player) {
        if (forceMode.contains(player)) {
            forceMode.remove(player);
            return false;
        }
        else forceMode.add(player);
        return true;
    }


    /**
     * Checks whether the player can teleport to the waystone
     */
    public boolean canTeleport(StoredWaystone waystone, Player player) {
        return forceMode(player) || hasAccess(player, waystone.id()) || waystone.owner().equals(player.getUniqueId()) || waystone.visibility() != Visibility.PRIVATE;
    }

    /**
     * Checks whether the player can see the waystone in the main menu
     */
    public boolean canSee(StoredWaystone waystone, Player player) {
        return forceMode(player) || hasAccess(player, waystone.id()) || waystone.owner().equals(player.getUniqueId()) || waystone.visibility() == Visibility.PUBLIC;
    }

    /**
     * Checks whether the player can edit the waystone
     */
    public boolean canEdit(StoredWaystone waystone, Player player) {
        return forceMode(player) || waystone.owner().equals(player.getUniqueId());
    }



    //
    // Popularity
    //

    private final HashMap<PlayerWaystoneCombo, Long> teleportTimestamps = new HashMap<>();
//    private final ArrayList<StoredWaystone> waystonesToUpdateTeleportUses = new ArrayList<>();

    public void addTeleport(Player player, UUID waystoneId) {
        boolean countes = true;
        if (teleportTimestamps.containsKey(new PlayerWaystoneCombo(player.getUniqueId(), waystoneId))) {
            if (System.currentTimeMillis() - teleportTimestamps.get(new PlayerWaystoneCombo(player.getUniqueId(), waystoneId)) < 300000) countes = false;   // 5 * 60 * 1000 = 5 minutes
        }
        teleportTimestamps.put(new PlayerWaystoneCombo(player.getUniqueId(), waystoneId), System.currentTimeMillis());
        if (countes) {
            StoredWaystone waystone = storage.getWaystone(waystoneId);
            if (waystone != null) {
                waystone.uses(waystone.uses() + 1);
            }
//            waystonesToUpdateTeleportUses.add(waystone);
            storage.updateUses(waystone);
        }
    }

//    public void updateTeleportUses() {
//        for (StoredWaystone waystone : waystonesToUpdateTeleportUses) {
//            storage.updateUses(waystone);
//        }
//        waystonesToUpdateTeleportUses.clear();
//    }
//    public void updateTeleportUses(StoredWaystone waystone) {
//        if (waystonesToUpdateTeleportUses.remove(waystone)) storage.updateUses(waystone);
//    }

    public void decreaseGlobalUsesScore() {
        storage.decreaseGlobalUsesScore();
        waystones.clear();
        waystoneNames.clear();
        waystoneLocations.clear();
    }
}
