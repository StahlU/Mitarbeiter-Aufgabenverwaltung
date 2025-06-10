module StahlU.Aufgabenverwaltung {
    requires javafx.controls;
	requires javafx.fxml;
    requires com.google.gson;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
	requires javafx.graphics;

	opens StahlU.Aufgabenverwaltung to javafx.fxml, com.google.gson,org.xerial.sqlitejdbc;
    exports StahlU.Aufgabenverwaltung;
	exports StahlU.Aufgabenverwaltung.Speichern;
	opens StahlU.Aufgabenverwaltung.Speichern to com.google.gson, javafx.fxml, org.xerial.sqlitejdbc;
	exports StahlU.Aufgabenverwaltung.Test;
	opens StahlU.Aufgabenverwaltung.Test to com.google.gson, javafx.fxml, org.xerial.sqlitejdbc;
	exports StahlU.Aufgabenverwaltung.Controller;
	opens StahlU.Aufgabenverwaltung.Controller to com.google.gson, javafx.fxml, org.xerial.sqlitejdbc;
	exports StahlU.Aufgabenverwaltung.Objekte;
	opens StahlU.Aufgabenverwaltung.Objekte to com.google.gson, javafx.fxml, org.xerial.sqlitejdbc;

}