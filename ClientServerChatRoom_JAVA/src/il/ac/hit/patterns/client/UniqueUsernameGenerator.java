package il.ac.hit.patterns.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * The UniqueUsernameGenerator class is responsible for generating unique guest usernames
 * <p>
 * based on a template "Guest#" and keeping track of used usernames.
 */
public class UniqueUsernameGenerator {

    private final Set<String> usedUsernames;

    private int nextGuestNumber;

    private static final String DEFAULT_USERNAME_FILE_PATH = "last_username.txt";

    /**
     * Constructs a new UniqueUsernameGenerator object.
     * Initializes the set of used usernames and loads the last guest number from a file.
     */
    public UniqueUsernameGenerator() {
        usedUsernames = new HashSet<>();
        nextGuestNumber = loadLastGuestNumber();
    }

    /**
     * Sets the next guest number.
     * @param nextGuestNumber The next guest number to be set.
     */
    public void setNextGuestNumber(int nextGuestNumber) {
        this.nextGuestNumber = nextGuestNumber;
    }

    /**
     * Retrieves the next guest number.
     * @return The next guest number.
     */
    public int getNextGuestNumber() {
        return nextGuestNumber;
    }

    /**
     * Retrieves the set of used usernames.
     * @return The set of used usernames.
     */
    public Set<String> getUsedUsernames() {
        return usedUsernames;
    }


    /**
     * Generates a unique guest username.
     *
     * @return A unique guest username.
     */
    public String generateUsername() {
        String username;
        do {
    // Create the username using the template "Guest#"
            username = "Guest" + getNextGuestNumber();
            setNextGuestNumber(getNextGuestNumber() + 1);
        } while (getUsedUsernames().contains(username));

        getUsedUsernames().add(username);
        saveLastGuestNumber(getNextGuestNumber());
        return username;
    }

    /**
     * Checks if a given username already exists in the set of used usernames.
     *
     * @param username The username to check.
     * @return True if the username exists, false otherwise.
     */
    public boolean usernameExists(String username) {
        return getUsedUsernames().contains(username);
    }

    /**
     * Adds a username to the set of used usernames.
     *
     * @param username The username to add.
     */
    public void addUsername(String username) {
        getUsedUsernames().add(username);
    }

    /**
     * Loads the last guest number from the file.
     * If the file exists and contains a valid number, it returns the number.
     * Otherwise, it creates the file with an initial guest number of 1 and returns 1.
     *
     * @return The last guest number.
     */
    private int loadLastGuestNumber() {
        try {
            if (Files.exists(Paths.get(DEFAULT_USERNAME_FILE_PATH))) {
                String lastGuestNumberString = Files.readString(Paths.get(DEFAULT_USERNAME_FILE_PATH));
                if (!lastGuestNumberString.isEmpty()) {
                    return Integer.parseInt(lastGuestNumberString);
                }
            } else {
                // Create the file with initial guest number 1
                Files.createFile(Paths.get(DEFAULT_USERNAME_FILE_PATH));
                saveLastGuestNumber(1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Saves the last guest number to the file.
     *
     * @param guestNumber The guest number to save.
     */
    private void saveLastGuestNumber(int guestNumber) {
        try {
            Files.writeString(Paths.get(DEFAULT_USERNAME_FILE_PATH), String.valueOf(guestNumber));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}