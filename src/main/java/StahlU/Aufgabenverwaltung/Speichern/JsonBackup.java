package StahlU.Aufgabenverwaltung.Speichern;

import StahlU.Aufgabenverwaltung.Objekte.Aufgabe;
import StahlU.Aufgabenverwaltung.Objekte.Mitarbeiter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonBackup {
    private static final String FILE_NAME = "mitarbeiter.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ObservableList.class, new ObservableListTypeAdapter())
            .setPrettyPrinting()
            .create();

public static void save(ObservableList<Mitarbeiter> mitarbeiter) {
    try (Writer writer = new FileWriter(FILE_NAME)) {
        gson.toJson(mitarbeiter, writer);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

public static ObservableList<Mitarbeiter> load() {
    File file = new File(FILE_NAME);
    if (!file.exists()) {
        return FXCollections.observableArrayList();
    }

    try (Reader reader = new FileReader(FILE_NAME)) {
        Mitarbeiter[] mitarbeiterArray = gson.fromJson(reader, Mitarbeiter[].class);
        ObservableList<Mitarbeiter> mitarbeiterList = FXCollections.observableArrayList(mitarbeiterArray);


        for (Mitarbeiter mitarbeiter : mitarbeiterList) {
            long erledigteAufgaben = mitarbeiter.getAufgaben().stream()
                .filter(Aufgabe::getStatus)
                .count();
            double fortschritt = mitarbeiter.getAufgaben().isEmpty() ? 0.0 : (double) erledigteAufgaben / mitarbeiter.getAufgaben().size();
            mitarbeiter.fortschrittProperty().set(fortschritt);
        }

        return mitarbeiterList;
    } catch (IOException e) {
        e.printStackTrace();
        return FXCollections.observableArrayList();
    }
}


    private static class ObservableListTypeAdapter implements JsonSerializer<ObservableList<?>>, JsonDeserializer<ObservableList<?>> {
        @Override
        public JsonElement serialize(ObservableList<?> src, Type typeOfSrc, JsonSerializationContext context) {
            return context.serialize(new ArrayList<>(src));
        }

        @Override
        public ObservableList<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            Type elementType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
            ArrayList<?> list = context.deserialize(json, TypeToken.getParameterized(ArrayList.class, elementType).getType());
            return FXCollections.observableArrayList(list);
        }
    }
}