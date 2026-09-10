package utils;

/**
 * Thrown when the input stream (System.in) is closed or exhausted while the
 * application is still waiting for input — e.g. when input is redirected
 * from a script/test file that runs out of lines instead of the user
 * explicitly choosing "Quit".
 *
 * This lets the top-level application loop catch it, save any unsaved
 * changes, and shut down gracefully instead of calling System.exit()
 * straight from a low-level input helper (which used to skip the save step
 * entirely and silently discard any unsaved add/update/remove operations).
 */
public class InputClosedException extends RuntimeException {
    public InputClosedException() {
        super("Input stream closed.");
    }
}
