package StahlU.Aufgabenverwaltung.Objekte;

import StahlU.Aufgabenverwaltung.Speichern.Kontext;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;


public class Aufgabe {

    private int ID;
    private String title;
    private String description;
    private boolean status;

    private transient SimpleStringProperty titleProperty;
    private transient SimpleStringProperty beschreibungProperty;
    private transient SimpleBooleanProperty erledigtProperty;




    public Aufgabe(String title, String description) {
        this.ID = -1;
        this.title = title;
        this.description = description;
        this.status = false;

        this.titleProperty = new SimpleStringProperty(title);
        this.beschreibungProperty = new SimpleStringProperty(description);
        this.erledigtProperty = new SimpleBooleanProperty(false);
    }

    public Aufgabe(int aufgabeID, String title, String description, boolean status) {
        this.ID = aufgabeID;
        this.title = title;
        this.description = description;
        this.status = status;

        this.titleProperty = new SimpleStringProperty(title);
        this.beschreibungProperty = new SimpleStringProperty(description);
        this.erledigtProperty = new SimpleBooleanProperty(status);
    }



    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }


    public String getTitle() {
       return title;

    }

    public String getDescription() {
        return description;
    }

    public boolean getStatus() {
        return status;
    }

    public boolean setStatus(boolean status) {
        this.status = status;
        if (erledigtProperty != null) {
            erledigtProperty.set(status);
            return true;
        }
        return false;
    }

    public void setStatus(boolean status , Kontext kontextDaten) {
        this.status = status;
        if (erledigtProperty != null) {
            erledigtProperty.set(status);
            kontextDaten.aufgabeStatusAenderungAusführen(this);
        }
    }

    public SimpleBooleanProperty statusProperty() {
        if (erledigtProperty == null) {
            erledigtProperty = new SimpleBooleanProperty(status);
        }
        return erledigtProperty;
    }

    public void setBeschreibung(String beschreibung) {
        if (beschreibung != null){
            this.description = beschreibung;
            beschreibungProperty.set(beschreibung);
        }
    }


    public void setTitle(String text) {
        if (text != null){
            this.title = text;
            this.titleProperty.set(text);
        }

    }


}

