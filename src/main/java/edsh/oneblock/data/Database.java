package edsh.oneblock.data;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import edsh.oneblock.OneBlockPlugin;
import edsh.oneblock.island.Island;
import edsh.oneblock.util.Util;

import java.io.File;
import java.sql.*;
import java.util.LinkedList;
import java.util.UUID;

public class Database {

    private final Connection conn;

    public Database(String file) {
        File dbFolder = new File(OneBlockPlugin.INSTANCE.getDataFolder(), "db\\");
        dbFolder.mkdirs();
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFolder.getPath() + '\\' + file);

            for (String st : SQL.INIT)
                conn.prepareStatement(st).execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getPlayerUUID(Player pl) {
        String id = Util.getId(pl);
        String uuid;
        try {
            var st = conn.prepareStatement(SQL.GET_UUID);
            st.setString(1, id);
            var res = st.executeQuery();

            if (res.next()) {
                uuid = res.getString(1);
            } else {
                uuid = pl.getLoginChainData().getClientUUID().toString();
                st = conn.prepareStatement(SQL.SET_UUID);
                st.setString(1, id);
                st.setString(2, uuid);
                st.execute();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return uuid;
    }

    public long getLastIslandId() {
        try {
            var st = conn.prepareStatement(SQL.LAST_ISLAND);
            var res = st.executeQuery();
            if (res.next()) return res.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public Island getIsland(long id) {
        try {
            var st = conn.prepareStatement(SQL.GET_ISLAND);
            st.setLong(1, id);
            var res = st.executeQuery();
            if (res.next()) {
                Position pos = new Position(
                        res.getLong("x"),
                        res.getLong("y"),
                        res.getLong("z")
                );
                LinkedList<UUID> allPlayers = new LinkedList<>();
                st = conn.prepareStatement(SQL.GET_ALL_PLAYERS);
                st.setLong(1, id);
                var plres = st.executeQuery();
                while (plres.next()) {
                    String uuid = plres.getString("uuid");
                    allPlayers.add(UUID.fromString(uuid));
                }
                return new Island(
                        pos,
                        id,
                        res.getInt("lvl"),
                        res.getLong("xp"),
                        allPlayers
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void saveIsland(Island island) {
        try {
            var st = conn.prepareStatement(SQL.SET_ISLAND);
            int i=1;
            st.setLong(i++, island.getId());
            Position pos = island.getPosition();
            st.setLong(i++, (long) pos.x);
            st.setLong(i++, (long) pos.y);
            st.setLong(i++, (long) pos.z);
            st.setInt(i++, island.getLvl());
            st.setLong(i++, island.getXp());

            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        try {
            var st = conn.prepareStatement(SQL.GET_PLAYER);
            st.setString(1, uuid.toString());
            var res = st.executeQuery();
            if (res.next()) {
                var data = new PlayerData();
                data.uuid = uuid;
                data.name = res.getString("name");
                data.island_id = res.getLong("island");
                data.is_owner = res.getBoolean("is_owner");
                return data;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void savePlayerData(PlayerData playerData) {
        try {
            var st = conn.prepareStatement(SQL.SET_PLAYER);
            int i=1;
            st.setString(i++, playerData.uuid.toString());
            st.setString(i++, playerData.name);
            st.setLong(i++, playerData.island_id);
            st.setBoolean(i++, playerData.is_owner);
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
