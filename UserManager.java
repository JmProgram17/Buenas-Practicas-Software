import java.io.*;
import java.util.*;

public class UserManager {

    private static final int MAX_USERS = 10;
    private static final List<String> users = new ArrayList<>();
    private static final String LOG_FILE = "log.txt";
    private static final String USERS_FILE = "users.txt";

    // Carga los usuarios desde el archivo si existe
    static {
        loadUsersFromFile();
    }

    public static boolean addUser(String user) {
        if (user == null || user.trim().isEmpty()) {
            log("Attempted to add invalid user: " + user);
            System.out.println("Invalid user name.");
            return false;
        }

        // Convertir a minúsculas para evitar duplicados por diferencias de mayúsculas/minúsculas
        user = user.trim().toLowerCase();

        if (users.contains(user)) {
            log("Attempted to add duplicate user: " + user);
            System.out.println("User already exists: " + user);
            return false;
        }

        if (users.size() >= MAX_USERS) {
            log("Attempted to add user but capacity is full.");
            System.out.println("Cannot add more users: capacity full.");
            return false;
        }

        users.add(user);
        log("User added: " + user);
        System.out.println("User added: " + user);
        saveUsersToFile();  // Guardar los cambios en disco
        return true;
    }

    public static void printUsers() {
        if (users.isEmpty()) {
            System.out.println("No users found.");
        } else {
            for (String user : users) {
                System.out.println(user);
            }
        }
    }

    // Cargar usuarios desde archivo
    private static void loadUsersFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(line.trim().toLowerCase());
            }
        } catch (IOException e) {
            System.out.println("No previous user data found, starting fresh.");
        }
    }

    // Guardar usuarios en archivo
    private static void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (String user : users) {
                writer.write(user);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users to file.");
        }
    }

    // Registrar acciones para auditoría
    private static void log(String message) {
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            logWriter.write(new Date() + " - " + message);
            logWriter.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to log file.");
        }
    }
}
