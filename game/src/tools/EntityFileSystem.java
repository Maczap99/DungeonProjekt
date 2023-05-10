package tools;

import ecs.entities.Entity;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The EntityFileSystem class provides methods for saving and loading sets of entities to/from a file.
 * It uses serialization to write the entity set to a file and deserialize it back to a set of entities.
 */
public class EntityFileSystem {

    /**
     * Saves a set of entities to a file.
     *
     * @param entitySet The set of entities to save.
     */
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

    /**
     * Loads a set of entities from a file.
     *
     * @return The set of entities loaded from the file.
     */
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

    /**
     * Checks if a save game file exists.
     *
     * @return {@code true} if the save game file exists, {@code false} otherwise.
     */
    public static boolean saveGameExists() {
        File file = new File("entities");
        return file.exists();
    }

    /**
     * Deletes the save game file.
     */
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
