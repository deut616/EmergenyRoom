package tracker.util;

import tracker.exceptions.InvalidVitalsException;
import tracker.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and loading the patient queue to/from a text file.
 * CONCEPT: File I/O — reads and writes patients.txt using BufferedReader/Writer.
 * CONCEPT: Exception Handling — catches and reports malformed records without crashing.
 */
public class FileManager {

    private static final String FILE_NAME = "patients.txt";

    /**
     * Saves all patients in the queue to patients.txt.
     * Each patient writes itself via toFileString() (polymorphic).
     *
     * @param queue the triage queue to save
     * @throws IOException if the file cannot be written
     */
    public static void saveQueue(TriageQueue queue) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            writer.write("# ER Triage System — Patient Queue\n");
            writer.write("# Format: TYPE|name|heartRate|systolicBP|painLevel|complaint|...\n\n");

            for (Patient p : queue.getQueue()) {
                // Polymorphic call — each subtype formats itself
                writer.write(p.toFileString());
                writer.newLine();
            }
        }
        System.out.println("Queue saved to " + FILE_NAME);
    }

    /**
     * Loads patients from patients.txt and returns them as a list.
     * Skips malformed records with a warning rather than crashing.
     *
     * @return list of reconstructed Patient objects
     * @throws IOException if the file cannot be read
     */
    public static List<Patient> loadQueue() throws IOException {
        List<Patient> patients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Skip comments and blank lines
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("\\|");

                try {
                    // Route by patient type — reconstruct correct subclass
                    switch (parts[0]) {
                        case "TRAUMA":
                            patients.add(parseTrauma(parts));
                            break;
                        case "CARDIAC":
                            patients.add(parseCardiac(parts));
                            break;
                        case "PEDIATRIC":
                            patients.add(parsePediatric(parts));
                            break;
                        default:
                            System.err.println("Warning: unknown type '" + parts[0] + "' — skipping.");
                    }
                } catch (InvalidVitalsException e) {
                    System.err.println("Warning: invalid record skipped — " + e.getMessage());
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Warning: malformed record skipped — " + line);
                }
            }
        }

        return patients;
    }

    // ── Private parse helpers ────────────────────────────────────────────────

    // TRAUMA|name|heartRate|systolicBP|painLevel|chiefComplaint|conscious
    private static TraumaPatient parseTrauma(String[] p) throws InvalidVitalsException {
        return new TraumaPatient(p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                Integer.parseInt(p[4]), p[5], Boolean.parseBoolean(p[6]));
    }

    // CARDIAC|name|heartRate|systolicBP|painLevel|chiefComplaint|chestPain|irregular
    private static CardiacPatient parseCardiac(String[] p) throws InvalidVitalsException {
        return new CardiacPatient(p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                Integer.parseInt(p[4]), p[5],
                Boolean.parseBoolean(p[6]), Boolean.parseBoolean(p[7]));
    }

    // PEDIATRIC|name|heartRate|systolicBP|painLevel|chiefComplaint|age|highFever
    private static PediatricPatient parsePediatric(String[] p) throws InvalidVitalsException {
        return new PediatricPatient(p[1], Integer.parseInt(p[2]), Integer.parseInt(p[3]),
                Integer.parseInt(p[4]), p[5],
                Integer.parseInt(p[6]), Boolean.parseBoolean(p[7]));
    }

    public static boolean saveFileExists() {
        return new File(FILE_NAME).exists();
    }
}