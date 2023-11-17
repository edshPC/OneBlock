package edsh.oneblock.data;

import edsh.oneblock.OneBlockPlugin;

import java.io.File;
import java.sql.*;

public class Database {

    private final Connection conn;

    public Database (String file) {
        File dbFolder = new File(OneBlockPlugin.INSTANCE.getDataFolder(), "db\\");
        dbFolder.mkdir();
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + dbFolder.getPath() + '\\' + file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
