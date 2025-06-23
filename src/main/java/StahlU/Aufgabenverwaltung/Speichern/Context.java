package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Task;
import StahlU.Aufgabenverwaltung.Objekte.Employee;
import StahlU.Aufgabenverwaltung.Controller.SharedData;
import javafx.collections.ObservableList;

public class Context {

    private IStorageStrategy storageStrategy;

    public void setStorageStrategy(IStorageStrategy storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

    public ObservableList<Employee> loadEmployees() {
        return this.storageStrategy.loadEmployees();
    }

    public Employee saveEmployee(String firstName, String lastName) {
        Employee employee = new Employee(-1, firstName, lastName);
        this.storageStrategy.saveEmployee(firstName, lastName);
        SharedData.addEmployee(employee);
        saveJsonAsync();
        return employee;
    }

    public void deleteEmployee(Employee employee) {
        this.storageStrategy.deleteEmployee(employee);
        SharedData.removeEmployee(employee);
        saveJsonAsync();
    }

    public ObservableList<Task> loadTasks(Employee employee, ObservableList<Employee> globalEmployeeList) {
        return this.storageStrategy.loadTasks(employee, globalEmployeeList);
    }

    public ObservableList<Task> loadAllTasks(ObservableList<Employee> globalEmployeeList) {
        return this.storageStrategy.loadAllTasks(globalEmployeeList);
    }

    public void saveTask(Employee employee, String title, String description) {
        Task task = new Task(title, description);
        task.addEmployee(employee);
        this.storageStrategy.saveTask(employee, task);
        SharedData.addTask(task);
        saveJsonAsync();
    }
    public void saveTask(ObservableList<Employee> employees, String title, String description) {
        Task task = new Task(title, description);
        for (Employee employee : employees) {
            task.addEmployee(employee);
        }
        this.storageStrategy.createTaskWithEmployees(employees, task);
        SharedData.addTask(task);
        saveJsonAsync();
    }

    public void updateTaskStatus(Task task) {
        this.storageStrategy.updateTaskStatus(task);
        saveJsonAsync();
    }

    public void deleteTask(Employee employee, Task task) {
        this.storageStrategy.removeTask(employee, task);
        if (task.getEmployeeList().isEmpty()) {
            this.storageStrategy.deleteTask(task);
            SharedData.removeTask(task);
        }
        saveJsonAsync();
    }

    public void saveJson() {
        ObservableList<Employee> employeeList = loadEmployees();
        ObservableList<Task> taskList = loadAllTasks(employeeList);
        JsonBackup.save(taskList);
    }

    public void saveJsonAsync() {
        new Thread(this::saveJson).start();
    }

    public void updateTaskData(Task task) {
        this.storageStrategy.updateTaskData(task);
        saveJsonAsync();
    }
}
