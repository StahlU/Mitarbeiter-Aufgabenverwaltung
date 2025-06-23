package StahlU.Aufgabenverwaltung.Controller;

import StahlU.Aufgabenverwaltung.Objekte.Employee;
import StahlU.Aufgabenverwaltung.Objekte.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SharedData {

    private static final ObservableList<Employee> employeeList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private static final ObservableList<Task> taskList = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());

    public static ObservableList<Employee> getEmployeeList() {
        return employeeList;
    }

    public static ObservableList<Task> getTaskList() {
        return taskList;
    }

    public static void addEmployee(Employee employee) {
        employeeList.add(employee);
    }

    public static void removeEmployee(Employee employee) {
        employeeList.remove(employee);
    }

    public static void addTask(Task task) {
        taskList.add(task);
    }

    public static void removeTask(Task task) {
        taskList.remove(task);
    }
}
