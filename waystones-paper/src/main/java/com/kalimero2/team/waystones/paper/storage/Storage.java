package com.kalimero2.team.waystones.paper.storage;

import com.kalimero2.team.waystones.paper.PaperWayStones;
import com.kalimero2.team.waystones.paper.util.SortMode;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

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
    }

    private void createWaystonesTableIfNotExists() {
        // ID, NAME, Owner(UUID), CHUNK_X, CHUNK_Z , X, Y, Z, WORLD(UUID)

        executeUpdate("CREATE TABLE IF NOT EXISTS WAYSTONES(" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NAME TEXT NOT NULL," +
                "OWNER_UUID VARCHAR(36) NOT NULL," +
                "CHUNK_X INTEGER NOT NULL," +
                "CHUNK_Z INTEGER NOT NULL," +
                "BLOCK_X INTEGER NOT NULL," +
                "BLOCK_Y INTEGER NOT NULL," +
                "BLOCK_Z INTEGER NOT NULL," +
                "WORLD_UUID VARCHAR(36) NOT NULL," +
                "USES INTEGER NOT NULL" +
                ");");

    }


    public void addWaystone(String name, UUID owner, int chunkX, int chunkZ, int x, int y, int z, UUID world) {
        executeUpdate("INSERT INTO WAYSTONES(NAME, OWNER_UUID, CHUNK_X, CHUNK_Z, BLOCK_X, BLOCK_Y, BLOCK_Z, WORLD_UUID, USES) VALUES('" + name + "', '" + owner + "', " + chunkX + ", " + chunkZ + ", " + x + ", " + y + ", " + z + ", '" + world + "', 0);");
    }

    public void updateWaystone(StoredWaystone waystone) {
        executeUpdate("UPDATE WAYSTONES SET NAME = '" + waystone.name() + "', OWNER_UUID = '" + waystone.owner() + "', CHUNK_X = " + waystone.chunk_x() + ", CHUNK_Z = " + waystone.chunk_z() + ", BLOCK_X = " + waystone.block_x() + ", BLOCK_Y = " + waystone.block_y() + ", BLOCK_Z = " + waystone.block_z() + ", WORLD_UUID = '" + waystone.world() + ", USES = '" + waystone.uses() + "' WHERE ID = " + waystone.id() + ";");
    }


    public StoredWaystone getWaystone(int id) {
        try (ResultSet resultSet = executeQuery("SELECT * FROM WAYSTONES WHERE ID = " + id + ";")) {
            if (resultSet.next()) {
                return new StoredWaystone(resultSet.getInt("ID"),
                        resultSet.getString("NAME"),
                        resultSet.getString("OWNER_UUID"),
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

    public StoredWaystone[] getWaystones(UUID world, SortMode sortMode) {
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
            return getWaystonesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        while (resultSet.next()) {
            waystones.add(new StoredWaystone(resultSet.getInt("ID"),
                    resultSet.getString("NAME"),
                    resultSet.getString("OWNER_UUID"),
                    resultSet.getInt("CHUNK_X"),
                    resultSet.getInt("CHUNK_Z"),
                    resultSet.getInt("BLOCK_X"),
                    resultSet.getInt("BLOCK_Y"),
                    resultSet.getInt("BLOCK_Z"),
                    resultSet.getString("WORLD_UUID"),
                    resultSet.getInt("USES"))
            );
        }
        return waystones.toArray(new StoredWaystone[0]);
    }

}