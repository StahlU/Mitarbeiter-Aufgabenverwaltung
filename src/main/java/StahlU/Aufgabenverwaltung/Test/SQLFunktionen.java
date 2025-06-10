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
                        "mitarbeiter_ID INTEGER PRIMARY KEY," +
                        "name TEXT NOT NULL," +
                        "surname TEXT NOT NULL);",

                "CREATE TABLE auftraege (" +
                        "auftrag_ID INTEGER PRIMARY KEY," +
                        "title TEXT NOT NULL," +
                        "description TEXT," +
                        "status INTEGER);",

                "CREATE TABLE mitarbeiter_auftraege (" +
                        "mitarbeiter_id INTEGER NOT NULL," +
                        "auftrag_id INTEGER NOT NULL," +
                        "assigned_date DATE," +
                        "PRIMARY KEY (mitarbeiter_id, auftrag_id)," +
                        "FOREIGN KEY (mitarbeiter_id) REFERENCES mitarbeiter(mitarbeiter_ID) ON DELETE CASCADE," +
                        "FOREIGN KEY (auftrag_id) REFERENCES auftraege(auftrag_ID) ON DELETE CASCADE);"
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
    public static void insertData(String name, String surname) {
        String sql = "INSERT INTO Mitarbeiter(name,surname) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(url);
             var pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, surname);
            pstmt.executeUpdate();

            System.out.println("Daten erfolgreich eingefügt!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        treiberTest();
        createTable();
//        insertData("Herbert", "Müller");


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

