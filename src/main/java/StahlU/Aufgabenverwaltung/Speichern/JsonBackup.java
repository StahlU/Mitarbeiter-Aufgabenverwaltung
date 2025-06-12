package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Task;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class JsonBackup {
    private static final String FILE_NAME = "backup.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void save(ObservableList<Task> taskList) {
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(taskList, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<Task> load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return FXCollections.observableArrayList();
        }
        try (Reader reader = new FileReader(FILE_NAME)) {
            Type listType = new TypeToken<List<Task>>() {}.getType();
            List<Task> tasks = gson.fromJson(reader, listType);
            return FXCollections.observableArrayList(tasks);
        } catch (IOException e) {
            e.printStackTrace();
            return FXCollections.observableArrayList();
        }
    }
}
