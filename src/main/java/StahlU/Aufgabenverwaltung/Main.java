package StahlU.Aufgabenverwaltung;

import StahlU.Aufgabenverwaltung.Test.Generator;
import StahlU.Aufgabenverwaltung.Test.SQLFunktionen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("TaskTracker.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Mitarbeiter Aufgabenverwaltung!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        SQLFunktionen.treiberTest();
        SQLFunktionen.createTablesIfNotExists();
//        Generator.fillTables(1000,10);
        launch();



    }
}