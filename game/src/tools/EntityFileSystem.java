package tools;

import ecs.entities.Entity;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class EntityFileSystem {
    public static void saveEntities(Set<Entity> entitySet) {
        try (FileOutputStream fileOut = new FileOutputStream("entities");
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(entitySet);
            System.out.println("Entitäten wurden erfolgreich gespeichert.");

        } catch (IOException e) {
            System.out.println("Fehler beim Speichern der Entitäten: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Set<Entity> loadEntities() {
        Set<Entity> entitySet = new HashSet<>();

        try (FileInputStream fileIn = new FileInputStream("entities");
             ObjectInputStream objectIn = new ObjectInputStream(fileIn)) {

            entitySet = (Set<Entity>) objectIn.readObject();
            System.out.println("Entitäten wurden erfolgreich geladen.");

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Fehler beim Laden der Entitäten: " + e.getMessage());
            e.printStackTrace();
        }

        return entitySet;
    }

    public static boolean saveGameExists() {
        File file = new File("entities");
        return file.exists();
    }

    public static void deleteSaveGame() {
        File file = new File("entities");
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("Der Spielstand wurde erfolgreich gelöscht.");
            } else {
                System.out.println("Fehler beim Löschen des Spielstands.");
            }
        } else {
            System.out.println("Es existiert kein Spielstand.");
        }
    }
}
