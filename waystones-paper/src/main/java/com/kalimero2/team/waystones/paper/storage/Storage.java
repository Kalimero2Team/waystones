package com.kalimero2.team.waystones.paper.storage;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.display.DisplayManager;
import com.kalimero2.team.waystones.paper.util.Category;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.Visibility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Storage {

    private final DisplayManager display;
    private final PaperWayStones plugin;
    private Connection connection;

    public Storage(PaperWayStones plugin, File dataBase) {
        this.plugin = plugin;
        this.display = plugin.getDisplayManager();

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBase.getPath());
            createTablesIfNotExists();
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getSLF4JLogger().error("Error while creating database connection", e);
        }
    }

    @NotNull
    private StoredWaystone getWaystoneFromResultSet(ResultSet resultSet) throws SQLException {
        return new StoredWaystone(resultSet.getInt("ID"),
                resultSet.getString("NAME"),
                resultSet.getString("OWNER_UUID"),
                resultSet.getInt("VISIBILITY"),
                plugin.getManager().getCategory(resultSet.getInt("CATEGORY")),
                resultSet.getInt("BLOCK_X"),
                resultSet.getInt("BLOCK_Y"),
                resultSet.getInt("BLOCK_Z"),
                resultSet.getString("WORLD_UUID"),
                resultSet.getInt("USES"));
    }

    @NotNull
    private List<StoredWaystone> getWaystonesFromResultSet(ResultSet resultSet) throws SQLException {
        List<StoredWaystone> waystones = new ArrayList<>();
        while (resultSet.next()) {
            waystones.add(getWaystoneFromResultSet(resultSet));
        }
        return waystones;
    }

    private ResultSet executeQuery(@Language(value = "SQL") String query, Object... args) {
        // TODO: Remove Try-Catch and handle exceptions at the right places
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int executeUpdate(@Language(value = "SQL") String sql, Object... args) {
        // TODO: Remove Try-Catch and handle exceptions at the right places
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private void createTablesIfNotExists() {
        createCategoriesTableIfNotExists();
        createWaystonesTableIfNotExists();
        createAccessListTableIfNotExists();
        createFavoriteTableIfNotExists();
        createSortModeTableIfNotExists();
    }

    private void createCategoriesTableIfNotExists() {
        // ID, NAME, PUBLIC(BOOLEAN)

        executeUpdate("CREATE TABLE IF NOT EXISTS CATEGORIES(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL," +
                "PUBLIC BOOLEAN NOT NULL" +
                ");");

    }

    private void createWaystonesTableIfNotExists() {
        // ID, NAME, Owner(UUID), VISIBILITY, CATEGORY, CHUNK_X, CHUNK_Z , X, Y, Z, WORLD(UUID), USES

        executeUpdate("CREATE TABLE IF NOT EXISTS WAYSTONES(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL," +
                "OWNER_UUID VARCHAR(36) NOT NULL," +
                "VISIBILITY TINYINT NOT NULL," +
                "CATEGORY INTEGER REFERENCES CATEGORIES(ID) NULL," +
                "CHUNK_X INTEGER NOT NULL," +
                "CHUNK_Z INTEGER NOT NULL," +
                "BLOCK_X INTEGER NOT NULL," +
                "BLOCK_Y INTEGER NOT NULL," +
                "BLOCK_Z INTEGER NOT NULL," +
                "WORLD_UUID VARCHAR(36) NOT NULL," +
                "USES INTEGER NOT NULL" +
                ");");

    }

    private void createFavoriteTableIfNotExists() {
        // ID, WAYSTONE, PLAYER

        executeUpdate("CREATE TABLE IF NOT EXISTS FAVORITES(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "WAYSTONE INTEGER NOT NULL REFERENCES WAYSTONES(ID)," +
                "PLAYER VARCHAR(36) NOT NULL" +
                ");");

    }


    //
    //  WAYSTONES
    //

    private void createAccessListTableIfNotExists() {
        // ID, WAYSTONE, PLAYER

        executeUpdate("CREATE TABLE IF NOT EXISTS ACCESSLISTS(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "WAYSTONE INTEGER NOT NULL REFERENCES WAYSTONES(ID)," +
                "PLAYER VARCHAR(36) NOT NULL" +
                ");");

    }

    private void createSortModeTableIfNotExists() {
        // PLAYER, MODE

        executeUpdate("CREATE TABLE IF NOT EXISTS SORTMODE(" +
                "PLAYER VARCHAR(36) PRIMARY KEY," +
                "MODE TINYINT NOT NULL" +
                ");");

    }

    public void addWaystone(@NotNull String name, @NotNull UUID owner, int visibility, int category, @NotNull int x, @NotNull int y, @NotNull int z, @NotNull UUID world) {
        executeUpdate("INSERT INTO WAYSTONES(NAME, OWNER_UUID, VISIBILITY, CATEGORY, CHUNK_X, CHUNK_Z, BLOCK_X, BLOCK_Y, BLOCK_Z, WORLD_UUID, USES) VALUES(?,?,?,?,?,?,?,?,?,?,?);", name, owner, visibility, category, (x >> 4), (z >> 4), x, y, z, world, 0);
    }

    public void removeWaystone(int id) {
        executeUpdate("DELETE FROM WAYSTONES WHERE ID = ?;", id);
    }

    public void renameWaystone(int id, String newName) {
        executeUpdate("UPDATE WAYSTONES SET NAME = ? WHERE ID = ?;", newName, id);
    }

    public void updateWaystone(StoredWaystone waystone) {
        executeUpdate("UPDATE WAYSTONES SET NAME = ?, OWNER_UUID = ?, VISIBILITY = ?, CATEGORY = ?, CHUNK_X = ?, CHUNK_Z = ?, BLOCK_X = ?, BLOCK_Y = ?, BLOCK_Z = ?, WORLD_UUID = ?, USES = ? WHERE ID = ?;", waystone.name(), waystone.owner(), waystone.visibility().ordinal(), waystone.category().id(), waystone.chunk_x(), waystone.chunk_z(), waystone.block_x(), waystone.block_y(), waystone.block_z(), waystone.world(), waystone.uses(), waystone.id());
        display.updateDisplay(getWaystone(waystone.id()));
    }

    /**
     * Get a waystone by its id
     *
     * @param id ID of the requested Waystone
     * @return the waystone if it exists, otherwise returns null
     */
    public StoredWaystone getWaystone(int id) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE ID = ?;", id)) {
            if (resultSet.next()) {
                return getWaystoneFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a waystone by its name
     *
     * @param name Name of the requested Waystone
     * @return the waystone if it exists, otherwise returns null
     */
    public StoredWaystone getWaystone(String name) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE NAME = ? ORDER BY ID;", name)) {
            if (resultSet.next()) {
                return getWaystoneFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a waystone by its location
     *
     * @param location Location of the requested Waystone
     * @return the waystone if it exists, otherwise returns null
     */
    public StoredWaystone getWaystone(Location location) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE BLOCK_X = ? AND BLOCK_Y = ? AND BLOCK_Z = ? AND WORLD_UUID = ?;", location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getWorld().getUID())) {
            if (resultSet.next()) {
                return getWaystoneFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<StoredWaystone> getWaystones() {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES;")) {
            return getWaystonesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();

    }

    //
    // Access list
    //

    public void setVisibility(int id, Visibility visibility) {
        executeUpdate("UPDATE WAYSTONES SET VISIBILITY = ? WHERE ID = ?;", visibility.ordinal(), id);
    }

    // TODO: Is this method required? It is not used anywhere
    public boolean hasAccess(OfflinePlayer player, int id) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM ACCESSLISTS WHERE PLAYER = ?;", player.getUniqueId())) {
            return resultSet.next();
        } catch (SQLException ignored) {
        }
        return false;
    }

    public void addAccess(OfflinePlayer player, int id) {
        executeUpdate("INSERT INTO ACCESSLISTS(PLAYER, WAYSTONE) VALUES(?, ?);", player.getUniqueId(), id);
    }

    public void removeAccess(OfflinePlayer player, int id) {
        executeUpdate("DELETE FROM ACCESSLISTS WHERE PLAYER = ? AND WAYSTONE = ?;", player.getUniqueId(), id);
    }


    public List<OfflinePlayer> getAccessList(int id) {
        List<OfflinePlayer> result = new ArrayList<>();
        try (ResultSet resultSet = executeQuery("SELECT * FROM ACCESSLISTS WHERE WAYSTONE = ?;", id)) {
            while (resultSet.next()) {
                result.add(Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("PLAYER"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }


    //
    // Favorites
    //
    public List<Integer> getFavorites(Player player) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM FAVORITES WHERE PLAYER = ?;", player.getUniqueId())) {
            List<Integer> favorites = new ArrayList<>();
            while (resultSet.next()) {
                favorites.add(resultSet.getInt("WAYSTONE"));
            }
            return favorites;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void addFavorite(Player player, int id) {
        executeUpdate("INSERT INTO FAVORITES(PLAYER, WAYSTONE) VALUES(?, ?);", player.getUniqueId(), id);
    }

    public void removeFavorite(Player player, int id) {
        executeUpdate("DELETE FROM FAVORITES WHERE PLAYER = ? AND WAYSTONE = ?;", player.getUniqueId(), id);
    }


    //
    // SortMode
    //

    public SortMode getSortMode(Player player) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM SORTMODE WHERE PLAYER = ?;", player.getUniqueId())) {
            return SortMode.valueByNumber(resultSet.getInt("MODE"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return SortMode.ALPHABETICAL;
    }

    public void setSortMode(Player player, SortMode sortMode) {
        executeUpdate("INSERT OR REPLACE INTO SORTMODE(PLAYER, MODE) VALUES(?, ?);", player.getUniqueId(), sortMode.ordinal());
    }


    //
    // Categories
    //

    public List<Category> getCategories() {
        List<Category> result = new ArrayList<>();
        try (ResultSet resultSet = executeQuery("SELECT * FROM CATEGORIES;")) {
            while (resultSet.next()) {
                result.add(new Category(resultSet.getInt("ID"), resultSet.getString("NAME"), resultSet.getBoolean("PUBLIC")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (result.size() == 0) {
            result.add(Category.NONE);
        }
        return result;
    }

    public @Nullable Category getCategory(int id) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM CATEGORIES WHERE ID = ?;", id)) {
            if (resultSet.next()) {
                return new Category(id, resultSet.getString("NAME"), resultSet.getBoolean("PUBLIC"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean addCategory(String name, boolean isPublic) {
        return executeUpdate("INSERT INTO CATEGORIES(NAME, PUBLIC) VALUES(?, ?);", name, isPublic) == 1;
    }

    public void removeCategory(String name) {
        executeUpdate("DELETE FROM CATEGORIES WHERE NAME = ?;", name);
    }

    public void removeCategory(int id) {
        executeUpdate("DELETE FROM CATEGORIES WHERE ID = ?;", id);
    }


    public void decreaseGlobalUsesScore() {
        executeUpdate("UPDATE WAYSTONES SET USES = USES / 1.5;");
    }

    public void updateUses(StoredWaystone waystone) {
        executeUpdate("UPDATE WAYSTONES SET USES = ? WHERE ID = ?;", waystone.uses(), waystone.id());
    }
}