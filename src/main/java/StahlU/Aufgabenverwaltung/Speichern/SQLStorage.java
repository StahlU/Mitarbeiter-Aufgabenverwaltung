package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Aufgabe;
import StahlU.Aufgabenverwaltung.Objekte.Mitarbeiter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLStorage implements ISpeicherStrategie {


private static String url = "jdbc:sqlite:SQL.db";
private Map<Integer, Aufgabe> aufgabenMap = new HashMap<>();


@Override
public ObservableList<Mitarbeiter> mitarbeiterLaden() {
    ObservableList<Mitarbeiter> mitarbeiterList = FXCollections.observableArrayList();
    String mitarbeiterSql = "SELECT * FROM mitarbeiter";
    String aufgabenSql = "SELECT * FROM auftraege";

    try (Connection conn = DriverManager.getConnection(url)) {

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(aufgabenSql)) {

            while (rs.next()) {
                Aufgabe aufgabe = new Aufgabe(
                        rs.getInt("auftrag_ID"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("status") == 1);
                aufgabenMap.put(aufgabe.getID(), aufgabe);
            }
        }

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(mitarbeiterSql)) {

            while (rs.next()) {
                Mitarbeiter mitarbeiter = new Mitarbeiter(
                        rs.getInt("mitarbeiter_ID"),
                        rs.getString("name"),
                        rs.getString("surname"));

                String mitarbeiterAufgabenSql = "SELECT auftrag_id FROM mitarbeiter_auftraege WHERE mitarbeiter_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(mitarbeiterAufgabenSql)) {
                    pstmt.setInt(1, mitarbeiter.getEmployeeID());
                    ResultSet aufgabenRs = pstmt.executeQuery();

                    while (aufgabenRs.next()) {
                        int auftragId = aufgabenRs.getInt("auftrag_id");
                        Aufgabe aufgabe = aufgabenMap.get(auftragId);
                        if (aufgabe != null) {
                            mitarbeiter.addAufgabe(aufgabe);
                        }
                    }
                }

                mitarbeiterList.add(mitarbeiter);
            }
        }

    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }

    return mitarbeiterList;
}

@Override
public void mitarbeiterSpeichern(String name, String surname) {

    String sql = "INSERT INTO mitarbeiter(name,surname) VALUES(?,?)";

    try (Connection conn = DriverManager.getConnection(url);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, name);
        pstmt.setString(2, surname);
        pstmt.executeUpdate();



    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}


@Override
public void mitarbeiterLöschen(Mitarbeiter mitarbeiter) {

    String deleteRelationsSql = "DELETE FROM mitarbeiter_auftraege WHERE mitarbeiter_id = ?";
    String deleteEmployeeSql = "DELETE FROM mitarbeiter WHERE mitarbeiter_ID = ?";

    try (Connection conn = DriverManager.getConnection(url)) {
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");


        try (PreparedStatement deleteRelationsStmt = conn.prepareStatement(deleteRelationsSql)) {
            deleteRelationsStmt.setInt(1, mitarbeiter.getEmployeeID());
            deleteRelationsStmt.executeUpdate();
        }


        try (PreparedStatement deleteEmployeeStmt = conn.prepareStatement(deleteEmployeeSql)) {
            deleteEmployeeStmt.setInt(1, mitarbeiter.getEmployeeID());
            deleteEmployeeStmt.executeUpdate();
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

@Override
public void aufgabeSpeichern(Mitarbeiter mitarbeiter, Aufgabe aufgabe) {
    String addSql = "INSERT INTO auftraege(title,description,status) VALUES(?,?,?)";
    String addBetweenSql = "INSERT INTO mitarbeiter_auftraege(mitarbeiter_id,auftrag_id,assigned_date) VALUES(?,?,?)";

    int aufgabeId = -1;

    try (Connection conn = DriverManager.getConnection(url);
         PreparedStatement pstmt = conn.prepareStatement(addSql, Statement.RETURN_GENERATED_KEYS)) {

        pstmt.setString(1, aufgabe.getTitle());
        pstmt.setString(2, aufgabe.getDescription());
        pstmt.setBoolean(3, aufgabe.getStatus());
        pstmt.executeUpdate();

        try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                aufgabeId = generatedKeys.getInt(1);
                aufgabe.setID(aufgabeId);
                aufgabenMap.put(aufgabeId, aufgabe);
            }
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }

    if (aufgabeId == -1) {
        System.out.println("Fehler beim Einfügen der Aufgabe, ID konnte nicht ermittelt werden.");
        return;
    }

    try (Connection conn = DriverManager.getConnection(url);
         PreparedStatement pstmt = conn.prepareStatement(addBetweenSql)) {

        pstmt.setInt(1, mitarbeiter.getEmployeeID());
        pstmt.setInt(2, aufgabe.getID());
        pstmt.setDate(3, Date.valueOf(java.time.LocalDate.now()));
        pstmt.executeUpdate();

    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

@Override
public void aufgabeStatusAenderung(Aufgabe aufgabe) {

    String sql = "UPDATE auftraege SET status = ? WHERE Auftrag_ID = ?";

    try (Connection conn = DriverManager.getConnection(url);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, aufgabe.getStatus() ? 1 : 0);
        pstmt.setInt(2, aufgabe.getID());
        pstmt.executeUpdate();


    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

    @Override
    public ObservableList<Aufgabe> aufgabenLaden(Mitarbeiter mitarbeiter) {
        ObservableList<Aufgabe> aufgabenList = FXCollections.observableArrayList();
        String sql = "SELECT auftrag_id FROM mitarbeiter_auftraege WHERE mitarbeiter_id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, mitarbeiter.getEmployeeID());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int auftragId = rs.getInt("auftrag_id");
                Aufgabe aufgabe = aufgabenMap.get(auftragId);
                if (aufgabe != null) {
                    aufgabenList.add(aufgabe);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return aufgabenList;
    }

@Override
public void aufgabeLöschen(Aufgabe aufgabe) {
    aufgabenMap.remove(aufgabe.getID());

    if (aufgabe == null) {
        System.out.println("Fehler: Die Aufgabe ist null und kann nicht gelöscht werden.");
        return;
    }

    String checkSql = "SELECT COUNT(*) AS count FROM mitarbeiter_auftraege WHERE auftrag_id = ?";
    String deleteRelationSql = "DELETE FROM mitarbeiter_auftraege WHERE auftrag_id = ?";
    String deleteTaskSql = "DELETE FROM auftraege WHERE Auftrag_ID = ?";

    try (Connection conn = DriverManager.getConnection(url)) {
        conn.createStatement().execute("PRAGMA foreign_keys = ON;");

        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, aufgabe.getID());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt("count") > 1) {
                try (PreparedStatement deleteRelationStmt = conn.prepareStatement(deleteRelationSql)) {
                    deleteRelationStmt.setInt(1, aufgabe.getID());
                    deleteRelationStmt.executeUpdate();
                }
                return;
            }
        }

        try (PreparedStatement deleteTaskStmt = conn.prepareStatement(deleteTaskSql)) {
            deleteTaskStmt.setInt(1, aufgabe.getID());
            deleteTaskStmt.executeUpdate();
        }
    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}

@Override
public void aufgabeDatenÄndern(Aufgabe aufgabe) {
    String sql = "UPDATE auftraege SET title = ?, description = ? WHERE Auftrag_ID = ?";

    try (Connection conn = DriverManager.getConnection(url);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, aufgabe.getTitle());
        pstmt.setString(2, aufgabe.getDescription());
        pstmt.setInt(3, aufgabe.getID());
        pstmt.executeUpdate();

    } catch (SQLException e) {
        System.out.println(e.getMessage());
    }
}


}



