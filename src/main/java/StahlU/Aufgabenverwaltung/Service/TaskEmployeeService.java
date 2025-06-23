package StahlU.Aufgabenverwaltung.Service;

import javafx.collections.ObservableList;
import StahlU.Aufgabenverwaltung.Objekte.Employee;

import java.util.Collections;
import java.util.Comparator;

public class TaskEmployeeService {
    public void addEmployeeToChosenList(Employee employee, ObservableList<Employee> employeeObservableList, ObservableList<Employee> chosenEmployeeObservableList, ObservableList<Employee> employeeSearchList, String searchText) {
        if (employee == null) return;
        employeeObservableList.remove(employee);
        employeeSearchList.remove(employee);
        chosenEmployeeObservableList.add(employee);
    }

    public void removeEmployeeFromChosenList(Employee employee, ObservableList<Employee> employeeObservableList, ObservableList<Employee> chosenEmployeeObservableList, ObservableList<Employee> employeeSearchList, String searchText) {
        if (employee == null) return;
        if (searchText != null && !searchText.isEmpty()) {
            employeeSearchList.add(employee);
        }
        employeeObservableList.add(employee);
        Collections.sort(employeeObservableList, Comparator.comparingInt(Employee::getEmployeeId));
        chosenEmployeeObservableList.remove(employee);
    }
}

