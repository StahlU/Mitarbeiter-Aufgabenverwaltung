package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Task;
import StahlU.Aufgabenverwaltung.Objekte.Employee;
import javafx.collections.ObservableList;

public interface IStorageStrategy {
    ObservableList<Employee> loadEmployees();
    void saveEmployee(String firstName, String lastName);
    void deleteEmployee(Employee employee);

    ObservableList<Task> loadTasks(Employee employee, ObservableList<Employee> globalEmployeeList);

    ObservableList<Task> loadAllTasks(ObservableList<Employee> globalEmployeeList);

    void saveTask(Employee employee, Task task);
    void updateTaskStatus(Task task);
    void deleteTask(Task task);
    void updateTaskData(Task task);

    void removeTask(Employee employee, Task task);
}
