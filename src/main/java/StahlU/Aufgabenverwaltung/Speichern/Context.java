package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Task;
import StahlU.Aufgabenverwaltung.Objekte.Employee;
import StahlU.Aufgabenverwaltung.Controller.SharedData;
import javafx.collections.ObservableList;

import java.util.Timer;
import java.util.TimerTask;

public class Context {
    private static Context instance;
    private IStorageStrategy storageStrategy;
    private Timer autoSaveTimer;

    private Context() {
        startAutoSave();
        Runtime.getRuntime().addShutdownHook(new Thread(this::saveJson));
    }

    public static Context getInstance() {
        if (instance == null) {
            instance = new Context();
        }
        return instance;
    }

    private void startAutoSave() {
        autoSaveTimer = new Timer(true);
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveJsonAsync();
            }
        }, 5 * 60 * 1000, 5 * 60 * 1000); // alle 5 Minuten
    }

    public void stopAutoSave() {
        if (autoSaveTimer != null) {
            autoSaveTimer.cancel();
        }
    }

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
        return employee;
    }

    public void deleteEmployee(Employee employee) {
        this.storageStrategy.deleteEmployee(employee);
        SharedData.removeEmployee(employee);
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
    }
    public void saveTask(ObservableList<Employee> employees, String title, String description) {
        Task task = new Task(title, description);
        for (Employee employee : employees) {
            task.addEmployee(employee);
        }
        this.storageStrategy.createTaskWithEmployees(employees, task);
        SharedData.addTask(task);
    }

    public void updateTaskStatus(Task task) {
        this.storageStrategy.updateTaskStatus(task);
    }

    public void deleteTask(Employee employee, Task task) {
        this.storageStrategy.removeTask(employee, task);
        if (task.getEmployeeList().isEmpty()) {
            this.storageStrategy.deleteTask(task);
            SharedData.removeTask(task);
        }
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
    }
}
