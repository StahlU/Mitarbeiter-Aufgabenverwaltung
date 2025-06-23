package StahlU.Aufgabenverwaltung.Service;

import StahlU.Aufgabenverwaltung.Controller.SharedData;
import StahlU.Aufgabenverwaltung.Speichern.Context;
import StahlU.Aufgabenverwaltung.Speichern.SQLStorage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import StahlU.Aufgabenverwaltung.Objekte.Employee;
import StahlU.Aufgabenverwaltung.Objekte.Task;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainService {
    Context context;
    public MainService() {
        context = new Context();
        context.setStorageStrategy(new SQLStorage());
    }

    public ObservableList<Employee> getEmployeesForTask(ObservableList<Employee> allEmployees, Task selectedTask) {
        if (selectedTask == null) {
            return FXCollections.observableArrayList();
        }
        List<Employee> filtered = allEmployees.stream()
                .filter(employee -> selectedTask.hasEmployee(employee))
                .toList();
        return FXCollections.observableArrayList(filtered);
    }
    public ObservableList<Task> getTasksForEmployee(ObservableList<Task> allTasks, Employee selectedEmployee) {
        if (selectedEmployee == null) {
            return FXCollections.observableArrayList();
        }
        List<Task> filtered = allTasks.stream()
                .filter(task -> task.hasEmployee(selectedEmployee))
                .toList();
        return FXCollections.observableArrayList(filtered);
    }

    public void updateProgress(ObservableList<Employee> employeeList, ObservableList<Task> allTasks) {
        for (Employee employee : employeeList) {
            long total = allTasks.stream().filter(a -> a.hasEmployee(employee)).count();
            long done = allTasks.stream().filter(a -> a.hasEmployee(employee) && a.isDone()).count();
            double progress = (total == 0) ? 0.0 : (double) done / total;
            employee.setProgress(progress);
        }
    }

    public void addTask(String title, String description, Employee selectedEmployee, ObservableList<Task> allTasks, ObservableList<Employee> employeeList, Context context) {
        Task newTask = new Task(title, description);
        newTask.addEmployee(selectedEmployee);
        SharedData.addTask(newTask);
        context.saveTask(selectedEmployee, title, description);
        allTasks.setAll(context.loadAllTasks(employeeList));
    }

    public boolean removeTask(Task task, Employee selectedEmployee, ObservableList<Task> allTasks, ObservableList<Employee> employeeList, Context context) {
        if (selectedEmployee != null) {
            task.removeEmployee(selectedEmployee);
            SharedData.removeTask(task);
            context.deleteTask(selectedEmployee, task);
            allTasks.setAll(context.loadAllTasks(employeeList));
            return true;
        }
        return false;
    }

    public void editTask(Task task, String newTitle, String newDescription, Context context) {
        task.setDescription(newDescription);
        task.setTitle(newTitle);
        context.updateTaskData(task);
    }

    public boolean addEmployee(String firstName, String lastName, ObservableList<Employee> employeeList, ObservableList<Task> allTasks, Context context) {
        boolean exists = employeeList.stream()
            .anyMatch(m -> m.getFirstName().equalsIgnoreCase(firstName) && m.getLastName().equalsIgnoreCase(lastName));
        if (!exists) {
            SharedData.addEmployee(context.saveEmployee(firstName, lastName));
            employeeList.setAll(context.loadEmployees());
            Collections.sort(employeeList, Comparator.comparingInt(Employee::getEmployeeId));
            allTasks.setAll(context.loadAllTasks(employeeList));
            return true;
        }
        return false;
    }

    public boolean removeEmployee(Employee selectedEmployee, ObservableList<Employee> employeeList, ObservableList<Task> allTasks, Context context) {
        if (selectedEmployee == null) {
            return false;
        }
        SharedData.removeEmployee(selectedEmployee);
        context.deleteEmployee(selectedEmployee);
        employeeList.setAll(context.loadEmployees());
        allTasks.setAll(context.loadAllTasks(employeeList));
        return true;
    }
}
