package StahlU.Aufgabenverwaltung.Objekte;

import StahlU.Aufgabenverwaltung.Speichern.Kontext;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class Mitarbeiter {
    private  int employeeID;
    private String name;
    private  String surname;



    private ObservableList<Aufgabe> aufgaben;
    private transient SimpleStringProperty nameProperty;
    private transient SimpleDoubleProperty fortschrittProperty;

    public Mitarbeiter(int id,String name,String surname) {
        this.employeeID = id;
        this.name = name;
        this.surname = surname;

        this.aufgaben = FXCollections.observableArrayList();
        this.nameProperty = new SimpleStringProperty(name);
        this.fortschrittProperty = new SimpleDoubleProperty(0.0);

    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getName() {

        return name;
    }
    public String getSurname() {
        return surname;
    }

    public SimpleStringProperty nameProperty() {
        if (nameProperty == null) {
            nameProperty = new SimpleStringProperty(name);
        }
        return nameProperty;
    }

    public ObservableList<Aufgabe> getAufgaben() {
        return aufgaben;

    }

    public double getFortschritt() {
        if (fortschrittProperty == null) {
            fortschrittProperty = new SimpleDoubleProperty(0.0);
        }
        return fortschrittProperty.get();
    }

    public SimpleDoubleProperty fortschrittProperty() {
        if (fortschrittProperty == null) {
            fortschrittProperty = new SimpleDoubleProperty(0.0);
        }
        return fortschrittProperty;
    }

    public void addAufgabe(Aufgabe aufgabe) {
        aufgaben.add(aufgabe);
        aufgabe.statusProperty().addListener((obs, oldVal, newVal) -> {
            updateFortschritt();
        });
        updateFortschritt();
    }

    public void removeAufgabe(Aufgabe aufgabe) {
        aufgaben.remove(aufgabe);
        updateFortschritt();
    }

    public void aufgabeErledigt(Aufgabe aufgabe, boolean erledigt, Kontext kontextDaten) {
        aufgabe.setStatus(erledigt, kontextDaten);
        updateFortschritt();
    }



    private void updateFortschritt() {
        if (aufgaben.isEmpty()) {
            if (fortschrittProperty != null) {
                fortschrittProperty.set(0);
            }
            return;
        }

        long erledigteAufgaben = aufgaben.stream()
                .filter(Aufgabe::getStatus)
                .count();

        double fortschritt = (double) erledigteAufgaben / aufgaben.size();

        if (fortschrittProperty != null) {
            fortschrittProperty.set(fortschritt);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Mitarbeiter{");
        sb.append("employeeID=").append(employeeID);
        sb.append(", name='").append(name).append('\'');
        sb.append(", surname='").append(surname).append('\'');
        sb.append(", aufgaben=").append(aufgaben);
        sb.append(", nameProperty=").append(nameProperty);
        sb.append(", fortschrittProperty=").append(fortschrittProperty);
        sb.append('}');
        return sb.toString();
    }


}