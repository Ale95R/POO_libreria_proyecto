import java.sql.*;

public final class DB {

    private static final String URL = "jdbc:sqlite:biblioteca.db";
    private static Connection conn;

    /** Conexión singleton reutilizable. */
    public static Connection get() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(URL);
            inicializarTablas();
        }
        return conn;
    }

    /** Crea el esquema solo la primera vez. */
    private static void inicializarTablas() throws SQLException {
        String ddl = """
            PRAGMA foreign_keys = ON;
            CREATE TABLE IF NOT EXISTS material (
                codigo   TEXT PRIMARY KEY,
                titulo   TEXT NOT NULL,
                autor    TEXT,
                tipo     TEXT CHECK (tipo IN ('Libro','Revista','CD','DVD')),
                idioma   TEXT
            );
            CREATE TABLE IF NOT EXISTS prestamo (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                codigo      TEXT NOT NULL REFERENCES material(codigo),
                usuario     TEXT NOT NULL,
                fechaInicio TEXT NOT NULL,
                fechaLimite TEXT NOT NULL,
                devuelto    INTEGER DEFAULT 0
            );
            CREATE TABLE IF NOT EXISTS mora (
                usuario TEXT PRIMARY KEY,
                deuda   REAL  DEFAULT 0
            );
            """;
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(ddl);
        }
    }

    private DB() {}   // util‑class
}
