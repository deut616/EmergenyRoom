package tracker.model;

import tracker.exceptions.InvalidVitalsException;

/**
 * Abstract base class for all ER patient types.
 * CONCEPT: Abstract Class + Inheritance
 *
 * Every patient has basic vitals and a name, but each subtype
 * calculates its own ESI priority level differently.
 */
public abstract class Patient {

    private final String name;
    private final int heartRate;      // beats per minute
    private final int systolicBP;     // top blood pressure number
    private final int painLevel;      // 1-10 scale
    private final String chiefComplaint;

    /**
     * Constructor with input validation.
     * CONCEPT: Exception Handling
     */
    public Patient(String name, int heartRate, int systolicBP,
                   int painLevel, String chiefComplaint)
            throws InvalidVitalsException {

        if (name == null || name.isBlank()) {
            throw new InvalidVitalsException("Patient name cannot be empty.");
        }
        if (heartRate < 20 || heartRate > 300) {
            throw new InvalidVitalsException(
                    "Heart rate " + heartRate + " is outside valid range (20-300 bpm).");
        }
        if (systolicBP < 50 || systolicBP > 250) {
            throw new InvalidVitalsException(
                    "Blood pressure " + systolicBP + " is outside valid range (50-250).");
        }
        if (painLevel < 0 || painLevel > 10) {
            throw new InvalidVitalsException(
                    "Pain level must be between 0 and 10. Got: " + painLevel);
        }
        if (chiefComplaint == null || chiefComplaint.isBlank()) {
            throw new InvalidVitalsException("Chief complaint cannot be empty.");
        }

        this.name = name.trim();
        this.heartRate = heartRate;
        this.systolicBP = systolicBP;
        this.painLevel = painLevel;
        this.chiefComplaint = chiefComplaint.trim();
    }

    /**
     * Each subtype must implement its own ESI scoring logic.
     * CONCEPT: Polymorphism — same method call, different behavior per subtype.
     * ESI levels: 1 = most critical, 5 = least critical
     *
     * @return ESI priority level 1-5
     */
    public abstract int calculateESILevel();

    /**
     * Returns a label for the patient type.
     * CONCEPT: Polymorphism
     */
    public abstract String getPatientType();

    /**
     * Pipe-delimited string for saving to file.
     * CONCEPT: File I/O (used by FileManager)
     */
    public abstract String toFileString();

    /**
     * Shared display summary — uses polymorphic calculateESILevel().
     */
    public String toDisplayString() {
        return String.format("[ESI %d] %-20s | Type: %-10s | HR: %d | BP: %d | Pain: %d/10 | %s",
                calculateESILevel(), name, getPatientType(),
                heartRate, systolicBP, painLevel, chiefComplaint);
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getName()           { return name; }
    public int getHeartRate()         { return heartRate; }
    public int getSystolicBP()        { return systolicBP; }
    public int getPainLevel()         { return painLevel; }
    public String getChiefComplaint() { return chiefComplaint; }
}