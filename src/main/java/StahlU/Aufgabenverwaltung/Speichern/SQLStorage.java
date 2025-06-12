package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Task;
import StahlU.Aufgabenverwaltung.Objekte.Employee;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SQLStorage implements IStorageStrategy {

    private static String url = "jdbc:sqlite:SQL.db";
    private Map<Integer, Task> taskMap = new HashMap<>();

    @Override
    public ObservableList<Employee> loadEmployees() {
        ObservableList<Employee> employeeList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM mitarbeiter";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Employee employee = new Employee(
                        rs.getInt("mitarbeiter_id"),
                        rs.getString("vorname"),
                        rs.getString("nachname"));
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return employeeList;
    }

    @Override
    public void saveEmployee(String firstName, String lastName) {
        String sql = "INSERT INTO mitarbeiter(vorname, nachname) VALUES(?,?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEmployee(Employee employee) {
        String sqlAssignments = "DELETE FROM mitarbeiter_aufgaben WHERE mitarbeiter_id = ?";
        String sqlEmployee = "DELETE FROM mitarbeiter WHERE mitarbeiter_id = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
            try (PreparedStatement stmtAssignments = conn.prepareStatement(sqlAssignments)) {
                stmtAssignments.setInt(1, employee.getEmployeeId());
                stmtAssignments.executeUpdate();
            }
            try (PreparedStatement stmtEmployee = conn.prepareStatement(sqlEmployee)) {
                stmtEmployee.setInt(1, employee.getEmployeeId());
                stmtEmployee.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ObservableList<Task> loadTasks(Employee employee, ObservableList<Employee> globalEmployeeList) {
        ObservableList<Task> taskList = FXCollections.observableArrayList();
        String sqlTasks = "SELECT * FROM aufgaben";
        String sqlAssignment = "SELECT mitarbeiter_id FROM mitarbeiter_aufgaben WHERE aufgabe_id = ?";

        Map<Integer, Employee> employeeCache = new HashMap<>();
        for (Employee e : globalEmployeeList) {
            employeeCache.put(e.getEmployeeId(), e);
        }
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlTasks)) {
            while (rs.next()) {
                Task task = new Task(
                        rs.getInt("aufgabe_id"),
                        rs.getString("titel"),
                        rs.getString("beschreibung"),
                        rs.getInt("status") == 1);
                ObservableList<Employee> assignedEmployees = FXCollections.observableArrayList();
                try (PreparedStatement pstmt = conn.prepareStatement(sqlAssignment)) {
                    pstmt.setInt(1, task.getTaskId());
                    ResultSet relRs = pstmt.executeQuery();
                    while (relRs.next()) {
                        int eid = relRs.getInt("mitarbeiter_id");
                        Employee e = employeeCache.get(eid);
                        if (e != null) {
                            assignedEmployees.add(e);
                        }
                    }
                }
                task.setEmployeeList(assignedEmployees);
                if (employee == null) {
                    taskList.add(task);
                } else if (task.hasEmployee(employee)) {
                    taskList.add(task);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return taskList;
    }

    @Override
    public ObservableList<Task> loadAllTasks(ObservableList<Employee> globalEmployeeList) {
        // Gibt alle Aufgaben zurück (employee==null)
        return loadTasks(null, globalEmployeeList);
    }

    @Override
    public void saveTask(Employee employee, Task task) {
        String sqlTask = "INSERT INTO aufgaben(titel, beschreibung, status) VALUES(?,?,?)";
        String sqlAssignment = "INSERT INTO mitarbeiter_aufgaben(mitarbeiter_id, aufgabe_id, zugewiesen_am) VALUES(?,?,?)";

        int taskId = -1;
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sqlTask, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setBoolean(3, task.isDone());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    taskId = generatedKeys.getInt(1);
                    task.setTaskId(taskId);
                    taskMap.put(taskId, task);
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        if (taskId == -1) {
            System.out.println("Fehler beim Einfügen der Aufgabe, ID konnte nicht ermittelt werden.");
            return;
        }
        task.addEmployee(employee);
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sqlAssignment)) {
            pstmt.setInt(1, employee.getEmployeeId());
            pstmt.setInt(2, task.getTaskId());
            pstmt.setDate(3, Date.valueOf(java.time.LocalDate.now()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateTaskStatus(Task task) {
        String sql = "UPDATE aufgaben SET status = ? WHERE aufgabe_id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, task.isDone() ? 1 : 0);
            pstmt.setInt(2, task.getTaskId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteTask(Task task) {
        taskMap.remove(task.getTaskId());
        if (task == null) {
            System.out.println("Fehler: Die Aufgabe ist null und kann nicht gelöscht werden.");
            return;
        }
        String sqlAssignment = "DELETE FROM mitarbeiter_aufgaben WHERE aufgabe_id = ?";
        String sqlTask = "DELETE FROM aufgaben WHERE aufgabe_id = ?";

        try (Connection conn = DriverManager.getConnection(url)) {
            conn.createStatement().execute("PRAGMA foreign_keys = ON;");
            try (PreparedStatement stmtAssignment = conn.prepareStatement(sqlAssignment)) {
                stmtAssignment.setInt(1, task.getTaskId());
                stmtAssignment.executeUpdate();
            }
            try (PreparedStatement stmtTask = conn.prepareStatement(sqlTask)) {
                stmtTask.setInt(1, task.getTaskId());
                stmtTask.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateTaskData(Task task) {
        String sql = "UPDATE aufgaben SET titel = ?, beschreibung = ? WHERE aufgabe_id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getDescription());
            pstmt.setInt(3, task.getTaskId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeTask(Employee employee, Task task) {
        String sql = "DELETE FROM mitarbeiter_aufgaben WHERE mitarbeiter_id = ? AND aufgabe_id = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employee.getEmployeeId());
            pstmt.setInt(2, task.getTaskId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        task.getEmployeeList().remove(employee);
    }
}
