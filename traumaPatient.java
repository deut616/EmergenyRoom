package tracker.model;

import tracker.exceptions.InvalidVitalsException;

/**
 * Represents a trauma patient (accidents, falls, injuries).
 * CONCEPT: Inheritance from Patient, Polymorphism via calculateESILevel()
 *
 * Trauma scoring weights blood pressure heavily —
 * low BP in trauma often means internal bleeding.
 */
public class TraumaPatient extends Patient {

    private final boolean consciousAndAlerts; // is patient responsive?

    public TraumaPatient(String name, int heartRate, int systolicBP,
                         int painLevel, String chiefComplaint,
                         boolean consciousAndAlerts)
            throws InvalidVitalsException {
        super(name, heartRate, systolicBP, painLevel, chiefComplaint);
        this.consciousAndAlerts = consciousAndAlerts;
    }

    /**
     * Trauma ESI logic:
     * - Unconscious OR BP below 90 → ESI 1 (immediate)
     * - HR above 120 OR BP below 100 → ESI 2 (emergent)
     * - High pain (7+) → ESI 3 (urgent)
     * - Moderate pain → ESI 4
     * - Stable, minor injury → ESI 5
     *
     * CONCEPT: Polymorphism — trauma-specific formula
     */
    @Override
    public int calculateESILevel() {
        if (!consciousAndAlerts || getSystolicBP() < 90) return 1;
        if (getHeartRate() > 120 || getSystolicBP() < 100)  return 2;
        if (getPainLevel() >= 7)                             return 3;
        if (getPainLevel() >= 4)                             return 4;
        return 5;
    }

    @Override
    public String getPatientType() {
        return "Trauma";
    }

    /**
     * File format: TRAUMA|name|heartRate|systolicBP|painLevel|chiefComplaint|conscious
     * CONCEPT: File I/O
     */
    @Override
    public String toFileString() {
        return String.format("TRAUMA|%s|%d|%d|%d|%s|%b",
                getName(), getHeartRate(), getSystolicBP(),
                getPainLevel(), getChiefComplaint(), consciousAndAlerts);
    }

    public boolean isConsciousAndAlerts() { return consciousAndAlerts; }
}