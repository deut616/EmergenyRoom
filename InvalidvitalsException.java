package tracker.exceptions;

/**
 * Thrown when patient vitals fail validation.
 * CONCEPT: Exception Handling — custom exception for domain-specific errors.
 *
 * Examples: heart rate out of range, invalid pain level, blank patient name.
 */
public class InvalidVitalsException extends Exception {

    public InvalidVitalsException(String message) {
        super(message);
    }

    public InvalidVitalsException(String message, Throwable cause) {
        super(message, cause);
    }
}