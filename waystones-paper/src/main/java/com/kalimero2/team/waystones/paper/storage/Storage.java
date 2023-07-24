package com.kalimero2.team.waystones.paper.storage;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.util.SortMode;
import com.kalimero2.team.waystones.paper.util.Visibility;
import org.bukkit.entity.Player;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Storage {

    private final PaperWayStones plugin;
    private Connection connection;

    public Storage(PaperWayStones plugin, File dataBase) {
        this.plugin = plugin;

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBase.getPath());
            createTablesIfNotExists();
        } catch (ClassNotFoundException | SQLException e) {
            plugin.getSLF4JLogger().error("Error while creating database connection", e);
        }
    }


    public ResultSet executeQuery(@Language(value = "SQL") String sql) {
        try {
            return connection.createStatement().executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int executeUpdate(@Language(value = "SQL") String sql) {
        try {
            return connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }


    private void createTablesIfNotExists() {
        createWaystonesTableIfNotExists();
        createWhitelistTableIfNotExists();
        createFavoriteTableIfNotExists();
        createSortModeTableIfNotExists();
    }

    private void createWaystonesTableIfNotExists() {
        // ID, NAME, Owner(UUID), CHUNK_X, CHUNK_Z , X, Y, Z, WORLD(UUID)

        executeUpdate("CREATE TABLE IF NOT EXISTS WAYSTONES(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL," +
                "OWNER_UUID VARCHAR(36) NOT NULL," +
                "VISIBILITY TINYINT NOT NULL," +
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


    private void createWhitelistTableIfNotExists() {
        // ID, WAYSTONE, PLAYER

        executeUpdate("CREATE TABLE IF NOT EXISTS WHITELISTS(" +
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




    //
    //  WAYSTONES
    //

    public void addWaystone(@NotNull String name, @NotNull UUID owner, int visibility, @NotNull int chunkX, @NotNull int chunkZ, @NotNull int x, @NotNull int y, @NotNull int z, @NotNull UUID world) {
        executeUpdate("INSERT INTO WAYSTONES(NAME, OWNER_UUID, VISIBILITY, CHUNK_X, CHUNK_Z, BLOCK_X, BLOCK_Y, BLOCK_Z, WORLD_UUID, USES) VALUES('" + name + "', '" + owner + "', " + visibility + ", " + chunkX + ", " + chunkZ + ", " + x + ", " + y + ", " + z + ", '" + world + "', 0);");
    }

    public void removeWaystone(int id) {
        executeUpdate("DELETE FROM WAYSTONES WHERE ID = " + id + ";");
    }

    public void updateWaystone(StoredWaystone waystone) {
        executeUpdate("UPDATE WAYSTONES SET NAME = '" + waystone.name() + "', OWNER_UUID = '" + waystone.owner() + "', VISIBILITY = " + waystone.visibility() + ", CHUNK_X = " + waystone.chunk_x() + ", CHUNK_Z = " + waystone.chunk_z() + ", BLOCK_X = " + waystone.block_x() + ", BLOCK_Y = " + waystone.block_y() + ", BLOCK_Z = " + waystone.block_z() + ", WORLD_UUID = '" + waystone.world() + "', USES = '" + waystone.uses() + "' WHERE ID = " + waystone.id() + ";");
    }


    /**
     * Get a waystone by its id
     * @param id ID of the requestd Waystone
     * @return the waystone if it exists, otherwise returns null
     */
    public StoredWaystone getWaystone(int id) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE ID = " + id + ";")) {
            if (resultSet.next()) {
                return new StoredWaystone(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("OWNER_UUID"),
                        resultSet.getInt("VISIBILITY"),
                        resultSet.getInt("CHUNK_X"),
                        resultSet.getInt("CHUNK_Z"),
                        resultSet.getInt("BLOCK_X"),
                        resultSet.getInt("BLOCK_Y"),
                        resultSet.getInt("BLOCK_Z"),
                        resultSet.getString("WORLD_UUID"),
                        resultSet.getInt("USES"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Get a waystone by its name
     * @param name Name of the requestd Waystone
     * @return the waystone if it exists, otherwise returns null
     */
    public StoredWaystone getWaystone(String name) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE NAME = '" + name + "' ORDER BY ID ASC;")) {
            if (resultSet.next()) {
                return new StoredWaystone(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("OWNER_UUID"),
                        resultSet.getInt("VISIBILITY"),
                        resultSet.getInt("CHUNK_X"),
                        resultSet.getInt("CHUNK_Z"),
                        resultSet.getInt("BLOCK_X"),
                        resultSet.getInt("BLOCK_Y"),
                        resultSet.getInt("BLOCK_Z"),
                        resultSet.getString("WORLD_UUID"),
                        resultSet.getInt("USES"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StoredWaystone getWaystone(int block_x, int block_y, int block_z, UUID world) {
        try (ResultSet resultSet = executeQuery("SELECT  * FROM WAYSTONES WHERE BLOCK_X = " + block_x + " AND BLOCK_Y = " + block_y + " AND BLOCK_Z = " + block_z + " AND WORLD_UUID = '"+world+"';")) {
            if (resultSet.next()) {
                return new StoredWaystone(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("OWNER_UUID"),
                        resultSet.getInt("VISIBILITY"),
                        resultSet.getInt("CHUNK_X"),
                        resultSet.getInt("CHUNK_Z"),
                        resultSet.getInt("BLOCK_X"),
                        resultSet.getInt("BLOCK_Y"),
                        resultSet.getInt("BLOCK_Z"),
                        resultSet.getString("WORLD_UUID"),
                        resultSet.getInt("USES"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StoredWaystone[] getWaystones(int chunk_x, int chunk_z, UUID world) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE CHUNK_X = " + chunk_x + " AND CHUNK_Z = " + chunk_z + " AND WORLD_UUID = '"+world+"';")) {
            return getWaystonesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new StoredWaystone[0];
    }

    public StoredWaystone[] getWaystones(Player player) {
        SortMode sortMode = getSortMode(player);
        try  {
            ResultSet resultSet = null;
            switch (sortMode) {
                case ALPHABETICAL -> resultSet = executeQuery("SELECT * FROM WAYSTONES  ORDER BY NAME COLLATE NOCASE ASC;");
                case ALPHABETICAL_DESCENDING -> resultSet = executeQuery("SELECT * FROM WAYSTONES  ORDER BY NAME COLLATE NOCASE DESC;");
                case NUMERIC -> resultSet = executeQuery("SELECT * FROM WAYSTONES  ORDER BY ID ASC;");
                case NUMERIC_DESCENDING -> resultSet = executeQuery("SELECT * FROM WAYSTONES  ORDER BY ID DESC;");
                case POPULARITY -> resultSet = executeQuery("SELECT * FROM WAYSTONES  ORDER BY USES DESC;");
                case POPULARITY_ASCENDING -> resultSet = executeQuery("SELECT * FROM WAYSTONES  ORDER BY USES ASC;");
            }
            assert resultSet != null;
            StoredWaystone[] all = getWaystonesFromResultSet(resultSet);
            StoredWaystone[] favs = getFavoriteWaystones(player);
            List<StoredWaystone> result = new ArrayList<StoredWaystone>(Arrays.stream(all).toList());
            result.removeAll(Arrays.stream(favs).toList());
            result.addAll(0, Arrays.stream(favs).toList());
            return result.toArray(new StoredWaystone[0]);
//            return all;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new StoredWaystone[0];
    }

    public StoredWaystone[] getWaystones(UUID world) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE WORLD_UUID = '"+world+"';")) {
            return getWaystonesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new StoredWaystone[0];
    }

    public StoredWaystone[] getWaystones(UUID world, String searchTerm) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE WORLD_UUID = '"+world+"' AND NAME LIKE '%" + searchTerm + "%' ORDER BY NAME COLLATE NOCASE ASC;")) {
            return getWaystonesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new StoredWaystone[0];
    }

    public StoredWaystone[] getWaystones(UUID world, Player player) {
        SortMode sortMode = getSortMode(player);
        try  {
            ResultSet resultSet = null;
            switch (sortMode) {
                case ALPHABETICAL -> resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE WORLD_UUID = '"+world+"' ORDER BY NAME COLLATE NOCASE ASC;");
                case ALPHABETICAL_DESCENDING -> resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE WORLD_UUID = '"+world+"' ORDER BY NAME COLLATE NOCASE DESC;");
                case NUMERIC -> resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE WORLD_UUID = '"+world+"' ORDER BY ID ASC;");
                case NUMERIC_DESCENDING -> resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE WORLD_UUID = '"+world+"' ORDER BY ID DESC;");
                case POPULARITY -> resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE WORLD_UUID = '"+world+"' ORDER BY USES DESC;");
                case POPULARITY_ASCENDING -> resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE WORLD_UUID = '"+world+"' ORDER BY USES ASC;");
            }
            assert resultSet != null;
            StoredWaystone[] all = getWaystonesFromResultSet(resultSet);
            StoredWaystone[] favs = getFavoriteWaystones(world, player);
            List<StoredWaystone> result = new ArrayList<StoredWaystone>(Arrays.stream(all).toList());
            result.removeAll(Arrays.stream(favs).toList());
            result.addAll(0, Arrays.stream(favs).toList());
            return result.toArray(new StoredWaystone[0]);
//            return all;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new StoredWaystone[0];
    }


    public StoredWaystone[] getFavoriteWaystones(Player player) {
        SortMode sortMode = getSortMode(player);
        try  {
            ResultSet resultSet = null;
            String sql =    "SELECT WAYSTONES.ID, WAYSTONES.NAME, WAYSTONES.OWNER_UUID, WAYSTONES.VISIBILITY, WAYSTONES.CHUNK_X, WAYSTONES.CHUNK_Z, WAYSTONES.BLOCK_X, WAYSTONES.BLOCK_Y, WAYSTONES.BLOCK_Z, WAYSTONES.WORLD_UUID, WAYSTONES.USES " +
                    "FROM WAYSTONES, FAVORITES " +
                    "WHERE FAVORITES.WAYSTONE = WAYSTONES.ID " +
                    "AND PLAYER = '"+player.getUniqueId()+"' ";
            switch (sortMode) {
                case ALPHABETICAL -> resultSet = executeQuery(sql + "ORDER BY NAME COLLATE NOCASE ASC;");
                case ALPHABETICAL_DESCENDING -> resultSet = executeQuery(sql + "ORDER BY NAME COLLATE NOCASE DESC;");
                case NUMERIC -> resultSet = executeQuery(sql + "ORDER BY WAYSTONES.ID ASC;");
                case NUMERIC_DESCENDING -> resultSet = executeQuery(sql + "ORDER BY WAYSTONES.ID DESC;");
                case POPULARITY -> resultSet = executeQuery(sql + "ORDER BY USES DESC;");
                case POPULARITY_ASCENDING -> resultSet = executeQuery(sql + "ORDER BY USES ASC;");
            }
            return getWaystonesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {}
        return new StoredWaystone[0];
    }

    public StoredWaystone[] getFavoriteWaystones(UUID world, Player player) {
        SortMode sortMode = getSortMode(player);
        try  {
            ResultSet resultSet = null;
            String sql =    "SELECT WAYSTONES.ID, WAYSTONES.NAME, WAYSTONES.OWNER_UUID, WAYSTONES.VISIBILITY, WAYSTONES.CHUNK_X, WAYSTONES.CHUNK_Z, WAYSTONES.BLOCK_X, WAYSTONES.BLOCK_Y, WAYSTONES.BLOCK_Z, WAYSTONES.WORLD_UUID, WAYSTONES.USES " +
                    "FROM WAYSTONES, FAVORITES " +
                    "WHERE FAVORITES.WAYSTONE = WAYSTONES.ID " +
                    "AND WORLD_UUID = '"+world+"' " +
                    "AND PLAYER = '"+player.getUniqueId()+"' ";
            switch (sortMode) {
                case ALPHABETICAL -> resultSet = executeQuery(sql + "ORDER BY NAME COLLATE NOCASE ASC;");
                case ALPHABETICAL_DESCENDING -> resultSet = executeQuery(sql + "ORDER BY NAME COLLATE NOCASE DESC;");
                case NUMERIC -> resultSet = executeQuery(sql + "ORDER BY WAYSTONES.ID ASC;");
                case NUMERIC_DESCENDING -> resultSet = executeQuery(sql + "ORDER BY WAYSTONES.ID DESC;");
                case POPULARITY -> resultSet = executeQuery(sql + "ORDER BY USES DESC;");
                case POPULARITY_ASCENDING -> resultSet = executeQuery(sql + "ORDER BY USES ASC;");
            }
            return getWaystonesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException ignored) {}
        return new StoredWaystone[0];
    }

    public StoredWaystone[] getWaystones() {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES;")) {
            return getWaystonesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new StoredWaystone[0];

    }

    @NotNull
    private StoredWaystone[] getWaystonesFromResultSet(ResultSet resultSet) throws SQLException {
        Set<StoredWaystone> waystones = new LinkedHashSet<>();
        try {
            while (resultSet.next()) {
                waystones.add(new StoredWaystone(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("OWNER_UUID"),
                        resultSet.getInt("VISIBILITY"),
                        resultSet.getInt("CHUNK_X"),
                        resultSet.getInt("CHUNK_Z"),
                        resultSet.getInt("BLOCK_X"),
                        resultSet.getInt("BLOCK_Y"),
                        resultSet.getInt("BLOCK_Z"),
                        resultSet.getString("WORLD_UUID"),
                        resultSet.getInt("USES"))
                );
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return waystones.toArray(new StoredWaystone[0]);
    }

    /**
     * @param name the requested name
     * @return true if the name is not used by any other waystone – false if used by at least one waystone
     */
    public boolean nameFree(String name) {
        return getWaystone(name) == null;
    }




    //
    // Whitelist
    //

    public void setVisibility(int waystoneID, Visibility visibility) {

    }





    //
    // Favorites
    //
    public Integer[] getFavorites(Player player) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM FAVORITES WHERE PLAYER = '"+player.getUniqueId()+"';")) {
            Set<Integer> favorites = new LinkedHashSet<>();
            while (resultSet.next()) {
                favorites.add(resultSet.getInt("WAYSTONE"));
            }
            return favorites.toArray(new Integer[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Integer[0];
    }

    public void addFavorite(Player player, int id) {
        executeUpdate("INSERT INTO FAVORITES(PLAYER, WAYSTONE) VALUES('"+player.getUniqueId()+"', " + id + ");");
    }

    public void removeFavorite(Player player, int id) {
        executeUpdate("DELETE FROM FAVORITES WHERE PLAYER = '"+player.getUniqueId()+"' AND WAYSTONE = " + id + ";");
    }






    //
    // SortMode
    //

    public SortMode getSortMode(Player player) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM SORTMODE WHERE PLAYER = '"+player.getUniqueId()+"';")) {
            return SortMode.valueByNumber(resultSet.getInt("MODE"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return SortMode.ALPHABETICAL;
    }

    public void setSortMode(Player player, SortMode sortMode) {
        executeUpdate("REPLACE INTO SORTMODE(PLAYER, MODE) VALUES('" + player.getUniqueId() + "', " + sortMode.ordinal() + ");");
    }

    public boolean onWhitelist(Player player, int id) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WHITELISTS WHERE PLAYER = '"+player.getUniqueId()+"';")) {
            return resultSet.next();
        } catch (SQLException ignored) {}
        return false;
    }

    public void addWhitelist(Player player, int id) {
        executeUpdate("INSERT INTO WHITELISTS(PLAYER, WAYSTONE) VALUES('"+player.getUniqueId()+"', " + id + ");");
    }

    public void removeWhitelist(Player player, int id) {
        executeUpdate("DELETE FROM WHITELISTS WHERE PLAYER = '"+player.getUniqueId()+"' AND WAYSTONE = " + id + ";");
    }
}