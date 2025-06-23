package StahlU.Aufgabenverwaltung.Objekte;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

public class EmployeeCell extends ListCell<Employee> {


    private Label nameLabel = new Label();
    @Override
    public void updateItem(Employee employee, boolean empty) {
        super.updateItem(employee, empty);
        if (empty || employee == null) {
            setGraphic(null);
        } else {
            nameLabel.setText(employee.getEmployeeId()+" "+ employee.getFirstName() + " " + employee.getLastName());
            setGraphic(nameLabel);

        }
    }
}
