package tracker.util;

import tracker.model.Patient;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Manages the ER patient queue, sorted by ESI priority level.
 * CONCEPT: Polymorphism — calls calculateESILevel() on Patient references
 * without knowing the concrete subtype. Each subtype returns its own score.
 */
public class TriageQueue {

    private final List<Patient> queue;

    public TriageQueue() {
        this.queue = new ArrayList<>();
    }

    /**
     * Adds a patient and re-sorts the queue by ESI level (1 = most critical first).
     * CONCEPT: Polymorphism — comparator calls the abstract calculateESILevel()
     * which dispatches to TraumaPatient, CardiacPatient, or PediatricPatient logic.
     */
    public void addPatient(Patient patient) {
        queue.add(patient);
        queue.sort(Comparator.comparingInt(Patient::calculateESILevel));
    }

    /**
     * Removes and returns the highest priority patient (ESI 1 first).
     * @return next patient, or null if queue is empty
     */
    public Patient nextPatient() {
        if (queue.isEmpty()) return null;
        return queue.remove(0);
    }

    /**
     * Prints the full sorted queue to the console.
     */
    public void displayQueue() {
        if (queue.isEmpty()) {
            System.out.println("  Queue is empty.");
            return;
        }

        System.out.println("┌─────────────────────────────────────────────────────────────────────────┐");
        System.out.println("│                        ER TRIAGE QUEUE                                  │");
        System.out.println("│              ESI 1 = Critical  →  ESI 5 = Non-urgent                   │");
        System.out.println("├─────────────────────────────────────────────────────────────────────────┤");

        for (int i = 0; i < queue.size(); i++) {
            // Polymorphic call — toDisplayString() uses calculateESILevel() internally
            System.out.printf("│ %2d. %s%n", i + 1, queue.get(i).toDisplayString());
        }

        System.out.println("└─────────────────────────────────────────────────────────────────────────┘");
    }

    public List<Patient> getQueue() { return queue; }
    public int size()               { return queue.size(); }
    public boolean isEmpty()        { return queue.isEmpty(); }
}