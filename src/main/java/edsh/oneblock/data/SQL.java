package edsh.oneblock.data;

public class SQL {
    static final String[] INIT = {
"""
CREATE TABLE IF NOT EXISTS island(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    x INT NOT NULL,
    y INT NOT NULL,
    z INT NOT NULL,
    lvl INT NOT NULL DEFAULT 1,
    xp INT NOT NULL DEFAULT 0
);
""",
"""
CREATE TABLE IF NOT EXISTS player(
    uuid TEXT PRIMARY KEY,
    name TEXT,
    island INT REFERENCES island(id) ON DELETE SET NULL,
    is_owner INT
);
""",
"""
CREATE TABLE IF NOT EXISTS account(
    id TEXT PRIMARY KEY,
    uuid TEXT NOT NULL
);
"""
    };

    static final String GET_UUID =
            "SELECT uuid FROM account WHERE id=?;";

    static final String SET_UUID =
            "INSERT INTO account(id, uuid) VALUES (?, ?);";

    static final String LAST_ISLAND =
            "SELECT seq FROM sqlite_sequence WHERE name='island';";

    static final String GET_ISLAND =
            "SELECT * FROM island WHERE id=?;";

    static final String SET_ISLAND =
            "INSERT OR REPLACE INTO island(id, x, y, z, lvl, xp) values (?, ?, ?, ?, ?, ?);";

    static final String GET_PLAYER =
            "SELECT * FROM player WHERE uuid=?;";

    static final String SET_PLAYER =
            "INSERT OR REPLACE INTO player(uuid, name, island, is_owner) values (?, ?, ?, ?);";

    static final String GET_ALL_PLAYERS =
            "SELECT uuid FROM player WHERE island=?;";
}
