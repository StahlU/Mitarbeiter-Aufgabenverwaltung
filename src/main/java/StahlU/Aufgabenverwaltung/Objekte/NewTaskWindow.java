package StahlU.Aufgabenverwaltung.Objekte;

import StahlU.Aufgabenverwaltung.Controller.MainController;
import StahlU.Aufgabenverwaltung.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class NewTaskWindow {

    private static NewTaskWindow instance;
    private Stage stage;

    private NewTaskWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("NewTask.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage = new Stage();
            stage.setTitle("Mitarbeiter Aufgabenverwaltung!");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setInstance(NewTaskWindow instance) {

        NewTaskWindow.instance = instance;
    }

    public static NewTaskWindow getInstance() {
        if (instance == null) {
            instance = new NewTaskWindow();
        }
        return instance;
    }

    public void show(Stage mainStage) {
        if (stage != null) {
            stage.setX(mainStage.getX() + (mainStage.getWidth() / 2));
            stage.setY(mainStage.getY());
            stage.show();
        }
    }

}
