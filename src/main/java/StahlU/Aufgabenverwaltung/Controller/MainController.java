package StahlU.Aufgabenverwaltung.Controller;

import StahlU.Aufgabenverwaltung.Objekte.Aufgabe;
import StahlU.Aufgabenverwaltung.Objekte.AufgabenFenster;
import StahlU.Aufgabenverwaltung.Objekte.Mitarbeiter;
import StahlU.Aufgabenverwaltung.Speichern.Kontext;
import StahlU.Aufgabenverwaltung.Speichern.SQLStorage;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.Objects;

public class MainController {
    public TextField vornameFeld;
    public TextField nachnameFeld;
    public TextField suchfeldMitarbeiter;
    public TextField titelFeld;
    public TextField beschreibungFeld;
    public Mitarbeiter ausgewaehlterMitarbeiter;
    public ListView<Mitarbeiter> mitarbeiterListenAnsicht;
    public ListView<Aufgabe> aufgabenListenAnsicht;

    private final ObservableList<Mitarbeiter> mitarbeiterListe = FXCollections.observableArrayList();
    private final ObservableList<Mitarbeiter> mitarbeiterSuchListe = FXCollections.observableArrayList();
    private final ObservableList<Aufgabe> alleAufgaben = FXCollections.observableArrayList();
    Kontext kontext = new Kontext();

    public void initialize() {
        kontext.setSpeicherStrategie(new SQLStorage());
        mitarbeiterListenAnsicht.setFixedCellSize(25);

        mitarbeiterListe.setAll(kontext.ladeMitarbeiter());
        mitarbeiterListenAnsicht.setItems(mitarbeiterListe);

        alleAufgaben.setAll(kontext.ladeAlleAufgaben(mitarbeiterListe));

        fortschrittAktualisieren();
        mitarbeiterListenAnsicht.setOnMouseClicked(this::mitarbeiterGeklickt);

        mitarbeiterListenAnsicht.setCellFactory(lv -> new ListCell<Mitarbeiter>() {
            private final HBox hbox = new HBox(10);
            private final Label nameLabel = new Label();
            private final Region spacer = new Region();
            private final ProgressBar fortschrittBalken = new ProgressBar();
            private final Image iconLeer = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/StahlU/Aufgabenverwaltung/icons/icon_empty.png")));
            private final Image iconTeilweise = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/StahlU/Aufgabenverwaltung/icons/icon_partial.png")));
            private final Image iconFertig = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/StahlU/Aufgabenverwaltung/icons/icon_done.png")));
            private final ImageView iconView = new ImageView();

            {
                hbox.setAlignment(Pos.CENTER_LEFT);
                fortschrittBalken.setPrefWidth(100);
                iconView.setFitWidth(20);
                iconView.setFitHeight(20);
                HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
                hbox.getChildren().addAll(nameLabel, spacer, fortschrittBalken, iconView);
            }

            @Override
            protected void updateItem(Mitarbeiter mitarbeiter, boolean empty) {
                super.updateItem(mitarbeiter, empty);
                if (empty || mitarbeiter == null) {
                    setGraphic(null);
                } else {
                    mitarbeiterListenAnsicht.setFixedCellSize(25);
                    System.setProperty("java.util.logging.config.file", "logging.properties");
                    nameLabel.setText(mitarbeiter.getMitarbeiterId() + " " + mitarbeiter.getVorname() + " " + mitarbeiter.getNachname());
                    fortschrittBalken.progressProperty().unbind();
                    fortschrittBalken.progressProperty().bind(mitarbeiter.fortschrittEigenschaft());

                    double fortschritt = mitarbeiter.getFortschritt();
                    if (fortschritt >= 1.0) {
                        iconView.setImage(iconFertig);
                    } else if (fortschritt > 0.0) {
                        iconView.setImage(iconTeilweise);
                    } else {
                        iconView.setImage(iconLeer);
                    }
                    setGraphic(hbox);
                }
            }
        });

        aufgabenListenAnsicht.setCellFactory(lv -> new ListCell<Aufgabe>() {
            @Override
            protected void updateItem(Aufgabe aufgabe, boolean empty) {
                super.updateItem(aufgabe, empty);
                if (empty || aufgabe == null) {
                    setGraphic(null);
                    setStyle("");
                } else {
                    setMaxWidth(aufgabenListenAnsicht.getWidth() - 40);

                    VBox vbox = new VBox(5);
                    vbox.setAlignment(Pos.CENTER_LEFT);

                    HBox hbox = new HBox(5);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    CheckBox checkBox = new CheckBox(aufgabe.getTitel());

                    Region spacer = new Region();
                    spacer.setMaxWidth(Double.MAX_VALUE);

                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                    Button loeschenButton = new Button("Löschen");
                    loeschenButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

                    Label beschreibungLabel = new Label(aufgabe.getBeschreibung());
                    beschreibungLabel.setWrapText(true);
                    beschreibungLabel.setMaxWidth(aufgabenListenAnsicht.getWidth() - 40);

                    checkBox.setSelected(aufgabe.isErledigt());
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        aufgabe.setStatus(newVal, kontext);
                        fortschrittAktualisieren();
                        mitarbeiterListenAnsicht.refresh();
                    });

                    loeschenButton.setOnAction(event -> {
                        aufgabeEntfernen(aufgabe);
                        mitarbeiterListenAnsicht.refresh();
                    });

                    hbox.getChildren().addAll(checkBox, spacer, loeschenButton);
                    vbox.getChildren().addAll(hbox, beschreibungLabel);
                    setGraphic(vbox);

                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem loeschenItem = new MenuItem("Aufgabe löschen");
                    MenuItem bearbeitenItem = new MenuItem("Aufgabe bearbeiten");

                    loeschenItem.setOnAction(e -> aufgabeEntfernen(aufgabe));
                    bearbeitenItem.setOnAction(e -> aufgabeBearbeiten(aufgabe));

                    contextMenu.getItems().addAll(loeschenItem, bearbeitenItem);
                    setContextMenu(contextMenu);
                }
            }
        });

        vornameFeld.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mitarbeiterHinzufuegen(new ActionEvent());
                mitarbeiterListenAnsicht.refresh();
            }
        });
        nachnameFeld.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                mitarbeiterHinzufuegen(new ActionEvent());
                mitarbeiterListenAnsicht.refresh();
            }
        });

        titelFeld.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                aufgabeHinzufuegen(new ActionEvent());
                mitarbeiterListenAnsicht.refresh();
            }
        });
        beschreibungFeld.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                aufgabeHinzufuegen(new ActionEvent());
                mitarbeiterListenAnsicht.refresh();
            }
        });
    }

    @FXML
    public void aufgabeHinzufuegen(ActionEvent actionEvent) {
        if (titelFeld.getText().isEmpty() || beschreibungFeld.getText().isEmpty()) {
            return;
        }
        if (ausgewaehlterMitarbeiter == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Keine Auswahl");
            alert.setHeaderText("Bitte wählen Sie einen Mitarbeiter aus, um eine Aufgabe hinzuzufügen.");
            alert.showAndWait();
            return;
        }
        String titel = this.titelFeld.getText();
        String beschreibung = this.beschreibungFeld.getText();

        Aufgabe neueAufgabe = new Aufgabe(titel, beschreibung);
        neueAufgabe.fuegeMitarbeiterHinzu(ausgewaehlterMitarbeiter);
        kontext.speichereAufgabe(ausgewaehlterMitarbeiter, titel, beschreibung);

        alleAufgaben.setAll(kontext.ladeAlleAufgaben(mitarbeiterListe));

        this.titelFeld.clear();
        this.beschreibungFeld.clear();
        mitarbeiterListenAnsicht.refresh();

        mitarbeiterGeklickt(null);
        fortschrittAktualisieren();
    }

    public void aufgabeEntfernen(Aufgabe aufgabe) {
        if (ausgewaehlterMitarbeiter != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Aufgabe löschen");
            alert.setHeaderText("Sind Sie sicher?");
            alert.setContentText("Möchten Sie die Aufgabe wirklich löschen?");
            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (result != ButtonType.OK) {
                return;
            }
            aufgabe.entferneMitarbeiter(ausgewaehlterMitarbeiter);
            kontext.loescheAufgabe(ausgewaehlterMitarbeiter, aufgabe);

            alleAufgaben.setAll(kontext.ladeAlleAufgaben(mitarbeiterListe));
            mitarbeiterGeklickt(null);
            mitarbeiterListenAnsicht.refresh();
            fortschrittAktualisieren();
        }
    }

    private void fortschrittAktualisieren() {
        for (Mitarbeiter mitarbeiter : mitarbeiterListe) {
            long gesamt = alleAufgaben.stream().filter(a -> a.hatMitarbeiter(mitarbeiter)).count();
            long erledigt = alleAufgaben.stream().filter(a -> a.hatMitarbeiter(mitarbeiter) && a.isErledigt()).count();
            double fortschritt = (gesamt == 0) ? 0.0 : (double) erledigt / gesamt;
            mitarbeiter.setFortschritt(fortschritt);
        }
    }

    private void mitarbeiterGeklickt(MouseEvent mouseEvent) {
        ausgewaehlterMitarbeiter = mitarbeiterListenAnsicht.getSelectionModel().getSelectedItem();
        if (ausgewaehlterMitarbeiter != null) {
            ObservableList<Aufgabe> aufgaben = FXCollections.observableArrayList();
            for (Aufgabe aufgabe : alleAufgaben) {
                if (aufgabe.hatMitarbeiter(ausgewaehlterMitarbeiter)) {
                    aufgaben.add(aufgabe);
                }
            }
            aufgabenListenAnsicht.setItems(aufgaben);
            aufgabenListenAnsicht.setPlaceholder(new Label("Keine Aufgaben für diesen Mitarbeiter"));
            aufgabenListenAnsicht.getSelectionModel().clearSelection();
        }
    }

    private Timeline suchVerzoegerung;

    @FXML
    public void mitarbeiterSuchen(KeyEvent actionEvent) {
        if (suchVerzoegerung != null) {
            suchVerzoegerung.stop();
        }

        suchVerzoegerung = new Timeline(new KeyFrame(javafx.util.Duration.millis(500), event -> {
            mitarbeiterSuchListe.clear();
            aufgabenListenAnsicht.setItems(null);

            mitarbeiterSuchListe.setAll(
                mitarbeiterListe.stream()
                    .filter(mitarbeiter -> (mitarbeiter.getMitarbeiterId() + " " + mitarbeiter.getVorname() + " " + mitarbeiter.getNachname())
                        .toLowerCase()
                        .contains(suchfeldMitarbeiter.getText().toLowerCase())
                    ).toList()
            );

            if (mitarbeiterSuchListe.isEmpty()) {
                mitarbeiterListenAnsicht.setItems(null);
            } else {
                mitarbeiterListenAnsicht.setItems(mitarbeiterSuchListe);
                mitarbeiterListenAnsicht.setPlaceholder(new Label("Keine Mitarbeiter gefunden"));
            }
        }));

        suchVerzoegerung.setCycleCount(1);
        suchVerzoegerung.play();
    }

    @FXML
    public void mitarbeiterHinzufuegen(ActionEvent actionEvent) {
        if (vornameFeld.getText().isEmpty() || nachnameFeld.getText().isEmpty()) {
            return;
        }

        String vorname = vornameFeld.getText();
        String nachname = nachnameFeld.getText();

        boolean existiert = mitarbeiterListe.stream()
            .anyMatch(m -> m.getVorname().equalsIgnoreCase(vorname) && m.getNachname().equalsIgnoreCase(nachname));

        if (existiert) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Fehler!");
            alert.setHeaderText("Ein Mitarbeiter mit diesem Namen existiert bereits.");
            alert.setContentText("Möchten Sie den Mitarbeiter trotzdem hinzufügen?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
        }

        mitarbeiterListe.add(kontext.speichereMitarbeiter(vorname, nachname));
        mitarbeiterListe.setAll(kontext.ladeMitarbeiter());
        mitarbeiterListenAnsicht.setItems(mitarbeiterListe);

        alleAufgaben.setAll(kontext.ladeAlleAufgaben(mitarbeiterListe));
        mitarbeiterListenAnsicht.refresh();

        if (!mitarbeiterListe.isEmpty()) {
            mitarbeiterListenAnsicht.getSelectionModel().selectLast();
            ausgewaehlterMitarbeiter = mitarbeiterListenAnsicht.getSelectionModel().getSelectedItem();
            mitarbeiterListenAnsicht.requestFocus();
        }

        suchfeldMitarbeiter.clear();
        vornameFeld.clear();
        nachnameFeld.clear();
        aufgabenListenAnsicht.setItems(null);
        fortschrittAktualisieren();
    }

    @FXML
    public void mitarbeiterEntfernen(ActionEvent actionEvent) {
        if (ausgewaehlterMitarbeiter == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Mitarbeiter löschen");
        alert.setHeaderText("Sind Sie sicher?");
        alert.setContentText("Möchten Sie den Mitarbeiter wirklich löschen?");

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        if (result != ButtonType.OK) {
            return;
        }

        kontext.loescheMitarbeiter(ausgewaehlterMitarbeiter);
        mitarbeiterListe.remove(ausgewaehlterMitarbeiter);
        mitarbeiterListenAnsicht.setItems(mitarbeiterListe);

        aufgabenListenAnsicht.setItems(null);
        mitarbeiterListenAnsicht.refresh();
    }

    public void aufgabeBearbeiten(Aufgabe aufgabe) {
        if (aufgabe == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Keine Auswahl");
            alert.setHeaderText("Keine Aufgabe ausgewählt");
            alert.setContentText("Bitte wählen Sie eine Aufgabe aus, um sie zu bearbeiten.");
            alert.showAndWait();
            return;
        }

        Dialog<Aufgabe> dialog = new Dialog<>();
        dialog.setTitle("Aufgabe bearbeiten");
        dialog.setHeaderText("Bearbeiten Sie die ausgewählte Aufgabe");

        ButtonType speichernButton = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(speichernButton, ButtonType.CANCEL);

        TextField titelFeldDialog = new TextField(aufgabe.getTitel());
        TextArea beschreibungFeldDialog = new TextArea(aufgabe.getBeschreibung());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Titel:"), 0, 0);
        grid.add(titelFeldDialog, 1, 0);
        grid.add(new Label("Beschreibung:"), 0, 1);
        grid.add(beschreibungFeldDialog, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == speichernButton) {
                aufgabe.setBeschreibung(beschreibungFeldDialog.getText());
                aufgabe.setTitel(titelFeldDialog.getText());
                return aufgabe;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(aktualisierteAufgabe -> {
            kontext.aktualisiereAufgabeDaten(aufgabe);
            aufgabenListenAnsicht.refresh();
        });
    }


}
