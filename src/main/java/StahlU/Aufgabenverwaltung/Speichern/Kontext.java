package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Aufgabe;
import StahlU.Aufgabenverwaltung.Objekte.Mitarbeiter;
import javafx.collections.ObservableList;

public class Kontext {

    private ISpeicherStrategie speicherStrategie;
    public void setSpeicherStrategie(ISpeicherStrategie speicherStrategie){
        this.speicherStrategie = speicherStrategie;
    }


    public ObservableList<Mitarbeiter> mitarbeiterLadenAusführen() {
        ObservableList<Mitarbeiter> mitarbeiterList = this.speicherStrategie.mitarbeiterLaden();

        for (Mitarbeiter mitarbeiter : mitarbeiterList) {
            ObservableList<Aufgabe> aufgaben = this.speicherStrategie.aufgabenLaden(mitarbeiter);
            for (Aufgabe aufgabe : aufgaben) {
                mitarbeiter.addAufgabe(aufgabe);
            }
        }

        return mitarbeiterList;
    }


    public Mitarbeiter mitarbeiterSpeichernAusführen(String name, String surname) {
        Mitarbeiter mitarbeiter = new Mitarbeiter(-1,name, surname);
        this.speicherStrategie.mitarbeiterSpeichern(name,surname);
        return mitarbeiter;
    }

    public void mitarbeiterLöschenAusführen(Mitarbeiter mitarbeiter) {
        this.speicherStrategie.mitarbeiterLöschen(mitarbeiter);

    }


    public ObservableList<Aufgabe> aufgabenLadenAusführen(Mitarbeiter mitarbeiter) {
        mitarbeiter.getAufgaben().clear();
        ObservableList<Aufgabe> liste = this.speicherStrategie.aufgabenLaden(mitarbeiter);
        for (Aufgabe aufgabe : liste) {
            mitarbeiter.addAufgabe(aufgabe);
        }
        return liste;
    }


    public void aufgabeSpeichernAusführen(Mitarbeiter mitarbeiter, String titleText, String titledescription) {
        Aufgabe aufgabe = new Aufgabe(titleText,titledescription);
        mitarbeiter.addAufgabe(aufgabe);

        this.speicherStrategie.aufgabeSpeichern(mitarbeiter, aufgabe);

    }
    public void aufgabeStatusAenderungAusführen(Aufgabe aufgabe) {
        this.speicherStrategie.aufgabeStatusAenderung(aufgabe);

    }


    public void aufgabeLöschenAusführen(Mitarbeiter selectedMitarbeiter, Aufgabe aufgabe) {
        selectedMitarbeiter.removeAufgabe(aufgabe);
        this.speicherStrategie.aufgabeLöschen(aufgabe);

    }


    public void saveToJson() {
        ObservableList<Mitarbeiter> mitarbeiterList = mitarbeiterLadenAusführen();
        JsonBackup.save(mitarbeiterList);

    }
}
