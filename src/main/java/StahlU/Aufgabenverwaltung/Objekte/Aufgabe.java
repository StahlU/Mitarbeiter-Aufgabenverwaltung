package StahlU.Aufgabenverwaltung.Objekte;

import StahlU.Aufgabenverwaltung.Speichern.Kontext;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Aufgabe {

    private int aufgabenId;
    private String titel;
    private String beschreibung;
    private boolean status;

    private transient SimpleStringProperty titelEigenschaft;
    private transient SimpleStringProperty beschreibungEigenschaft;
    private transient SimpleBooleanProperty erledigtEigenschaft;

    private ObservableList<Mitarbeiter> mitarbeiterListe = FXCollections.observableArrayList();

    public Aufgabe(String titel, String beschreibung) {
        this.aufgabenId = -1;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.status = false;
        this.titelEigenschaft = new SimpleStringProperty(titel);
        this.beschreibungEigenschaft = new SimpleStringProperty(beschreibung);
        this.erledigtEigenschaft = new SimpleBooleanProperty(false);
    }

    public Aufgabe(int aufgabenId, String titel, String beschreibung, boolean status) {
        this.aufgabenId = aufgabenId;
        this.titel = titel;
        this.beschreibung = beschreibung;
        this.status = status;
        this.titelEigenschaft = new SimpleStringProperty(titel);
        this.beschreibungEigenschaft = new SimpleStringProperty(beschreibung);
        this.erledigtEigenschaft = new SimpleBooleanProperty(status);
    }

    public int getAufgabenId() {
        return aufgabenId;
    }

    public void setAufgabenId(int aufgabenId) {
        this.aufgabenId = aufgabenId;
    }

    public String getTitel() {
        return titel;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public boolean isErledigt() {
        return status;
    }


    public void setStatus(boolean status, Kontext kontext) {
        this.status = status;
        if (erledigtEigenschaft != null) {
            erledigtEigenschaft.set(status);
            kontext.aktualisiereAufgabenStatus(this);
        }
    }
    public void setStatus(boolean status) {
        this.status = status;
        if (erledigtEigenschaft != null) {
            erledigtEigenschaft.set(status);
        }
    }


    public SimpleBooleanProperty statusEigenschaft() {
        if (erledigtEigenschaft == null) {
            erledigtEigenschaft = new SimpleBooleanProperty(status);
        }
        return erledigtEigenschaft;
    }

    public void setBeschreibung(String beschreibung) {
        if (beschreibung != null) {
            this.beschreibung = beschreibung;
            beschreibungEigenschaft.set(beschreibung);
        }
    }

    public void setTitel(String titel) {
        if (titel != null) {
            this.titel = titel;
            this.titelEigenschaft.set(titel);
        }
    }

    public ObservableList<Mitarbeiter> getMitarbeiterListe() {
        return mitarbeiterListe;
    }

    public void setMitarbeiterListe(ObservableList<Mitarbeiter> liste) {
        this.mitarbeiterListe = liste;
    }

    public void fuegeMitarbeiterHinzu(Mitarbeiter mitarbeiter) {
        if (!mitarbeiterListe.contains(mitarbeiter)) {
            mitarbeiterListe.add(mitarbeiter);
        }
    }

    public void entferneMitarbeiter(Mitarbeiter mitarbeiter) {
        mitarbeiterListe.remove(mitarbeiter);
    }

    public boolean hatMitarbeiter(Mitarbeiter mitarbeiter) {
        for (Mitarbeiter m : mitarbeiterListe) {
            if (m.getMitarbeiterId() == mitarbeiter.getMitarbeiterId()) {
                return true;
            }
        }
        return false;
    }



}
