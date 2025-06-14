package StahlU.Aufgabenverwaltung.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;


public class Generator {


private static final String URL = "jdbc:sqlite:SQL.db";

private static final String[] namen = {
        "Herbert", "Norbert", "Gisela", "Michael", "Frederik", "Max", "Nils", "Mathias", "Jürgen", "Daniel",
        "Anna", "Ben", "Clara", "David", "Eva", "Felix", "Greta", "Hugo", "Ida", "Jonas",
        "Kai", "Lena", "Mia", "Noah", "Olivia", "Paul", "Quentin", "Rita", "Sven", "Tina",
        "Uwe", "Vera", "Walter", "Xenia", "Yannick", "Zoe", "Alina", "Bastian", "Carla", "Dario",
        "Emily", "Finn", "Gina", "Henry", "Isabella", "Jack", "Klara", "Luis", "Mara", "Nico",
        "Oscar", "Pia", "Rafael", "Sophia", "Tom", "Ulrike", "Viktor", "Wilma", "Xander", "Yasmin",
        "Zacharias", "Alicia", "Bruno", "Celine", "Dominik", "Elena", "Fabian", "Gustav", "Helena", "Ingo",
        "Jana", "Kevin", "Laura", "Max", "Nina", "Oliver", "Petra", "René", "Silke", "Theresa",
        "Vincent", "Wanda", "Xaver", "Yvonne", "Zara", "Anton", "Bianca", "Chris", "Diana", "Erik",
        "Franziska", "Gerrit", "Hannah", "Ivo", "Jasmin", "Karl", "Lara", "Matthias", "Nele", "Albert",
        "Bettina", "Carsten", "Denise", "Elias", "Fiona", "Gregor", "Hilda", "Igor", "Jette", "Kilian",
        "Lukas", "Miriam", "Norbert", "Olga", "Philipp", "Rebekka", "Stefan", "Tobias", "Anja", "Bernd",
        "Carmen", "Dirk", "Elisa", "Frank", "Gabi", "Hannes", "Ilona", "Jan", "Kerstin", "Lea",
        "Markus", "Nadja", "Otto", "Rainer", "Sabine", "Timo", "Udo", "Viktoria", "Wolfgang", "Yara",
        "Zach", "Alex", "Boris", "Claudia", "Dieter", "Evelyn", "Florian", "Gerda", "Hans", "Ines",
        "Jürgen", "Katrin", "Leon", "Maria", "Nils", "Olli", "Rita", "Stefan", "Tanja", "Aaron",
        "Britta", "Clemens", "Daniel", "Esther", "Falk", "Günter", "Heike", "Iris", "Jochen", "Karin",
        "Lothar", "Marlene", "Niklas", "Patrick", "Sonja", "Torsten", "Ute", "Volker", "Wilhelm", "Arne",
        "Beate", "Christoph", "Doris", "Erik", "Franz", "Gisela", "Holger", "Ina", "Jörg", "Klaus",
        "Linda", "Martin", "Olaf", "Peter", "Anette", "Bodo", "Claus", "Diana", "Egon", "Frieda",
        "Gero", "Helga", "Irma", "Jens", "Kurt", "Lia", "Milo", "Nora", "Omar", "Paula",
        "Quirin", "Rosa", "Siegfried", "Toni", "Ursula", "Willi", "Yves", "Amelie", "Benno", "Else",
        "Fritz", "Hartmut", "Ilse", "Joachim", "Konrad", "Liesel", "Marius", "Oswald", "Quirin", "Rosa",
        "Sophie", "Thomas", "Ulrich", "Veronika", "Werner", "Zoltan", "Anke", "Bertram", "Christina", "Detlef",
        "Erika", "Friedhelm", "Gabriele", "Horst", "Ilona", "Jutta", "Kurt", "Lena", "Matthias", "Nicole",
        "Petra", "Roland", "Simone", "Tobias", "Ulrike", "Volkmar", "Wilma", "Anja", "Bernd", "Caroline",
        "Dieter", "Eva", "Frank", "Günther", "Heidi", "Ingo", "Jens", "Klaus", "Michael", "Nadine",
        "Oliver", "Sabine", "Thomas", "Ulrike", "Volker", "Werner", "Andreas", "Bettina", "Dirk", "Elke",
        "Franz", "Gertrud", "Helmut", "Irene", "Lars", "Monika", "Philipp", "Renate", "Silvia", "Torsten",
        "Vera", "Armin", "Britta", "Detlef", "Elena", "Falk", "Gundula", "Hartmut", "Ingrid", "Karin",
        "Ludwig", "Marlies", "Pauline", "Rolf", "Susanne", "Thilo", "Urs", "Volkmar", "Zacharias", "Brigitte",
        "Christian", "Erika", "Ferdinand", "Greta", "Johannes", "Lotte", "Maximilian", "Nico", "Quirin", "Ruth",
        "Sebastian", "Ursula", "Wilhelm", "Yannik", "Achim", "Berta", "Claudia", "Dora", "Emil", "Friedrich",
        "Gisela", "Jana", "Karl", "Lukas", "Marina", "Otto", "Paul", "Stefan", "Tina", "Viktor",
        "Alexandra", "Benedikt", "Conny", "Darius", "Elisabeth", "Frieda", "Gerhard", "Hanna", "Isabell", "Joachim",
        "Kristin", "Leonard", "Melanie", "Nadja", "Philipp", "Ricarda", "Sabrina", "Tobias", "Ulrich", "Valerie",
        "Wiebke", "Xaver", "Yvonne", "Zara", "Annalena", "Bastian", "Carsten", "Dominik", "Elli", "Fiona",
        "Gustav", "Helena", "Ilka", "Jürgen", "Katrina", "Lennart", "Marlene", "Nils", "Oliver", "Rainer",
        "Sarah", "Tanja", "Uwe", "Viktoria", "Werner", "Xenia", "Yasmin", "Zoe", "Andreas", "Bianca",
        "Christian", "Dieter", "Elena", "Felix", "Gabriele", "Heinz", "Ines", "Jörg", "Katrin", "Lukas",
        "Marion", "Norbert", "Petra", "Ralf", "Sebastian", "Ulrike", "Volker", "Wilhelm", "Yvonne", "Alfred",
        "Britta", "Christoph", "Ernst", "Franziska", "Günther", "Hildegard", "Ingo", "Julia", "Klaus", "Lorenz",
        "Martina", "Olaf", "Regina", "Stefan", "Thomas", "Verena", "Wolfgang", "Yannik", "Angelika", "Bernd",
        "Cornelia", "Doris", "Erika", "Friedrich", "Gisela", "Hans", "Irmgard", "Jens", "Konrad", "Lothar",
        "Meike", "Nico", "Petra", "Roland", "Simone", "Theodor", "Ute", "Vera", "Wolfram", "Xaver"
};


private static final String[] nachnamen = {
        "Müller", "Schmidt", "Schneider", "Fischer", "Weber", "Meyer", "Wagner", "Becker", "Schulz", "Hoffmann",
        "Koch", "Bauer", "Richter", "Klein", "Wolf", "Schröder", "Neumann", "Schwarz", "Zimmermann", "Braun",
        "Krüger", "Hofmann", "Hartmann", "Lange", "Schmitt", "Werner", "Schmitz", "Krause", "Meier", "Lehmann",
        "Schmid", "Schulze", "Maier", "Köhler", "Herrmann", "König", "Walter", "Mayer", "Huber", "Kaiser",
        "Fuchs", "Peters", "Lang", "Scholz", "Möller", "Weiß", "Jung", "Hahn", "Schubert", "Vogel",
        "Friedrich", "Keller", "Günther", "Frank", "Berger", "Winkler", "Roth", "Beck", "Lorenz", "Baumann",
        "Franke", "Albrecht", "Schuster", "Simon", "Ludwig", "Böhm", "Winter", "Kraus", "Martin", "Schumacher",
        "Krämer", "Vogt", "Stein", "Jäger", "Otto", "Sommer", "Groß", "Seidel", "Heinrich", "Brandt",
        "Haas", "Schreiber", "Graf", "Dietrich", "Ziegler", "Kuhn", "Pohl", "Engel", "Horn", "Busch",
        "Bergmann", "Thomas", "Voigt", "Sauer", "Arnold", "Wolff", "Pfeiffer", "Böttcher", "Fiedler", "Berg",
        "Kühn", "Thiel", "Reuter", "Götz", "Paul", "Schulte", "Schulz", "Lenz", "Schuster", "Kluge",
        "Fritz", "Bock", "Jansen", "Schumacher", "Wiese", "Kraft", "Kunz", "Hermann", "Schütz", "Koch",
        "Bauer", "Brunner", "Petersen", "Bender", "Hennig", "Lorenz", "Bach", "Schilling", "Schwarz", "Hübner"
};

private static final String[] aufgaben = {
        "Kochen", "Putzen", "Einkaufen", "Gartenarbeit", "Lesen", "Schreiben", "Sport", "Musik hören",
        "Malen", "Reisen", "Joggen", "Schwimmen", "Radfahren", "Basteln", "Fotografieren", "Backen",
        "Programmieren", "Zeichnen", "Tanzen", "Spazieren gehen", "Yoga", "Meditieren", "Angeln",
        "Klettern", "Filme schauen", "Videospiele spielen", "Gärtnern", "Tiere füttern", "Lernen", "Entspannen",
        "Stricken", "Häkeln", "Nähen", "Origami", "Brettspiele spielen", "Puzzeln", "Kalligraphie",
        "Töpfern", "Modellbau", "Fahrrad reparieren", "Auto waschen", "Haus dekorieren", "Cocktails mixen",
        "Teezeremonie", "Fremdsprache lernen", "Schach spielen", "Karten spielen", "Astronomie", "Fotobuch erstellen",
        "Bloggen", "Vloggen", "Podcast aufnehmen", "Gitarre spielen", "Klavier spielen", "Singen", "Trommeln",
        "Schlagzeug spielen", "Ukulele spielen", "Violine spielen", "Flöte spielen", "Saxophon spielen", "Trompete spielen",
        "Comics zeichnen", "Geschichten schreiben", "Gedichte schreiben", "Tagebuch führen", "Webdesign", "App-Entwicklung",
        "Datenanalyse", "Mathematische Probleme lösen", "Chemieexperimente", "Physikexperimente", "Vögel beobachten",
        "Insekten sammeln", "Aquaristik", "Terraristik", "Fische angeln", "Pilze sammeln", "Kräuter sammeln", "Geocaching",
        "Camping", "Wandern", "Bergsteigen", "Surfen", "Skifahren", "Snowboarden", "Eislaufen", "Rollschuhlaufen",
        "Kanu fahren", "Segeln", "Tauchen", "Schnorcheln", "Kitesurfen", "Windsurfen", "Paragliding", "Fallschirmspringen",
        "Bungee Jumping", "Rafting", "Höhlenforschung", "Bouldern", "Slacklining", "Parkour", "Trampolinspringen",
        "Jonglieren", "Zaubern", "Improvisationstheater", "Schauspielern", "Ballett", "Hip-Hop tanzen", "Breakdance",
        "Zumba", "Aerobic", "Pilates", "Fitnessstudio besuchen", "Crossfit", "Boxen", "Kickboxen", "Karate",
        "Judo", "Taekwondo", "Aikido", "Kung Fu", "Fechten", "Bogenschießen", "Schießen", "Paintball",
        "Lasertag", "Airsoft", "Softball", "Baseball", "Basketball", "Volleyball", "Handball", "Fußball",
        "Rugby", "American Football", "Tennis", "Tischtennis", "Badminton", "Squash", "Golf", "Minigolf",
        "Bowling", "Billard", "Darts", "Kegeln", "Boccia", "Petanque", "Krocket", "Hockey",
        "Eishockey", "Feldhockey", "Curling", "Eiskunstlauf", "Synchronschwimmen", "Wasserspringen", "Wasserball", "Rudern",
        "Kajakfahren", "Drachenbootfahren", "Segway fahren", "Quad fahren", "Motorrad fahren", "Motocross", "Rennfahren",
        "Kartfahren", "Autocross", "Driften", "Simracing", "Flugsimulation", "Fliegen", "Segelfliegen", "Modellflugzeug fliegen",
        "Drohnen fliegen", "Kite fliegen", "Drachen steigen lassen", "Gärtnern", "Vögel beobachten", "Insekten sammeln",
        "Aquaristik", "Terraristik", "Fische angeln", "Pilze sammeln", "Kräuter sammeln", "Geocaching", "Camping",
        "Wandern", "Bergsteigen", "Surfen", "Skifahren", "Snowboarden", "Eislaufen", "Rollschuhlaufen", "Kanu fahren",
        "Segeln", "Tauchen", "Schnorcheln", "Kitesurfen", "Windsurfen", "Paragliding", "Fallschirmspringen", "Bungee Jumping",
        "Rafting", "Höhlenforschung", "Bouldern", "Slacklining", "Parkour", "Trampolinspringen", "Jonglieren", "Zaubern",
        "Improvisationstheater", "Schauspielern", "Ballett", "Hip-Hop tanzen", "Breakdance", "Zumba", "Aerobic", "Pilates",
        "Fitnessstudio besuchen", "Crossfit", "Boxen", "Kickboxen", "Karate", "Judo", "Taekwondo", "Aikido",
        "Kung Fu", "Fechten", "Bogenschießen", "Schießen", "Paintball", "Lasertag", "Airsoft", "Softball",
        "Baseball", "Basketball", "Volleyball", "Handball", "Fußball", "Rugby", "American Football", "Tennis",
        "Tischtennis", "Badminton", "Squash", "Golf", "Minigolf", "Bowling", "Billard", "Darts",
        "Kegeln", "Boccia", "Petanque", "Krocket", "Hockey", "Eishockey", "Feldhockey", "Curling",
        "Eiskunstlauf", "Synchronschwimmen", "Wasserspringen", "Wasserball", "Rudern", "Kajakfahren", "Drachenbootfahren",
        "Segway fahren", "Quad fahren", "Motorrad fahren", "Motocross", "Rennfahren", "Kartfahren", "Autocross", "Driften",
        "Simracing", "Flugsimulation", "Fliegen", "Segelfliegen", "Modellflugzeug fliegen", "Drohnen fliegen", "Kite fliegen",
        "Drachen steigen lassen", "Töpfern", "Modellbau", "Fahrrad reparieren", "Auto waschen", "Haus dekorieren",
        "Cocktails mixen", "Teezeremonie", "Fremdsprache lernen", "Schach spielen", "Karten spielen", "Astronomie",
        "Fotobuch erstellen", "Bloggen", "Vloggen", "Podcast aufnehmen", "Gitarre spielen", "Klavier spielen", "Singen",
        "Trommeln", "Schlagzeug spielen", "Ukulele spielen", "Violine spielen", "Flöte spielen", "Saxophon spielen",
        "Trompete spielen", "Comics zeichnen", "Geschichten schreiben", "Gedichte schreiben", "Tagebuch führen", "Webdesign",
        "App-Entwicklung", "Datenanalyse", "Mathematische Probleme lösen", "Chemieexperimente", "Physikexperimente"
};


public static void fillTables(int numberOfEmployees, int numberOfTasks) {
    Random random = new Random();

    try (Connection conn = DriverManager.getConnection(URL)) {
        conn.setAutoCommit(false);


        String mitarbeiterSql = "INSERT INTO mitarbeiter(vorname, nachname) VALUES(?, ?)";
        try (PreparedStatement mitarbeiterStmt = conn.prepareStatement(mitarbeiterSql)) {
            for (int i = 0; i < numberOfEmployees; i++) {
                mitarbeiterStmt.setString(1, namen[random.nextInt(namen.length)]);
                mitarbeiterStmt.setString(2, nachnamen[random.nextInt(nachnamen.length)]);
                mitarbeiterStmt.executeUpdate();
            }
        }


        String aufgabenSql = "INSERT INTO aufgaben(titel, beschreibung, status) VALUES(?, ?, ?)";
        try (PreparedStatement aufgabenStmt = conn.prepareStatement(aufgabenSql)) {
            for (int i = 0; i < numberOfTasks; i++) {
                aufgabenStmt.setString(1, aufgaben[random.nextInt(aufgaben.length)]);
                aufgabenStmt.setString(2, "Beschreibung " + (i + 1));
                aufgabenStmt.setInt(3, random.nextBoolean() ? 1 : 0);
                aufgabenStmt.executeUpdate();
            }
        }

        String mitarbeiterAufgabenSql = "INSERT INTO mitarbeiter_aufgaben(mitarbeiter_id, aufgabe_id, zugewiesen_am) VALUES(?, ?, date('now'))";
        try (PreparedStatement mitarbeiterAufgabenStmt = conn.prepareStatement(mitarbeiterAufgabenSql)) {
            for (int employeeId = 1; employeeId <= numberOfEmployees; employeeId++) {
                Set<Integer> assignedTasks = new HashSet<>();
                int tasksToAssign = random.nextInt(numberOfTasks) + 1;
                for (int j = 0; j < tasksToAssign; j++) {
                    int taskId;
                    do {
                        taskId = random.nextInt(numberOfTasks) + 1;
                    } while (assignedTasks.contains(taskId));
                    assignedTasks.add(taskId);

                    mitarbeiterAufgabenStmt.setInt(1, employeeId);
                    mitarbeiterAufgabenStmt.setInt(2, taskId);
                    mitarbeiterAufgabenStmt.executeUpdate();
                }
            }
        }

        conn.commit();
        System.out.println("Tabellen erfolgreich gefüllt mit " + numberOfEmployees + " Mitarbeitern und " + numberOfTasks + " Aufgaben.");
    } catch (SQLException e) {
        System.out.println("Fehler beim Befüllen der Tabellen: " + e.getMessage());
    }
}


}