import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

// Enum for severity — restricts values to only LOW, MEDIUM, HIGH
// This is better than using plain strings because typos like "HGIH" become impossible
enum Severity {
    LOW, MEDIUM, HIGH
}

// Enum for status — only these three states are allowed
enum Status {
    OPEN, IN_PROGRESS, RESOLVED
}

// This class represents one Bug — all its data lives here
class Bug {
    String id;
    String title;
    String description;
    Severity severity;
    Status status;

    // Constructor — called when a new Bug is created
    Bug(String id, String title, String description, Severity severity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = Status.OPEN; // Every new bug starts as OPEN
    }

    // Prints bug details in a clean readable format
    public void display() {
        System.out.println("---------------------------");
        System.out.println("Bug ID    : " + id);
        System.out.println("Title     : " + title);
        System.out.println("Description: " + description);
        System.out.println("Severity  : " + severity);
        System.out.println("Status    : " + status);
        System.out.println("---------------------------");
    }
}

public class BugTracker {

    // HashMap stores bugs — key is Bug ID (String), value is the Bug object
    // HashMap gives O(1) lookup — finding any bug by ID is instant regardless of how many bugs exist
    static HashMap<String, Bug> bugMap = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    static int bugCounter = 1; // Auto-generates bug IDs like BUG001, BUG002

    public static void main(String[] args) {
        System.out.println("===== Bug Tracker — Inspired by BrickRed Systems =====");

        while (true) {
            System.out.println("\n1. Log New Bug");
            System.out.println("2. View All Bugs");
            System.out.println("3. Search Bug by ID");
            System.out.println("4. Update Bug Status");
            System.out.println("5. Delete Resolved Bug");
            System.out.println("6. Show Summary");
            System.out.println("7. Filter by Severity");
            System.out.println("8. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1: logBug(); break;
                case 2: viewAllBugs(); break;
                case 3: searchBug(); break;
                case 4: updateStatus(); break;
                case 5: deleteResolvedBug(); break;
                case 6: showSummary(); break;
                case 7: filterBySeverity(); break;
                case 8:
                    System.out.println("Exiting Bug Tracker. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Creates a new Bug and puts it into the HashMap
    static void logBug() {
        // Auto-generate Bug ID — BUG001, BUG002, BUG003 and so on
        String id = String.format("BUG%03d", bugCounter++);

        System.out.print("Enter Bug Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Description: ");
        String description = scanner.nextLine();

        System.out.println("Enter Severity (LOW / MEDIUM / HIGH): ");
        String sevInput = scanner.nextLine().toUpperCase();

        Severity severity;
        try {
            severity = Severity.valueOf(sevInput); // Converts string to Enum safely
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid severity. Bug not logged.");
            bugCounter--; // Roll back the counter since bug was not saved
            return;
        }

        // Create Bug object and store in HashMap with ID as key
        Bug newBug = new Bug(id, title, description, severity);
        bugMap.put(id, newBug);
        System.out.println("Bug logged with ID: " + id);
    }

    // Loops through all entries in the HashMap and displays each bug
    static void viewAllBugs() {
        if (bugMap.isEmpty()) {
            System.out.println("No bugs logged.");
            return;
        }
        System.out.println("\n--- All Bugs ---");
        for (Map.Entry<String, Bug> entry : bugMap.entrySet()) {
            entry.getValue().display();
        }
    }

    // HashMap get() retrieves a bug by ID in O(1) time
    static void searchBug() {
        System.out.print("Enter Bug ID: ");
        String id = scanner.nextLine().toUpperCase();

        Bug bug = bugMap.get(id); // Direct lookup — no loop needed
        if (bug != null) {
            bug.display();
        } else {
            System.out.println("Bug not found.");
        }
    }

    // Finds bug by ID and changes its status
    static void updateStatus() {
        System.out.print("Enter Bug ID to update: ");
        String id = scanner.nextLine().toUpperCase();

        Bug bug = bugMap.get(id);
        if (bug == null) {
            System.out.println("Bug not found.");
            return;
        }

        System.out.println("Enter new Status (OPEN / IN_PROGRESS / RESOLVED): ");
        String statusInput = scanner.nextLine().toUpperCase();

        try {
            bug.status = Status.valueOf(statusInput);
            System.out.println("Status updated to: " + bug.status);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status entered.");
        }
    }

    // Only allows deletion if the bug is RESOLVED — open bugs cannot be deleted
    static void deleteResolvedBug() {
        System.out.print("Enter Bug ID to delete: ");
        String id = scanner.nextLine().toUpperCase();

        Bug bug = bugMap.get(id);
        if (bug == null) {
            System.out.println("Bug not found.");
            return;
        }

        if (bug.status != Status.RESOLVED) {
            System.out.println("Cannot delete. Bug is not RESOLVED yet. Update status first.");
            return;
        }

        bugMap.remove(id);
        System.out.println("Bug " + id + " deleted successfully.");
    }

    // Counts total, open, in-progress, and resolved bugs for a dashboard view
    static void showSummary() {
        int total = bugMap.size();
        int open = 0, inProgress = 0, resolved = 0;

        for (Bug bug : bugMap.values()) {
            if (bug.status == Status.OPEN) open++;
            else if (bug.status == Status.IN_PROGRESS) inProgress++;
            else if (bug.status == Status.RESOLVED) resolved++;
        }

        System.out.println("\n===== Bug Summary =====");
        System.out.println("Total Bugs    : " + total);
        System.out.println("Open          : " + open);
        System.out.println("In Progress   : " + inProgress);
        System.out.println("Resolved      : " + resolved);
        System.out.println("=======================");
    }

    // Filters bugs by severity — loops through and prints only matching ones
    static void filterBySeverity() {
        System.out.println("Enter Severity to filter (LOW / MEDIUM / HIGH): ");
        String sevInput = scanner.nextLine().toUpperCase();

        Severity filter;
        try {
            filter = Severity.valueOf(sevInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid severity.");
            return;
        }

        System.out.println("\n--- Bugs with severity: " + filter + " ---");
        boolean found = false;
        for (Bug bug : bugMap.values()) {
            if (bug.severity == filter) {
                bug.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No bugs found with that severity.");
        }
    }
}
