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
        ObservableList<Mitarbeiter> mitarbeiterListe = FXCollections.observableArrayList();
        String sql = "SELECT * FROM mitarbeiter";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Mitarbeiter mitarbeiter = new Mitarbeiter(
                        rs.getInt("mitarbeiter_id"),
                        rs.getString("vorname"),
                        rs.getString("nachname"));
                mitarbeiterListe.add(mitarbeiter);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return mitarbeiterListe;
    }

    @Override
    public void mitarbeiterSpeichern(String vorname, String nachname) {
        String sql = "INSERT INTO mitarbeiter(vorname, nachname) VALUES(?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, vorname);
            pstmt.setString(2, nachname);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void mitarbeiterLöschen(Mitarbeiter mitarbeiter) {
        String sqlZuordnungen = "DELETE FROM mitarbeiter_aufgaben WHERE mitarbeiter_id = ?";
        String sqlMitarbeiter = "DELETE FROM mitarbeiter WHERE mitarbeiter_id = ?";
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
            try (PreparedStatement stmtZuordnungen = conn.prepareStatement(sqlZuordnungen)) {
                stmtZuordnungen.setInt(1, mitarbeiter.getMitarbeiterId());
                stmtZuordnungen.executeUpdate();
            }
            try (PreparedStatement stmtMitarbeiter = conn.prepareStatement(sqlMitarbeiter)) {
                stmtMitarbeiter.setInt(1, mitarbeiter.getMitarbeiterId());
                stmtMitarbeiter.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ObservableList<Aufgabe> aufgabenLaden(Mitarbeiter mitarbeiter, ObservableList<Mitarbeiter> globaleMitarbeiterListe) {
        ObservableList<Aufgabe> aufgabenListe = FXCollections.observableArrayList();
        String sqlAufgaben = "SELECT * FROM aufgaben";
        String sqlZuordnung = "SELECT mitarbeiter_id FROM mitarbeiter_aufgaben WHERE aufgabe_id = ?";
        Map<Integer, Mitarbeiter> mitarbeiterCache = new HashMap<>();
        for (Mitarbeiter m : globaleMitarbeiterListe) {
            mitarbeiterCache.put(m.getMitarbeiterId(), m);
        }
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlAufgaben)) {
            while (rs.next()) {
                Aufgabe aufgabe = new Aufgabe(
                        rs.getInt("aufgabe_id"),
                        rs.getString("titel"),
                        rs.getString("beschreibung"),
                        rs.getInt("status") == 1);
                ObservableList<Mitarbeiter> zugeordneteMitarbeiter = FXCollections.observableArrayList();
                try (PreparedStatement pstmt = conn.prepareStatement(sqlZuordnung)) {
                    pstmt.setInt(1, aufgabe.getAufgabenId());
                    ResultSet relRs = pstmt.executeQuery();
                    while (relRs.next()) {
                        int mid = relRs.getInt("mitarbeiter_id");
                        Mitarbeiter m = mitarbeiterCache.get(mid);
                        if (m != null) {
                            zugeordneteMitarbeiter.add(m);
                        }
                    }
                }
                aufgabe.setMitarbeiterListe(zugeordneteMitarbeiter);
                if (mitarbeiter == null) {
                    aufgabenListe.add(aufgabe);
                } else if (aufgabe.hatMitarbeiter(mitarbeiter)) {
                    aufgabenListe.add(aufgabe);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return aufgabenListe;
    }

    @Override
    public ObservableList<Aufgabe> alleAufgabenLaden(ObservableList<Mitarbeiter> globaleMitarbeiterListe) {
        // Gibt alle Aufgaben zurück (mitarbeiter==null)
        return aufgabenLaden(null, globaleMitarbeiterListe);
    }

    @Override
    public void aufgabeSpeichern(Mitarbeiter mitarbeiter, Aufgabe aufgabe) {
        String sqlAufgabe = "INSERT INTO aufgaben(titel, beschreibung, status) VALUES(?,?,?)";
        String sqlZuordnung = "INSERT INTO mitarbeiter_aufgaben(mitarbeiter_id, aufgabe_id, zugewiesen_am) VALUES(?,?,?)";
        int aufgabeId = -1;
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sqlAufgabe, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, aufgabe.getTitel());
            pstmt.setString(2, aufgabe.getBeschreibung());
            pstmt.setBoolean(3, aufgabe.isErledigt());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    aufgabeId = generatedKeys.getInt(1);
                    aufgabe.setAufgabenId(aufgabeId);
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
        aufgabe.fuegeMitarbeiterHinzu(mitarbeiter);
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sqlZuordnung)) {
            pstmt.setInt(1, mitarbeiter.getMitarbeiterId());
            pstmt.setInt(2, aufgabe.getAufgabenId());
            pstmt.setDate(3, Date.valueOf(java.time.LocalDate.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void aufgabeStatusAenderung(Aufgabe aufgabe) {
        String sql = "UPDATE aufgaben SET status = ? WHERE aufgabe_id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, aufgabe.isErledigt() ? 1 : 0);
            pstmt.setInt(2, aufgabe.getAufgabenId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void aufgabeLöschen(Aufgabe aufgabe) {
        aufgabenMap.remove(aufgabe.getAufgabenId());
        if (aufgabe == null) {
            System.out.println("Fehler: Die Aufgabe ist null und kann nicht gelöscht werden.");
            return;
        }
        String sqlZuordnung = "DELETE FROM mitarbeiter_aufgaben WHERE aufgabe_id = ?";
        String sqlAufgabe = "DELETE FROM aufgaben WHERE aufgabe_id = ?";
        try (Connection conn = DriverManager.getConnection(url)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
            try (PreparedStatement stmtZuordnung = conn.prepareStatement(sqlZuordnung)) {
                stmtZuordnung.setInt(1, aufgabe.getAufgabenId());
                stmtZuordnung.executeUpdate();
            }
            try (PreparedStatement stmtAufgabe = conn.prepareStatement(sqlAufgabe)) {
                stmtAufgabe.setInt(1, aufgabe.getAufgabenId());
                stmtAufgabe.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void aufgabeDatenÄndern(Aufgabe aufgabe) {
        String sql = "UPDATE aufgaben SET titel = ?, beschreibung = ? WHERE aufgabe_id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, aufgabe.getTitel());
            pstmt.setString(2, aufgabe.getBeschreibung());
            pstmt.setInt(3, aufgabe.getAufgabenId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void aufgabeEntfernen(Mitarbeiter mitarbeiter, Aufgabe aufgabe) {
        // Implementiert das Entfernen der Zuordnung eines Mitarbeiters zu einer Aufgabe
        String sql = "DELETE FROM mitarbeiter_aufgaben WHERE mitarbeiter_id = ? AND aufgabe_id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, mitarbeiter.getMitarbeiterId());
            pstmt.setInt(2, aufgabe.getAufgabenId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // Optional: Mitarbeiter aus der Aufgabenliste entfernen
        aufgabe.getMitarbeiterListe().remove(mitarbeiter);
    }

    // Die alte Methode aufgabenLaden(Mitarbeiter) kann entfernt oder leer gelassen werden
    @Deprecated
    public ObservableList<Aufgabe> aufgabenLaden(Mitarbeiter mitarbeiter) {
        return FXCollections.observableArrayList();
    }

    // ...restlicher Code...
}