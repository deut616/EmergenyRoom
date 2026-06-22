package tracker.model;

import tracker.exceptions.InvalidVitalsException;

/**
 * Represents a pediatric patient (children under 18).
 * CONCEPT: Inheritance from Patient, Polymorphism via calculateESILevel()
 *
 * Pediatric scoring uses age-adjusted thresholds —
 * a heart rate of 140 is normal for an infant but critical for an adult.
 */
public class PediatricPatient extends Patient {

    private final int ageYears;         // patient age in years
    private final boolean highFever;    // fever above 38.5°C / 101.3°F?

    public PediatricPatient(String name, int heartRate, int systolicBP,
                            int painLevel, String chiefComplaint,
                            int ageYears, boolean highFever)
            throws InvalidVitalsException {
        super(name, heartRate, systolicBP, painLevel, chiefComplaint);

        // CONCEPT: Exception Handling — additional pediatric validation
        if (ageYears < 0 || ageYears > 17) {
            throw new InvalidVitalsException(
                    "Pediatric patient age must be 0-17. Got: " + ageYears);
        }

        this.ageYears = ageYears;
        this.highFever = highFever;
    }

    /**
     * Pediatric ESI logic uses age-adjusted normal HR ranges.
     * Infants (0-1): normal HR up to 160
     * Children (2-12): normal HR up to 130
     * Teens (13-17): normal HR up to 110
     *
     * CONCEPT: Polymorphism — pediatric-specific formula
     */
    @Override
    public int calculateESILevel() {
        int normalHRLimit = (ageYears <= 1) ? 160 : (ageYears <= 12) ? 130 : 110;

        if (getSystolicBP() < 70 || getHeartRate() > normalHRLimit + 40) return 1;
        if (highFever && getHeartRate() > normalHRLimit)                  return 2;
        if (highFever || getPainLevel() >= 7)                             return 3;
        if (getPainLevel() >= 4)                                          return 4;
        return 5;
    }

    @Override
    public String getPatientType() {
        return "Pediatric";
    }

    /**
     * File format: PEDIATRIC|name|heartRate|systolicBP|painLevel|chiefComplaint|age|highFever
     * CONCEPT: File I/O
     */
    @Override
    public String toFileString() {
        return String.format("PEDIATRIC|%s|%d|%d|%d|%s|%d|%b",
                getName(), getHeartRate(), getSystolicBP(),
                getPainLevel(), getChiefComplaint(), ageYears, highFever);
    }

    public int getAgeYears()   { return ageYears; }
    public boolean hasHighFever() { return highFever; }
}