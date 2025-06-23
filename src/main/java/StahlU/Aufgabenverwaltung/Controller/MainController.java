package StahlU.Aufgabenverwaltung.Controller;

import StahlU.Aufgabenverwaltung.Objekte.NewTaskWindow;
import StahlU.Aufgabenverwaltung.Objekte.Task;
import StahlU.Aufgabenverwaltung.Objekte.Employee;
import StahlU.Aufgabenverwaltung.Speichern.Context;
import StahlU.Aufgabenverwaltung.Speichern.SQLStorage;
import StahlU.Aufgabenverwaltung.Service.MainService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
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

import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class MainController {
    private static MainController instance;

    public TextField firstNameField;
    public TextField lastNameField;
    public TextField employeeSearchField;
    public TextField titleField;
    public TextField descriptionField;
    public Employee selectedEmployee;
    public ListView<Employee> employeeListView;
    public ListView<Task> taskListView;

    private final ObservableList<Employee> employeeList = SharedData.getEmployeeList();
    private final ObservableList<Task> allTasks = SharedData.getTaskList();
    private final ObservableList<Employee> employeeSearchList = FXCollections.observableArrayList();
    Context context = new Context();
    MainService mainService = new MainService();

    public MainController() {
        instance = this;
    }

    public static MainController getInstance() {
        return instance;
    }
    public void loadTaskList() {
        taskListView.setItems(mainService.getTasksForEmployee(allTasks, selectedEmployee));
    }

    public void initialize() {
        context.setStorageStrategy(new SQLStorage());
        employeeListView.setFixedCellSize(25);

        employeeList.setAll(context.loadEmployees());
        Collections.sort(employeeList, Comparator.comparingInt(Employee::getEmployeeId));
        employeeListView.setItems(employeeList);
        employeeListView.setPlaceholder(new Label("Keine Mitarbeiter gefunden"));
        taskListView.setPlaceholder(new Label("Keine Aufgaben für diesen Mitarbeiter"));

        allTasks.setAll(context.loadAllTasks(employeeList));

        updateProgress();
        employeeListView.setOnMouseClicked(this::employeeClicked);

        Platform.runLater(() -> {
            Stage stage = (Stage) employeeListView.getScene().getWindow();
            stage.setOnCloseRequest(event -> Platform.exit());
        });



        employeeListView.setCellFactory(lv -> new ListCell<Employee>() {
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
            protected void updateItem(Employee employee, boolean empty) {
                super.updateItem(employee, empty);
                if (empty || employee == null) {
                    setGraphic(null);
                } else {
                    employeeListView.setFixedCellSize(25);
                    nameLabel.setText(employee.getEmployeeId() + " " + employee.getFirstName() + " " + employee.getLastName());
                    progressBar.progressProperty().unbind();
                    progressBar.progressProperty().bind(employee.progressProperty());

                    double progress = employee.getProgress();
                    if (progress >= 1.0) {
                        iconView.setImage(iconDone);
                    } else if (progress > 0.0) {
                        iconView.setImage(iconPartial);
                    } else {
                        iconView.setImage(iconEmpty);
                    }
                    setGraphic(hbox);
                }
            }
        });

        taskListView.setCellFactory(lv -> new ListCell<Task>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setGraphic(null);
                    setStyle("");
                } else {
                    setMaxWidth(taskListView.getWidth() - 40);

                    VBox vbox = new VBox(5);
                    vbox.setAlignment(Pos.CENTER_LEFT);

                    HBox hbox = new HBox(5);
                    hbox.setAlignment(Pos.CENTER_LEFT);

                    CheckBox checkBox = new CheckBox(task.getTitle());

                    Region spacer = new Region();
                    spacer.setMaxWidth(Double.MAX_VALUE);

                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

                    Button deleteButton = new Button("Löschen");
                    deleteButton.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white;");

                    Label descriptionLabel = new Label(task.getDescription());
                    descriptionLabel.setWrapText(true);
                    descriptionLabel.setMaxWidth(taskListView.getWidth() - 40);

                    checkBox.setSelected(task.isDone());
                    checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
                        task.setStatus(newVal, context);
                        updateProgress();
                        employeeListView.refresh();
                    });

                    deleteButton.setOnAction(event -> {
                        removeTask(task);
                        employeeListView.refresh();
                    });

                    hbox.getChildren().addAll(checkBox, spacer, deleteButton);
                    vbox.getChildren().addAll(hbox, descriptionLabel);
                    setGraphic(vbox);

                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem deleteItem = new MenuItem("Aufgabe löschen");
                    MenuItem editItem = new MenuItem("Aufgabe bearbeiten");

                    deleteItem.setOnAction(e -> removeTask(task));
                    editItem.setOnAction(e -> editTask(task));

                    contextMenu.getItems().addAll(deleteItem, editItem);
                    setContextMenu(contextMenu);
                }
            }
        });

        firstNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addEmployee(new ActionEvent());
                employeeListView.refresh();
            }
        });
        lastNameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addEmployee(new ActionEvent());
                employeeListView.refresh();
            }
        });

        titleField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addTask(new ActionEvent());
                employeeListView.refresh();
            }
        });
        descriptionField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                addTask(new ActionEvent());
                employeeListView.refresh();
            }
        });
    }

    @FXML
    public void addTask(ActionEvent actionEvent) {
        if (titleField.getText().isEmpty() || descriptionField.getText().isEmpty()) {
            return;
        }
        if (selectedEmployee == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Keine Auswahl");
            alert.setHeaderText("Bitte wählen Sie einen Mitarbeiter aus, um eine Aufgabe hinzuzufügen.");
            alert.showAndWait();
            return;
        }
        mainService.addTask(titleField.getText(), descriptionField.getText(), selectedEmployee, allTasks, employeeList, context);
        titleField.clear();
        descriptionField.clear();
        employeeListView.refresh();
        employeeClicked(null);
        mainService.updateProgress(employeeList, allTasks);
    }

    public void removeTask(Task task) {
        if (selectedEmployee != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Aufgabe löschen");
            alert.setHeaderText("Sind Sie sicher?");
            alert.setContentText("Möchten Sie die Aufgabe wirklich löschen?");
            ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
            if (result != ButtonType.OK) {
                return;
            }
            mainService.removeTask(task, selectedEmployee, allTasks, employeeList, context);
            employeeClicked(null);
            employeeListView.refresh();
            mainService.updateProgress(employeeList, allTasks);
        }
    }

    void updateProgress() {
        mainService.updateProgress(employeeList, allTasks);
    }

    private void employeeClicked(MouseEvent mouseEvent) {
        selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            ObservableList<Task> tasks = FXCollections.observableArrayList();
            for (Task task : allTasks) {
                if (task.hasEmployee(selectedEmployee)) {
                    tasks.add(task);
                }
            }
            taskListView.setItems(tasks);
            taskListView.setPlaceholder(new Label("Keine Aufgaben für diesen Mitarbeiter"));
            taskListView.getSelectionModel().clearSelection();
        }
    }

    private Timeline searchDelay;

    @FXML
    public void searchEmployee(KeyEvent actionEvent) {
        if (searchDelay != null) {
            searchDelay.stop();
        }

        searchDelay = new Timeline(new KeyFrame(javafx.util.Duration.millis(500), event -> {
            employeeSearchList.clear();
            taskListView.setItems(null);

            employeeSearchList.setAll(
                employeeList.stream()
                    .filter(employee -> (employee.getEmployeeId() + " " + employee.getFirstName() + " " + employee.getLastName())
                        .toLowerCase()
                        .contains(employeeSearchField.getText().toLowerCase())
                    ).toList()
            );

            if (employeeSearchList.isEmpty()) {
                employeeListView.setItems(null);
            } else {
                employeeListView.setItems(employeeSearchList);
                employeeListView.setPlaceholder(new Label("Keine Mitarbeiter gefunden"));
            }
        }));

        searchDelay.setCycleCount(1);
        searchDelay.play();
    }

    @FXML
    public void addEmployee(ActionEvent actionEvent) {
        if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
            return;
        }

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        boolean exists = employeeList.stream()
            .anyMatch(m -> m.getFirstName().equalsIgnoreCase(firstName) && m.getLastName().equalsIgnoreCase(lastName));

        if (exists) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Fehler!");
            alert.setHeaderText("Ein Mitarbeiter mit diesem Namen existiert bereits.");
            alert.setContentText("Möchten Sie den Mitarbeiter trotzdem hinzufügen?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                return;
            }
        }
        mainService.addEmployee(firstName, lastName, employeeList, allTasks, context);
        employeeListView.setItems(employeeList);
        allTasks.setAll(context.loadAllTasks(employeeList));
        employeeListView.refresh();
        if (!employeeList.isEmpty()) {
            employeeListView.getSelectionModel().selectLast();
            selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
            employeeListView.requestFocus();
        }
        employeeSearchField.clear();
        firstNameField.clear();
        lastNameField.clear();
        taskListView.setItems(null);
        mainService.updateProgress(employeeList, allTasks);
    }

    @FXML
    public void removeEmployee(ActionEvent actionEvent) {
        if (selectedEmployee == null) {
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
        mainService.removeEmployee(selectedEmployee, employeeList, allTasks, context);
        taskListView.setItems(null);
        employeeListView.refresh();
    }

    public void editTask(Task task) {
        if (task == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Keine Auswahl");
            alert.setHeaderText("Keine Aufgabe ausgewählt");
            alert.setContentText("Bitte wählen Sie eine Aufgabe aus, um sie zu bearbeiten.");
            alert.showAndWait();
            return;
        }

        Dialog<Task> dialog = new Dialog<>();
        dialog.setTitle("Aufgabe bearbeiten");
        dialog.setHeaderText("Bearbeiten Sie die ausgewählte Aufgabe");

        ButtonType saveButton = new ButtonType("Speichern", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField titleFieldDialog = new TextField(task.getTitle());
        TextArea descriptionFieldDialog = new TextArea(task.getDescription());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Titel:"), 0, 0);
        grid.add(titleFieldDialog, 1, 0);
        grid.add(new Label("Beschreibung:"), 0, 1);
        grid.add(descriptionFieldDialog, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                mainService.editTask(task, titleFieldDialog.getText(), descriptionFieldDialog.getText(), context);
                return task;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedTask -> {
            taskListView.refresh();
        });
    }

    @FXML
    public void newTask(ActionEvent actionEvent) {
        System.out.println("Neue Aufgabe erstellen");
        NewTaskWindow window = NewTaskWindow.getInstance();
        window.show((Stage) employeeListView.getScene().getWindow());
    }
}
