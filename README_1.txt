README.txt
ER Triage Sorting System
========================

1. PROJECT DESCRIPTION
----------------------
This program simulates an Emergency Room triage sorting system.
When a patient arrives, the system takes their vitals (heart rate,
blood pressure, pain level, and chief complaint) and automatically
assigns them an ESI (Emergency Severity Index) priority level from
1 to 5, where 1 is the most critical. Patients are then sorted in
the queue so medical staff always treat the most critical patient
first. The queue can be saved to a file and reloaded so data
persists between sessions.


2. CONCEPTS USED
----------------
- Abstract Classes
- Polymorphism / Inheritance
- Interfaces (via Exportable behavior in toFileString())
- Exception Handling
- File I/O


3. WHERE THEY ARE USED
----------------------
Abstract Classes:
    Patient.java is the abstract base class. It holds shared fields
    (name, heart rate, blood pressure, pain level, chief complaint)
    and declares calculateESILevel() and getPatientType() as abstract
    methods that every subclass must implement.

Polymorphism / Inheritance:
    TraumaPatient, CardiacPatient, and PediatricPatient all extend
    Patient. Each one overrides calculateESILevel() with its own
    scoring logic. For example, PediatricPatient uses age-adjusted
    heart rate thresholds because a rate of 140 bpm is normal for
    an infant but critical for an adult. TriageQueue sorts patients
    by calling calculateESILevel() on each Patient reference without
    knowing the concrete subtype — that is polymorphism in action.

Exception Handling:
    InvalidVitalsException is a custom exception thrown whenever a
    patient is created with invalid data — for example, a heart rate
    outside the 20-300 bpm range, a pain level above 10, or a blank
    patient name. FileManager also catches NumberFormatException and
    ArrayIndexOutOfBoundsException when reading malformed file records,
    logging a warning and skipping the bad record rather than crashing.

File I/O:
    FileManager.java writes the patient queue to patients.txt using
    BufferedWriter and reads it back using BufferedReader. Each patient
    subclass formats its own record via toFileString(), producing a
    pipe-delimited line that FileManager can parse back into the correct
    subtype on load.


4. CHALLENGES
-------------
The most difficult problem was deciding where and how to handle
exceptions for patient vitals. At first I was catching exceptions
inside the constructor itself, which hid errors from the caller.
I solved this by letting InvalidVitalsException propagate up to
Main.java, where the user can be shown a clear error message and
prompted to re-enter the data without the program crashing.


5. FUTURE IMPROVEMENTS
-----------------------
I would make the patient type system more dynamic. Right now adding
a new patient category like StrokePatient or BurnPatient requires
writing a new Java subclass. A better design would load patient
categories and their triage scoring rules from a configuration file,
so the system could handle the full variety of ER cases without
requiring code changes. The triage logic could also be replaced with
a trained model over time as real patient outcome data becomes
available, though that would likely require moving beyond Java to
a language better suited for machine learning.
