package utils;

import java.util.Scanner;

public final class ConsoleUtils {

    private ConsoleUtils() {}

    public static String readLine(Scanner scanner, String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            // Don't exit here — throw so the caller (Application) can save
            // any unsaved changes before shutting down.
            throw new InputClosedException();
        }
        return scanner.nextLine().trim();
    }

    public static String readNonBlank(Scanner scanner, String prompt) {
        String value;
        do {
            value = readLine(scanner, prompt);
            if (value.isEmpty()) System.out.println("This field cannot be empty. Please try again.");
        } while (value.isEmpty());
        return value;
    }

    public static int readInt(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            String raw = readLine(scanner, prompt);
            try {
                int value = Integer.parseInt(raw);
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) {}
            System.out.printf("Please enter a number between %d and %d.%n", min, max);
        }
    }
}
