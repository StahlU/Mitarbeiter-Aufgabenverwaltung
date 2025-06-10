package StahlU.Aufgabenverwaltung.Objekte;

            import StahlU.Aufgabenverwaltung.Main;
            import javafx.fxml.FXMLLoader;
            import javafx.scene.Scene;
            import javafx.stage.Stage;

            public class AufgabenFenster {

                private static AufgabenFenster instance;
                private Stage stage;

                private AufgabenFenster() {
                }

                public static AufgabenFenster getInstance() {
                    if (instance == null) {
                        instance = new AufgabenFenster();
                    }
                    return instance;
                }

                public void show(Stage mainStage) {
                    if (stage == null || !stage.isShowing()) {
                        try {
                            FXMLLoader loader = new FXMLLoader(Main.class.getResource("newTask.fxml"));
                            Scene scene = new Scene(loader.load());
                            stage = new Stage();
                            stage.setTitle("Aufgaben Fenster");
                            stage.setScene(scene);
                            stage.setResizable(false);

                            stage.setX(mainStage.getX() + mainStage.getWidth());
                            stage.setY(mainStage.getY());

                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        stage.requestFocus();
                    }
                }
            }