package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Aufgabe;
import StahlU.Aufgabenverwaltung.Objekte.Mitarbeiter;
import javafx.collections.ObservableList;

public interface ISpeicherStrategie {
    ObservableList<Mitarbeiter> mitarbeiterLaden();
    void mitarbeiterSpeichern(String vorname, String nachname);
    void mitarbeiterLöschen(Mitarbeiter mitarbeiter);

    ObservableList<Aufgabe> aufgabenLaden(Mitarbeiter mitarbeiter, ObservableList<Mitarbeiter> globaleMitarbeiterListe);

    ObservableList<Aufgabe> alleAufgabenLaden(ObservableList<Mitarbeiter> globaleMitarbeiterListe);

    void aufgabeSpeichern(Mitarbeiter mitarbeiter, Aufgabe aufgabe);
    void aufgabeStatusAenderung(Aufgabe aufgabe);
    void aufgabeLöschen(Aufgabe aufgabe);
    void aufgabeDatenÄndern(Aufgabe aufgabe);

    void aufgabeEntfernen(Mitarbeiter mitarbeiter, Aufgabe aufgabe);
}
