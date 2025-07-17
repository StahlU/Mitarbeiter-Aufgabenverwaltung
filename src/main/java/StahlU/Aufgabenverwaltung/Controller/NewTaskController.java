package StahlU.Aufgabenverwaltung.Controller;

import StahlU.Aufgabenverwaltung.Objekte.*;
import StahlU.Aufgabenverwaltung.Speichern.Context;
import StahlU.Aufgabenverwaltung.Speichern.SQLStorage;
import StahlU.Aufgabenverwaltung.Service.TaskEmployeeService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Collections;
import java.util.Comparator;

public class NewTaskController {

    public TextField titleTextField;
    public TextArea descriptionTextField;
    public TextField searchTextField;


    ObservableList<Employee> employeeObservableList = FXCollections.observableArrayList();
    ObservableList<Employee> chosenEmployeeObservableList = FXCollections.observableArrayList();

    ObservableList<Employee> employeeSearchList = FXCollections.observableArrayList();

    public ListView<Employee> employeeListView;
    public ListView<Employee> chosenEmployeeListView;
    Context context = Context.getInstance();

    Employee selectedEmployee = null;

    TaskEmployeeService taskEmployeeService = new TaskEmployeeService();


    @FXML
    public void initialize(){

        context.setStorageStrategy(new SQLStorage());

        employeeObservableList.setAll(SharedData.getEmployeeList());
        Collections.sort(employeeObservableList, Comparator.comparingInt(Employee::getEmployeeId));

        employeeListView.setItems(employeeObservableList);
        chosenEmployeeListView.setItems(chosenEmployeeObservableList);

        chosenEmployeeListView.setPlaceholder(new Label("Keine Mitarbeiter ausgewählt"));
        employeeListView.setPlaceholder(new Label("Keine Mitarbeiter gefunden"));

        employeeListView.setCellFactory(listView -> new EmployeeCell());
        chosenEmployeeListView.setCellFactory(listView -> new EmployeeCell());

        Platform.runLater(() -> {
            Stage stage = (Stage) employeeListView.getScene().getWindow();
            stage.setOnCloseRequest(event -> {
                event.consume(); // verhindert das automatische Schließen
                cancel();
            });
        });
    }
    @FXML
    public void save() {
        if (titleTextField.getText().isEmpty() || descriptionTextField.getText().isEmpty()) {
            System.out.println("Etwas ist leer");
            return;
        }

        Task task = new Task(titleTextField.getText(), descriptionTextField.getText());
        SharedData.addTask(task);

        context.saveTask(chosenEmployeeObservableList, titleTextField.getText(), descriptionTextField.getText());
        System.out.println("Speichern");
        MainController.getInstance().updateProgress();
        MainController.getInstance().loadTaskList();
        ((Stage) titleTextField.getScene().getWindow()).close();
        NewTaskWindow.setInstance(null);
    }

    @FXML
    public void cancel(){
        System.out.println("Abbrechen");
        ((Stage) titleTextField.getScene().getWindow()).close();
        NewTaskWindow.setInstance(null);

    }

    @FXML
    public void chooseEmployee(){
        selectedEmployee = employeeListView.getSelectionModel().getSelectedItem();
        taskEmployeeService.addEmployeeToChosenList(selectedEmployee, employeeObservableList, chosenEmployeeObservableList, employeeSearchList, searchTextField.getText());
        employeeListView.getSelectionModel().clearSelection();
    }

    @FXML
    public void unchooseEmployee() {
        selectedEmployee = chosenEmployeeListView.getSelectionModel().getSelectedItem();
        taskEmployeeService.removeEmployeeFromChosenList(selectedEmployee, employeeObservableList, chosenEmployeeObservableList, employeeSearchList, searchTextField.getText());
        employeeListView.getSelectionModel().clearSelection();
    }

    private Timeline searchDelay;
    @FXML
    public void searchEmployee(KeyEvent actionEvent) {
        if (searchDelay != null) {
            searchDelay.stop();
        }

        searchDelay = new Timeline(new KeyFrame(javafx.util.Duration.millis(500), event -> {
            employeeSearchList.clear();
            employeeListView.setItems(null);

            employeeSearchList.setAll(
                    employeeObservableList.stream()
                            .filter(employee -> (employee.getEmployeeId() + " " + employee.getFirstName() + " " + employee.getLastName())
                            .toLowerCase()
                            .contains(searchTextField.getText().toLowerCase())
                            ).toList()
            );

            if (employeeSearchList.isEmpty()) {
                employeeListView.setItems(null);
            } else {
                employeeListView.setItems(employeeSearchList);

            }
        }));

        searchDelay.setCycleCount(1);
        searchDelay.play();
    }
}
