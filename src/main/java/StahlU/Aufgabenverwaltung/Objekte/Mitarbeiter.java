package StahlU.Aufgabenverwaltung.Objekte;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Mitarbeiter {
    private int mitarbeiterId;
    private String vorname;
    private String nachname;

    private transient SimpleStringProperty vornameEigenschaft;
    private transient SimpleDoubleProperty fortschrittEigenschaft;

    public Mitarbeiter(int id, String vorname, String nachname) {
        this.mitarbeiterId = id;
        this.vorname = vorname;
        this.nachname = nachname;
        this.vornameEigenschaft = new SimpleStringProperty(vorname);
        this.fortschrittEigenschaft = new SimpleDoubleProperty(0.0);
    }

    public int getMitarbeiterId() {
        return mitarbeiterId;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public SimpleStringProperty vornameEigenschaft() {
        if (vornameEigenschaft == null) {
            vornameEigenschaft = new SimpleStringProperty(vorname);
        }
        return vornameEigenschaft;
    }

    public double getFortschritt() {
        if (fortschrittEigenschaft == null) {
            fortschrittEigenschaft = new SimpleDoubleProperty(0.0);
        }
        return fortschrittEigenschaft.get();
    }

    public SimpleDoubleProperty fortschrittEigenschaft() {
        if (fortschrittEigenschaft == null) {
            fortschrittEigenschaft = new SimpleDoubleProperty(0.0);
        }
        return fortschrittEigenschaft;
    }

    public void setFortschritt(double fortschritt) {
        if (fortschrittEigenschaft == null) {
            fortschrittEigenschaft = new SimpleDoubleProperty(0.0);
        }
        fortschrittEigenschaft.set(fortschritt);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Mitarbeiter that = (Mitarbeiter) obj;
        return mitarbeiterId == that.mitarbeiterId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(mitarbeiterId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Mitarbeiter{");
        sb.append("mitarbeiterId=").append(mitarbeiterId);
        sb.append(", vorname='").append(vorname).append('\'');
        sb.append(", nachname='").append(nachname).append('\'');
        sb.append(", vornameEigenschaft=").append(vornameEigenschaft);
        sb.append(", fortschrittEigenschaft=").append(fortschrittEigenschaft);
        sb.append('}');
        return sb.toString();
    }
}
