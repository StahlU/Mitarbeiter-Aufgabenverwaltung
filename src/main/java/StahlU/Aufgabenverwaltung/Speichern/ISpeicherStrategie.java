package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Aufgabe;
import StahlU.Aufgabenverwaltung.Objekte.Mitarbeiter;
import javafx.collections.ObservableList;

public interface ISpeicherStrategie {
    public abstract ObservableList<Mitarbeiter> mitarbeiterLaden();
    public abstract void                        mitarbeiterSpeichern(String name,String Surname);
    public abstract void                        mitarbeiterLöschen(Mitarbeiter mitarbeiter);


    public abstract ObservableList<Aufgabe>     aufgabenLaden(Mitarbeiter mitarbeiter);
    public abstract void                        aufgabeSpeichern(Mitarbeiter mitarbeiter, Aufgabe aufgabe);

    public abstract void                        aufgabeStatusAenderung(Aufgabe aufgabe);

    public abstract void                        aufgabeLöschen(Aufgabe aufgabe);

    public abstract void                        aufgabeDatenÄndern(Aufgabe aufgabe);


}
