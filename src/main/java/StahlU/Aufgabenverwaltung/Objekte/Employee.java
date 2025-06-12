package StahlU.Aufgabenverwaltung.Objekte;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Employee {
    private int employeeId;
    private String firstName;
    private String lastName;

    private transient SimpleStringProperty firstNameProperty;
    private transient SimpleDoubleProperty progressProperty;

    public Employee(int id, String firstName, String lastName) {
        this.employeeId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.firstNameProperty = new SimpleStringProperty(firstName);
        this.progressProperty = new SimpleDoubleProperty(0.0);
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public SimpleStringProperty firstNameProperty() {
        if (firstNameProperty == null) {
            firstNameProperty = new SimpleStringProperty(firstName);
        }
        return firstNameProperty;
    }

    public double getProgress() {
        if (progressProperty == null) {
            progressProperty = new SimpleDoubleProperty(0.0);
        }
        return progressProperty.get();
    }

    public SimpleDoubleProperty progressProperty() {
        if (progressProperty == null) {
            progressProperty = new SimpleDoubleProperty(0.0);
        }
        return progressProperty;
    }

    public void setProgress(double progress) {
        if (progressProperty == null) {
            progressProperty = new SimpleDoubleProperty(0.0);
        }
        progressProperty.set(progress);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Employee that = (Employee) obj;
        return employeeId == that.employeeId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(employeeId);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Employee{");
        sb.append("employeeId=").append(employeeId);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", firstNameProperty=").append(firstNameProperty);
        sb.append(", progressProperty=").append(progressProperty);
        sb.append('}');
        return sb.toString();
    }
}