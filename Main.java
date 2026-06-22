import tracker.exceptions.InvalidVitalsException;
import tracker.model.*;
import tracker.util.FileManager;
import tracker.util.TriageQueue;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the ER Triage Sorting System.
 * Handles all console input/output and the main program loop.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final TriageQueue queue = new TriageQueue();

    public static void main(String[] args) {

        System.out.println("=========================================");
        System.out.println("       ER TRIAGE SORTING SYSTEM          ");
        System.out.println("=========================================");

        // Load saved queue from file if it exists
        if (FileManager.saveFileExists()) {
            System.out.println("Saved queue found. Loading...");
            try {
                List<Patient> loaded = FileManager.loadQueue();
                for (Patient p : loaded) {
                    queue.addPatient(p);
                }
                System.out.println(loaded.size() + " patient(s) loaded.\n");
            } catch (IOException e) {
                System.out.println("Could not load saved queue: " + e.getMessage());
            }
        }

        // Main menu loop
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    addPatient();
                    break;
                case "2":
                    queue.displayQueue();
                    break;
                case "3":
                    treatNextPatient();
                    break;
                case "4":
                    saveQueue();
                    break;
                case "5":
                    running = false;
                    System.out.println("Saving and exiting...");
                    saveQueue();
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1-5.");
            }
        }

        scanner.close();
    }

    // ── Menu ─────────────────────────────────────────────────────────────────

    private static void printMenu() {
        System.out.println("\n-----------------------------------------");
        System.out.println("  1. Add new patient");
        System.out.println("  2. View triage queue");
        System.out.println("  3. Treat next patient (remove from queue)");
        System.out.println("  4. Save queue to file");
        System.out.println("  5. Exit");
        System.out.print("Choice: ");
    }

    // ── Add Patient ──────────────────────────────────────────────────────────

    private static void addPatient() {
        System.out.println("\nPatient type:");
        System.out.println("  1. Trauma");
        System.out.println("  2. Cardiac");
        System.out.println("  3. Pediatric");
        System.out.print("Choice: ");
        String typeChoice = scanner.nextLine().trim();

        try {
            // Collect shared vitals first
            System.out.print("Patient name: ");
            String name = scanner.nextLine().trim();

            System.out.print("Heart rate (bpm): ");
            int hr = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Systolic blood pressure: ");
            int bp = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Pain level (0-10): ");
            int pain = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Chief complaint: ");
            String complaint = scanner.nextLine().trim();

            // Route to correct subtype
            Patient patient = null;

            switch (typeChoice) {
                case "1":
                    System.out.print("Is patient conscious and alert? (y/n): ");
                    boolean conscious = scanner.nextLine().trim().equalsIgnoreCase("y");
                    patient = new TraumaPatient(name, hr, bp, pain, complaint, conscious);
                    break;

                case "2":
                    System.out.print("Reporting chest pain? (y/n): ");
                    boolean chestPain = scanner.nextLine().trim().equalsIgnoreCase("y");
                    System.out.print("Irregular heart rhythm detected? (y/n): ");
                    boolean irregular = scanner.nextLine().trim().equalsIgnoreCase("y");
                    patient = new CardiacPatient(name, hr, bp, pain, complaint, chestPain, irregular);
                    break;

                case "3":
                    System.out.print("Patient age (0-17): ");
                    int age = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("High fever present? (y/n): ");
                    boolean fever = scanner.nextLine().trim().equalsIgnoreCase("y");
                    patient = new PediatricPatient(name, hr, bp, pain, complaint, age, fever);
                    break;

                default:
                    System.out.println("Invalid patient type.");
                    return;
            }

            queue.addPatient(patient);
            System.out.println("\nPatient added. Assigned ESI Level: "
                    + patient.calculateESILevel());
            System.out.println(patient.toDisplayString());

        } catch (InvalidVitalsException e) {
            // Custom exception — invalid vitals caught here, not in constructor
            System.out.println("Invalid vitals: " + e.getMessage());
            System.out.println("Patient not added. Please re-enter.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number entered. Patient not added.");
        }
    }

    // ── Treat Next ───────────────────────────────────────────────────────────

    private static void treatNextPatient() {
        Patient next = queue.nextPatient();
        if (next == null) {
            System.out.println("Queue is empty — no patients to treat.");
        } else {
            System.out.println("\nNow treating:");
            System.out.println(next.toDisplayString());
            System.out.println("Remaining in queue: " + queue.size());
        }
    }

    // ── Save ─────────────────────────────────────────────────────────────────

    private static void saveQueue() {
        try {
            FileManager.saveQueue(queue);
        } catch (IOException e) {
            System.out.println("Error saving queue: " + e.getMessage());
        }
    }
}
