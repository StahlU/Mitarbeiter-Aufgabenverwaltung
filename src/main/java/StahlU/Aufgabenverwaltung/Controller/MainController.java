package StahlU.Aufgabenverwaltung.Controller;

import StahlU.Aufgabenverwaltung.Objekte.Aufgabe;
import StahlU.Aufgabenverwaltung.Objekte.AufgabenFenster;
import StahlU.Aufgabenverwaltung.Objekte.Mitarbeiter;
import StahlU.Aufgabenverwaltung.Speichern.JsonBackup;
import StahlU.Aufgabenverwaltung.Speichern.Kontext;
import StahlU.Aufgabenverwaltung.Speichern.SQLStorage;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class MainController {
    public TextField neuerMitarbeiternameField;
    public TextField neuerMitarbeitersurnameField;

    public TextField searchMitarbeiter;
    public TextField titleTextField;
    public TextField descriptionTextField;
    public Mitarbeiter selectedMitarbeiter;

    public ListView<Mitarbeiter> mitarbeiterListView;

    public ListView<Aufgabe> aufgabenListView;

    private final ObservableList<Mitarbeiter> mitarbeiterList = FXCollections.observableArrayList();
    private final ObservableList<Mitarbeiter> mitarbeiterSearchList = FXCollections.observableArrayList();
    Kontext kontext = new Kontext();




public void initialize() {

        kontext.setSpeicherStrategie(new SQLStorage());
        mitarbeiterListView.setFixedCellSize(25);

        mitarbeiterList.setAll(kontext.mitarbeiterLadenAusführen());
        mitarbeiterListView.setItems(mitarbeiterList);


        Platform.runLater(() -> {
            mitarbeiterListView.getScene().getWindow().setOnCloseRequest(event -> {
                JsonBackup.save(mitarbeiterList);
                Platform.exit();
            });
        });

        mitarbeiterListView.setOnMouseClicked(this::handleMitarbeiterClick);


            mitarbeiterListView.setCellFactory(lv -> new ListCell<Mitarbeiter>() {
                private final HBox hbox = new HBox(10);
                private final Label nameLabel = new Label();
                private final Region spacer = new Region();
                private final ProgressBar progressBar = new ProgressBar();
                private final Image iconEmpty = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/StahlU/Aufgabenverwaltung/icons/icon_empty.png")));
                private final Image iconPartial = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/StahlU/Aufgabenverwaltung/icons/icon_partial.png")));
                private final Image iconDone = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/StahlU/Aufgabenverwaltung/icons/icon_done.png")));

                private final ImageView iconView = new ImageView();

                {
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    progressBar.setPrefWidth(100);
                    iconView.setFitWidth(20);
                    iconView.setFitHeight(20);
                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                    hbox.getChildren().addAll(nameLabel, spacer, progressBar, iconView);
                }

                @Override
                protected void updateItem(Mitarbeiter mitarbeiter, boolean empty) {
                    super.updateItem(mitarbeiter, empty);
                    if (empty || mitarbeiter == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        nameLabel.setText(mitarbeiter.getEmployeeID()+" "+mitarbeiter.getName()+" "+mitarbeiter.getSurname());
                        progressBar.progressProperty().unbind();
                        progressBar.progressProperty().bind(mitarbeiter.fortschrittProperty());

                        double fortschritt = mitarbeiter.getFortschritt();
                        if (fortschritt >= 1.0) {
                            iconView.setImage(iconDone);
                        } else if (fortschritt > 0.0) {
                            iconView.setImage(iconPartial);
                        } else {
                            iconView.setImage(iconEmpty);
                        }

                        setGraphic(hbox);
                    }
                }
            });

        aufgabenListView.setCellFactory(lv -> new ListCell<Aufgabe>() {
            @Override
            protected void updateItem(Aufgabe aufgabe, boolean empty) {
                super.updateItem(aufgabe, empty);
                if (empty || aufgabe == null) {
                    setGraphic(null);
                } else {
                    VBox vbox = new VBox(5);
                    vbox.setAlignment(Pos.CENTER_LEFT);
                    vbox.setPrefWidth(aufgabenListView.getWidth() - 20);

                    HBox hbox = new HBox(5);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    CheckBox checkBox = new CheckBox(aufgabe.getTitle());
                    checkBox.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(checkBox, javafx.scene.layout.Priority.ALWAYS);

                    Button deleteButton = new Button("Löschen");
                    deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

                    Label descriptionLabel = new Label(aufgabe.getDescription());
                    descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
                    descriptionLabel.setWrapText(true);
                    descriptionLabel.setMaxWidth(aufgabenListView.getWidth() - 40);

                    checkBox.setSelected(aufgabe.getStatus());
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        if (selectedMitarbeiter != null) {
                            selectedMitarbeiter.aufgabeErledigt(aufgabe, newVal, kontext);
                            mitarbeiterListView.refresh();
                        }
                    });

                    deleteButton.setOnAction(event -> {
                        if (selectedMitarbeiter != null) {
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Aufgabe löschen");
                            alert.setHeaderText("Sind Sie sicher?");
                            alert.setContentText("Möchten Sie die Aufgabe wirklich löschen?");

                            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
                            if (result != ButtonType.OK) {
                                return;
                            }

                            kontext.aufgabeLöschenAusführen(selectedMitarbeiter, aufgabe);
                            aufgabenListView.getItems().remove(aufgabe);
                            mitarbeiterListView.refresh();
                        }
                    });

                    hbox.getChildren().addAll(checkBox, deleteButton);
                    vbox.getChildren().addAll(hbox, descriptionLabel);
                    setGraphic(vbox);
                }
            }
        });

        neuerMitarbeiternameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addMitarbeiter(new ActionEvent());
                mitarbeiterListView.refresh();
            }
        });
        neuerMitarbeitersurnameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addMitarbeiter(new ActionEvent());
                mitarbeiterListView.refresh();
            }
        });

        titleTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addAufgabe(new ActionEvent());
                mitarbeiterListView.refresh();
            }
        });
        descriptionTextField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addAufgabe(new ActionEvent());
                mitarbeiterListView.refresh();
            }
        });


    }


    private void handleMitarbeiterClick(MouseEvent mouseEvent) {

        selectedMitarbeiter = mitarbeiterListView.getSelectionModel().getSelectedItem();

        if (selectedMitarbeiter != null) {

            ObservableList<Aufgabe> aufgaben = kontext.aufgabenLadenAusführen(selectedMitarbeiter);
            aufgabenListView.setItems(aufgaben);

                if (aufgabenListView.getItems() != null) {
                    aufgabenListView.setItems(aufgaben);
                } else {
                    aufgabenListView.setItems(null);
                }

        }
    }


    private Timeline searchDelay;

    @FXML
    public void search(KeyEvent actionEvent) {
        if (searchDelay != null) {
            searchDelay.stop();
        }

        searchDelay = new Timeline(new KeyFrame(javafx.util.Duration.millis(500), event -> {
            mitarbeiterSearchList.clear();
            aufgabenListView.setItems(null);


            mitarbeiterSearchList.setAll(
                mitarbeiterList.stream()
                    .filter(mitarbeiter -> (mitarbeiter.getEmployeeID() + " " + mitarbeiter.getName() + " " + mitarbeiter.getSurname())
                        .toLowerCase()
                        .contains(searchMitarbeiter.getText().toLowerCase())
                    ).toList()
            );

            if (mitarbeiterSearchList.isEmpty()) {
                mitarbeiterListView.setItems(null);
            } else {
                mitarbeiterListView.setItems(mitarbeiterSearchList);
                mitarbeiterListView.setPlaceholder(new Label("Keine Mitarbeiter gefunden"));
            }
        }));

        searchDelay.setCycleCount(1);
        searchDelay.play();
    }

    @FXML
    public void addMitarbeiter(ActionEvent actionEvent) {
        if (neuerMitarbeiternameField.getText().isEmpty() || neuerMitarbeitersurnameField.getText().isEmpty()) {
            return;
        }

        String name = neuerMitarbeiternameField.getText();
        String surname = neuerMitarbeitersurnameField.getText();

        boolean exists = mitarbeiterList.stream()
            .anyMatch(m -> m.getName().equalsIgnoreCase(name) && m.getSurname().equalsIgnoreCase(surname));

        if (exists) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Fehler!");
            alert.setHeaderText("Ein Mitarbeiter mit diesem Namen existiert bereits.");
            alert.setContentText("Möchten Sie den Mitarbeiter trotzdem hinzufügen?");


            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
        }


        mitarbeiterList.add(kontext.mitarbeiterSpeichernAusführen(name, surname));
        mitarbeiterList.setAll(kontext.mitarbeiterLadenAusführen());
        mitarbeiterListView.setItems(mitarbeiterList);
        mitarbeiterListView.refresh();

        if (!mitarbeiterList.isEmpty()) {
            mitarbeiterListView.getSelectionModel().selectLast();
            selectedMitarbeiter = mitarbeiterListView.getSelectionModel().getSelectedItem();
            mitarbeiterListView.requestFocus();
        }

        searchMitarbeiter.clear();
        neuerMitarbeiternameField.clear();
        neuerMitarbeitersurnameField.clear();
        aufgabenListView.setItems(null);
    }

    @FXML
    public void removeMitarbeiter(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mitarbeiter löschen");
        alert.setHeaderText("Sind Sie sicher?");
        alert.setContentText("Möchten Sie den Mitarbeiter wirklich löschen?");

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        if (result != ButtonType.OK) {
            return;
        }

        kontext.mitarbeiterLöschenAusführen(selectedMitarbeiter);
        mitarbeiterList.remove(selectedMitarbeiter);
        aufgabenListView.setItems(null);
        mitarbeiterListView.refresh();


    }

    @FXML
    public void addAufgabe(ActionEvent actionEvent) {
        if (titleTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty()) {
            return;
        }

        String titleText = this.titleTextField.getText();
        String titledescription = this.descriptionTextField.getText();

        if (selectedMitarbeiter != null) {


            kontext.aufgabeSpeichernAusführen(selectedMitarbeiter,titleText,titledescription);
            this.titleTextField.clear();
            this.descriptionTextField.clear();
            mitarbeiterListView.refresh();
        }
        ObservableList<Aufgabe> aufgaben = kontext.aufgabenLadenAusführen(selectedMitarbeiter);
        aufgabenListView.setItems(aufgaben);
        mitarbeiterListView.requestFocus();

    }
    @FXML
    public void newAufgabe(ActionEvent actionEvent) {
        Stage mainStage = (Stage) mitarbeiterListView.getScene().getWindow();
        AufgabenFenster mitarbeiterFenster = AufgabenFenster.getInstance();
        mitarbeiterFenster.show(mainStage);
    }


}



