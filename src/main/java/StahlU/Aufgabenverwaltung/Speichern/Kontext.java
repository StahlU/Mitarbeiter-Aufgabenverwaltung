package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Aufgabe;
import StahlU.Aufgabenverwaltung.Objekte.Mitarbeiter;
import javafx.collections.ObservableList;

public class Kontext {

    private ISpeicherStrategie speicherStrategie;

    public void setSpeicherStrategie(ISpeicherStrategie speicherStrategie) {
        this.speicherStrategie = speicherStrategie;
    }

    public ObservableList<Mitarbeiter> ladeMitarbeiter() {
        return this.speicherStrategie.mitarbeiterLaden();
    }

    public Mitarbeiter speichereMitarbeiter(String vorname, String nachname) {
        Mitarbeiter mitarbeiter = new Mitarbeiter(-1, vorname, nachname);
        this.speicherStrategie.mitarbeiterSpeichern(vorname, nachname);
        speichereJsonAsync();
        return mitarbeiter;
    }

    public void loescheMitarbeiter(Mitarbeiter mitarbeiter) {
        this.speicherStrategie.mitarbeiterLöschen(mitarbeiter);
        speichereJsonAsync();
    }

    public ObservableList<Aufgabe> ladeAufgaben(Mitarbeiter mitarbeiter, ObservableList<Mitarbeiter> globaleMitarbeiterListe) {
        return this.speicherStrategie.aufgabenLaden(mitarbeiter, globaleMitarbeiterListe);
    }

    public ObservableList<Aufgabe> ladeAlleAufgaben(ObservableList<Mitarbeiter> globaleMitarbeiterListe) {
        return this.speicherStrategie.alleAufgabenLaden(globaleMitarbeiterListe);
    }

    public void speichereAufgabe(Mitarbeiter mitarbeiter, String titel, String beschreibung) {
        Aufgabe aufgabe = new Aufgabe(titel, beschreibung);
        aufgabe.fuegeMitarbeiterHinzu(mitarbeiter);
        this.speicherStrategie.aufgabeSpeichern(mitarbeiter, aufgabe);
        speichereJsonAsync();
    }

    public void aktualisiereAufgabenStatus(Aufgabe aufgabe) {
        this.speicherStrategie.aufgabeStatusAenderung(aufgabe);
        speichereJsonAsync();
    }

    public void loescheAufgabe(Mitarbeiter mitarbeiter, Aufgabe aufgabe) {
        this.speicherStrategie.aufgabeEntfernen(mitarbeiter, aufgabe);
        if (aufgabe.getMitarbeiterListe().isEmpty()) {
            this.speicherStrategie.aufgabeLöschen(aufgabe);
        }
        speichereJsonAsync();
    }

    public void speichereJson() {
        ObservableList<Mitarbeiter> mitarbeiterListe = ladeMitarbeiter();
        ObservableList<Aufgabe> aufgabenListe = ladeAlleAufgaben(mitarbeiterListe);
        JsonBackup.save(aufgabenListe);
    }

    public void speichereJsonAsync() {
        new Thread(this::speichereJson).start();
    }

    public void aktualisiereAufgabeDaten(Aufgabe aufgabe) {
        this.speicherStrategie.aufgabeDatenÄndern(aufgabe);
        speichereJsonAsync();
    }
}
