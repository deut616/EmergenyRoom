package tracker.model;

import tracker.exceptions.InvalidVitalsException;

/**
 * Represents a cardiac patient (chest pain, heart attack, arrhythmia).
 * CONCEPT: Inheritance from Patient, Polymorphism via calculateESILevel()
 *
 * Cardiac scoring weights heart rate irregularity and chest pain heavily.
 */
public class CardiacPatient extends Patient {

    private final boolean chestPain;        // is patient reporting chest pain?
    private final boolean irregularRhythm;  // known arrhythmia detected?

    public CardiacPatient(String name, int heartRate, int systolicBP,
                          int painLevel, String chiefComplaint,
                          boolean chestPain, boolean irregularRhythm)
            throws InvalidVitalsException {
        super(name, heartRate, systolicBP, painLevel, chiefComplaint);
        this.chestPain = chestPain;
        this.irregularRhythm = irregularRhythm;
    }

    /**
     * Cardiac ESI logic:
     * - Chest pain + irregular rhythm → ESI 1 (possible STEMI)
     * - Chest pain OR very high/low HR → ESI 2
     * - Elevated HR with pain → ESI 3
     * - Mild symptoms, stable → ESI 4
     * - Routine follow-up → ESI 5
     *
     * CONCEPT: Polymorphism — cardiac-specific formula, different from Trauma
     */
    @Override
    public int calculateESILevel() {
        if (chestPain && irregularRhythm)                        return 1;
        if (chestPain || getHeartRate() > 150 || getHeartRate() < 40) return 2;
        if (getHeartRate() > 120 && getPainLevel() >= 5)         return 3;
        if (getPainLevel() >= 3)                                 return 4;
        return 5;
    }

    @Override
    public String getPatientType() {
        return "Cardiac";
    }

    /**
     * File format: CARDIAC|name|heartRate|systolicBP|painLevel|chiefComplaint|chestPain|irregular
     * CONCEPT: File I/O
     */
    @Override
    public String toFileString() {
        return String.format("CARDIAC|%s|%d|%d|%d|%s|%b|%b",
                getName(), getHeartRate(), getSystolicBP(),
                getPainLevel(), getChiefComplaint(), chestPain, irregularRhythm);
    }

    public boolean hasChestPain()       { return chestPain; }
    public boolean hasIrregularRhythm() { return irregularRhythm; }
}