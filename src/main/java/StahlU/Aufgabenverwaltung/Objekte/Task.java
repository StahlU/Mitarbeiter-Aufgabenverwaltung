package StahlU.Aufgabenverwaltung.Objekte;

import StahlU.Aufgabenverwaltung.Speichern.Context;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Task {

    private int taskId;
    private String title;
    private String description;
    private boolean status;

    private transient SimpleStringProperty titleProperty;
    private transient SimpleStringProperty descriptionProperty;
    private transient SimpleBooleanProperty doneProperty;

    private ObservableList<Employee> employeeList = FXCollections.observableArrayList();

    public Task(String title, String description) {
        this.taskId = -1;
        this.title = title;
        this.description = description;
        this.status = false;
        this.titleProperty = new SimpleStringProperty(title);
        this.descriptionProperty = new SimpleStringProperty(description);
        this.doneProperty = new SimpleBooleanProperty(false);
    }

    public Task(int taskId, String title, String description, boolean status) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.titleProperty = new SimpleStringProperty(title);
        this.descriptionProperty = new SimpleStringProperty(description);
        this.doneProperty = new SimpleBooleanProperty(status);
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isDone() {
        return status;
    }

    public void setStatus(boolean status, Context context) {
        this.status = status;
        if (doneProperty != null) {
            doneProperty.set(status);
            context.updateTaskStatus(this);
        }
    }


    public SimpleBooleanProperty statusProperty() {
        if (doneProperty == null) {
            doneProperty = new SimpleBooleanProperty(status);
        }
        return doneProperty;
    }

    public void setDescription(String description) {
        if (description != null) {
            this.description = description;
            descriptionProperty.set(description);
        }
    }

    public void setTitle(String title) {
        if (title != null) {
            this.title = title;
            this.titleProperty.set(title);
        }
    }

    public ObservableList<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(ObservableList<Employee> list) {
        this.employeeList = list;
    }

    public void addEmployee(Employee employee) {
        if (!employeeList.contains(employee)) {
            employeeList.add(employee);
        }
    }

    public void removeEmployee(Employee employee) {
        employeeList.remove(employee);
    }

    public boolean hasEmployee(Employee employee) {
        if (employee == null) return false;
        return employeeList.stream().anyMatch(e -> e.getEmployeeId() == employee.getEmployeeId());
    }
}
