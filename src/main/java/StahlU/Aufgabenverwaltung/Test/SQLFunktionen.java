package StahlU.Aufgabenverwaltung.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLFunktionen {
    private static String url = "jdbc:sqlite:SQL.db";

    public static void treiberTest(){
        try {
            Class.forName("org.sqlite.JDBC");
            System.out.println("SQLite JDBC-Treiber erfolgreich geladen!");
        } catch (ClassNotFoundException e) {
            System.out.println("Fehler: SQLite JDBC-Treiber nicht gefunden.");
            e.printStackTrace();
        }
    }
    public static void createTable(){
        String[] sqlStatements = {
                "CREATE TABLE IF NOT EXISTS mitarbeiter (" +
                        "mitarbeiter_id INTEGER PRIMARY KEY," +
                        "vorname TEXT NOT NULL," +
                        "nachname TEXT NOT NULL);",

                "CREATE TABLE IF NOT EXISTS aufgaben (" +
                        "aufgabe_id INTEGER PRIMARY KEY," +
                        "titel TEXT NOT NULL," +
                        "beschreibung TEXT," +
                        "status INTEGER);",

                "CREATE TABLE IF NOT EXISTS mitarbeiter_aufgaben (" +
                        "mitarbeiter_id INTEGER NOT NULL," +
                        "aufgabe_id INTEGER NOT NULL," +
                        "zugewiesen_am DATE," +
                        "PRIMARY KEY (mitarbeiter_id, aufgabe_id)," +
                        "FOREIGN KEY (mitarbeiter_id) REFERENCES mitarbeiter(mitarbeiter_id) ON DELETE CASCADE," +
                        "FOREIGN KEY (aufgabe_id) REFERENCES aufgaben(aufgabe_id) ON DELETE CASCADE);"
        };

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
            for (String sql : sqlStatements) {
                stmt.execute(sql);
                System.out.println("Tabelle erfolgreich erstellt: " + sql);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void insertData(String vorname, String nachname) {
        String sql = "INSERT INTO mitarbeiter(vorname,nachname) VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vorname);
            pstmt.setString(2, nachname);
            pstmt.executeUpdate();
            System.out.println("Daten erfolgreich eingefügt!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        treiberTest();
        createTable();
    }

    public static void createTablesIfNotExists() {
        File dbFile = new File("SQL.db");
        if (!dbFile.exists()) {
            System.out.println("SQL.db Datei existiert nicht. Erstelle Tabellen...");
            createTable();
        } else {
            System.out.println("SQL.db Datei existiert bereits. Tabellen werden nicht erstellt.");
        }
    }
}
