package boundary;

import adt.SortedArrayList;
import control.FacilityMaintenance;
import entity.Facility;
import java.util.Scanner;

/**
 * Boundary (UI) layer for the Facility module.
 *
 * Facility ID format:
 *   L001, L002, … — Library Discussion Room / Individual Study Room
 *   C001, C002, … — CyberCentre Discussion Room
 *   S001, S002, … — Sports Facilities
 *
 * The prefix is resolved automatically from the chosen facility name —
 * the user never types an ID manually.
 *
 * Follows the same structural conventions as UserMaintenanceUI:
 *   - one Scanner shared across the instance
 *   - a single FacilityMaintenance control object
 *   - a readInt() guard to prevent InputMismatchException
 *   - a start() entry point called from Main or a parent menu
 *
 *
 */

public class FacilityMaintenanceUI {

    // ------------------------------------------------------------------ //
    //  Fields                                                               //
    // ------------------------------------------------------------------ //

    private final Scanner scanner = new Scanner(System.in);
    private final FacilityMaintenance facilityMaintenance = new FacilityMaintenance();

    // ------------------------------------------------------------------ //
    //  Predefined option arrays                                             //
    // ------------------------------------------------------------------ //

    /**
     * Predefined facility-name categories.
     * The prefix (L / C / S) is resolved automatically from the chosen name
     * via FacilityMaintenance.resolvePrefixFor().
     * The last entry lets the user type a custom name.
     */
    private static final String[] FACILITY_NAME_OPTIONS = {
        "Cyber Centre Discussion Room",   // → C prefix
        "Library Discussion Room",         // → L prefix
        "Individual Study Room",           // → L prefix
        "Sports Facilities",               // → S prefix
        "Other (enter manually)"
    };

    /**
     * Predefined room-type options.
     * The last entry lets the user type a custom type.
     */
    private static final String[] ROOM_TYPE_OPTIONS = {
        "Discussion Room (1 PC)",
        "Discussion Room with Projector (2 PCs)",
        "Seminar Room",
        "Pickleball Court",
        "Badminton Court",
        "Basketball Court",
        "Other (enter manually)"
    };

    // ------------------------------------------------------------------ //
    //  Entry point                                                          //
    // ------------------------------------------------------------------ //

    /**
     * Main loop for the Facility Management menu.
     * Intended to be called from Main or a parent navigation class.
     */
    public void start() {
        int choice;

        do {
            System.out.println("\n========== FACILITY MANAGEMENT MENU ==========");
            System.out.println("1. Display All Facilities");
            System.out.println("2. Add Facility");
            System.out.println("3. Search Facility");
            System.out.println("4. Update Facility");
            System.out.println("5. Delete Facility");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> displayAllFacilities();
                case 2 -> addFacility();
                case 3 -> searchFacility();
                case 4 -> updateFacility();
                case 5 -> deleteFacility();
                case 0 -> System.out.println("Exiting Facility Management Module...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    // ------------------------------------------------------------------ //
    //  Display                                                              //
    // ------------------------------------------------------------------ //

    /**
     * Prints the full facility table then offers quick-action sub-options.
     */
    private void displayAllFacilities() {
        int choice;

        do {
            System.out.println(facilityMaintenance.displayAllFacilities());
            System.out.println("1. Add Facility");
            System.out.println("2. Update Facility");
            System.out.println("3. Delete Facility");
            System.out.println("4. Back");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> addFacility();
                case 2 -> updateFacility();
                case 3 -> deleteFacility();
                case 4 -> System.out.println("Returning to main menu...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 4);
    }

    // ------------------------------------------------------------------ //
    //  Add                                                                  //
    // ------------------------------------------------------------------ //

    /**
     * Guides the user through entering a new facility record.
     *
     * The facility ID is auto-generated from the chosen facility name:
     *   Cyber  → C001, C002, …
     *   Library / Study → L001, L002, …
     *   Sports → S001, S002, …
     */
    private void addFacility() {
        int choice;

        do {
            System.out.println("\n============ ADD FACILITY ===========");

            // --- Facility Name (determines ID prefix) ---
            String facilityName = chooseFacilityName();
            if (facilityName == null) {
                System.out.println("Add facility cancelled.");
                return;
            }

            // --- Room Type ---
            String roomType = chooseRoomType();
            if (roomType == null) {
                System.out.println("Add facility cancelled.");
                return;
            }

            // --- Room Name ---
            System.out.print("Enter Room Name (e.g. B002, Pickleball Court 1): ");
            String roomName = scanner.nextLine().trim();

            if (!facilityMaintenance.isValidRoomName(roomName)) {
                System.out.println("Room name cannot be empty. Please try again.");
                choice = 2;
                continue;
            }

            // Duplicate check — binary search via SortedArrayList.contains()
            if (facilityMaintenance.facilityExists(facilityName, roomType, roomName)) {
                System.out.println("A facility with the same details already exists.");
                choice = 2;
                continue;
            }

            // Auto-generate ID based on facility name prefix (L / C / S)
            String generatedId = facilityMaintenance.generateFacilityId(facilityName);
            Facility preview   = new Facility(generatedId, facilityName, roomType, roomName);

            System.out.println("\n----------- FACILITY PREVIEW -----------");
            System.out.println("Facility ID  : " + generatedId);
            System.out.println("Facility Name: " + facilityName);
            System.out.println("Room Type    : " + roomType);
            System.out.println("Room Name    : " + roomName);
            System.out.println("----------------------------------------");
            System.out.println("1. Confirm and Save");
            System.out.println("2. Re-enter Details");
            System.out.println("3. Cancel");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> {
                    boolean added = facilityMaintenance.addFacility(preview);
                    if (added) {
                        System.out.println("Facility added successfully. (ID: " + generatedId + ")");

                        int next;
                        do {
                            System.out.println("\n1. Add Another Facility");
                            System.out.println("2. Back");
                            System.out.print("Enter choice: ");
                            next = readInt();
                            if (next != 1 && next != 2) {
                                System.out.println("Invalid choice.");
                            }
                        } while (next != 1 && next != 2);

                        if (next == 1) {
                            addFacility();
                        } else {
                            System.out.println("Returning to main menu...");
                        }
                        return;
                    } else {
                        System.out.println("Failed to add facility. Please try again.");
                        choice = 2;
                    }
                }
                case 2 -> System.out.println("Please re-enter facility details.");
                case 3 -> System.out.println("Add facility cancelled.");
                default -> {
                    System.out.println("Invalid choice.");
                    choice = 2;
                }
            }
        } while (choice == 2);
    }

    // ------------------------------------------------------------------ //
    //  Search                                                               //
    // ------------------------------------------------------------------ //

    /**
     * Lets the user search by facility name keyword, room name keyword,
     * or exact facility ID (e.g. L001, C002, S003).
     */
    private void searchFacility() {
        System.out.println("\n============ SEARCH FACILITY ===========");
        System.out.println("1. Search by Facility Name");
        System.out.println("2. Search by Room Name");
        System.out.println("3. Search by Facility ID (e.g. L001, C002, S003)");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");
        int choice = readInt();

        switch (choice) {
            case 1 -> {
                System.out.print("Enter facility name keyword: ");
                String keyword = scanner.nextLine().trim();
                SortedArrayList<Facility> results =
                        facilityMaintenance.searchByFacilityName(keyword);

                if (results.isEmpty()) {
                    System.out.println("No facilities found matching: " + keyword);
                } else {
                    System.out.println("Search results for \"" + keyword + "\":");
                    System.out.println(facilityMaintenance.displayFacilityList(results));
                }
            }
            case 2 -> {
                System.out.print("Enter room name keyword: ");
                String keyword = scanner.nextLine().trim();
                SortedArrayList<Facility> results =
                        facilityMaintenance.searchByRoomName(keyword);

                if (results.isEmpty()) {
                    System.out.println("No facilities found matching room name: " + keyword);
                } else {
                    System.out.println("Search results for room \"" + keyword + "\":");
                    System.out.println(facilityMaintenance.displayFacilityList(results));
                }
            }
            case 3 -> {
                System.out.print("Enter Facility ID (e.g. L001, C002, S003): ");
                String idStr = scanner.nextLine().trim().toUpperCase();
                Facility found = facilityMaintenance.findByFacilityId(idStr);

                if (found == null) {
                    System.out.println("No facility found with ID: " + idStr);
                } else {
                    System.out.println("\n----------- FACILITY RECORD -----------");
                    System.out.println("Facility ID  : " + found.getFacilityId());
                    System.out.println("Facility Name: " + found.getFacilityName());
                    System.out.println("Room Type    : " + found.getRoomType());
                    System.out.println("Room Name    : " + found.getRoomName());
                    System.out.println("---------------------------------------");
                }
            }
            case 0 -> System.out.println("Returning to main menu...");
            default -> System.out.println("Invalid choice. Please try again.");
        }
    }

    // ------------------------------------------------------------------ //
    //  Update                                                               //
    // ------------------------------------------------------------------ //

    /**
     * Prompts for a facility ID, displays the current record, then allows
     * field-by-field updates with press-Enter-to-keep-current support.
     *
     * Note: the facility ID itself is never changed during an update — only
     * the name fields can change.
     */
    private void updateFacility() {
        System.out.println("\n=========== UPDATE FACILITY ============");
        System.out.println(facilityMaintenance.displayAllFacilities());
        System.out.print("Enter Facility ID to update (e.g. L001, C002, S003): ");
        String idStr = scanner.nextLine().trim().toUpperCase();

        Facility existing = facilityMaintenance.findByFacilityId(idStr);
        if (existing == null) {
            System.out.println("Facility not found.");
            return;
        }

        System.out.println("\nCurrent Facility Information");
        System.out.println("Facility ID  : " + existing.getFacilityId());
        System.out.println("Facility Name: " + existing.getFacilityName());
        System.out.println("Room Type    : " + existing.getRoomType());
        System.out.println("Room Name    : " + existing.getRoomName());

        // --- Facility Name ---
        System.out.println("\nUpdate Facility Name:");
        System.out.println("0. Keep current (" + existing.getFacilityName() + ")");
        for (int i = 0; i < FACILITY_NAME_OPTIONS.length; i++) {
            System.out.println((i + 1) + ". " + FACILITY_NAME_OPTIONS[i]);
        }
        System.out.print("Enter choice: ");
        String newFacilityName;
        String rawInput = scanner.nextLine().trim();

        if (rawInput.isEmpty() || rawInput.equals("0")) {
            newFacilityName = existing.getFacilityName();
        } else {
            try {
                int opt = Integer.parseInt(rawInput);
                if (opt >= 1 && opt < FACILITY_NAME_OPTIONS.length) {
                    newFacilityName = FACILITY_NAME_OPTIONS[opt - 1];
                } else if (opt == FACILITY_NAME_OPTIONS.length) {
                    System.out.print("Enter custom facility name: ");
                    newFacilityName = scanner.nextLine().trim();
                    if (!facilityMaintenance.isValidFacilityName(newFacilityName)) {
                        System.out.println("Invalid facility name. Update cancelled.");
                        return;
                    }
                } else {
                    System.out.println("Invalid option. Update cancelled.");
                    return;
                }
            } catch (NumberFormatException e) {
                newFacilityName = rawInput;
                if (!facilityMaintenance.isValidFacilityName(newFacilityName)) {
                    System.out.println("Invalid facility name. Update cancelled.");
                    return;
                }
            }
        }

        // --- Room Type ---
        System.out.println("\nUpdate Room Type:");
        System.out.println("0. Keep current (" + existing.getRoomType() + ")");
        for (int i = 0; i < ROOM_TYPE_OPTIONS.length; i++) {
            System.out.println((i + 1) + ". " + ROOM_TYPE_OPTIONS[i]);
        }
        System.out.print("Enter choice: ");
        String newRoomType;
        rawInput = scanner.nextLine().trim();

        if (rawInput.isEmpty() || rawInput.equals("0")) {
            newRoomType = existing.getRoomType();
        } else {
            try {
                int opt = Integer.parseInt(rawInput);
                if (opt >= 1 && opt < ROOM_TYPE_OPTIONS.length) {
                    newRoomType = ROOM_TYPE_OPTIONS[opt - 1];
                } else if (opt == ROOM_TYPE_OPTIONS.length) {
                    System.out.print("Enter custom room type: ");
                    newRoomType = scanner.nextLine().trim();
                    if (!facilityMaintenance.isValidRoomType(newRoomType)) {
                        System.out.println("Invalid room type. Update cancelled.");
                        return;
                    }
                } else {
                    System.out.println("Invalid option. Update cancelled.");
                    return;
                }
            } catch (NumberFormatException e) {
                newRoomType = rawInput;
                if (!facilityMaintenance.isValidRoomType(newRoomType)) {
                    System.out.println("Invalid room type. Update cancelled.");
                    return;
                }
            }
        }

        // --- Room Name ---
        System.out.print("\nEnter New Room Name (press Enter to keep \""
                + existing.getRoomName() + "\"): ");
        String newRoomName = scanner.nextLine().trim();
        if (newRoomName.isEmpty()) {
            newRoomName = existing.getRoomName();
        }
        if (!facilityMaintenance.isValidRoomName(newRoomName)) {
            System.out.println("Room name cannot be empty. Update cancelled.");
            return;
        }

        // --- Preview & Confirm ---
        System.out.println("\n----------- UPDATED FACILITY PREVIEW -----------");
        System.out.println("Facility ID  : " + existing.getFacilityId() + " (unchanged)");
        System.out.println("Facility Name: " + newFacilityName);
        System.out.println("Room Type    : " + newRoomType);
        System.out.println("Room Name    : " + newRoomName);
        System.out.println("------------------------------------------------");
        System.out.println("1. Confirm Update");
        System.out.println("2. Cancel");
        System.out.print("Enter choice: ");
        int confirm = readInt();

        if (confirm == 1) {
            boolean updated = facilityMaintenance.updateFacility(
                    idStr, newFacilityName, newRoomType, newRoomName);

            if (updated) {
                System.out.println("Facility updated successfully.");
            } else {
                System.out.println("Failed to update facility. "
                        + "The new details may conflict with an existing record.");
            }
        } else {
            System.out.println("Update cancelled.");
        }
    }

    // ------------------------------------------------------------------ //
    //  Delete                                                               //
    // ------------------------------------------------------------------ //

    /**
     * Prompts for a facility ID, shows the record, then asks for confirmation
     * before deletion.
     */
    private void deleteFacility() {
        System.out.println("\n=========== DELETE FACILITY ============");
        System.out.println(facilityMaintenance.displayAllFacilities());
        System.out.print("Enter Facility ID to delete (e.g. L001, C002, S003): ");
        String idStr = scanner.nextLine().trim().toUpperCase();

        Facility existing = facilityMaintenance.findByFacilityId(idStr);
        if (existing == null) {
            System.out.println("Facility not found.");
            return;
        }

        System.out.println("\nFacility Record to Delete");
        System.out.println("Facility ID  : " + existing.getFacilityId());
        System.out.println("Facility Name: " + existing.getFacilityName());
        System.out.println("Room Type    : " + existing.getRoomType());
        System.out.println("Room Name    : " + existing.getRoomName());

        System.out.println("\n1. Confirm Delete");
        System.out.println("2. Cancel");
        System.out.print("Enter choice: ");
        int choice = readInt();

        if (choice == 1) {
            boolean deleted = facilityMaintenance.deleteFacility(existing.getFacilityId());
            if (deleted) {
                System.out.println("Facility deleted successfully.");
            } else {
                System.out.println("Failed to delete facility.");
            }
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    // ------------------------------------------------------------------ //
    //  Input helpers                                                        //
    // ------------------------------------------------------------------ //

    /**
     * Prompts the user to pick a facility name from the predefined list
     * or type a custom one. Returns null if the user cancels.
     *
     * The name chosen here directly controls which ID prefix is assigned
     * (L / C / S) via FacilityMaintenance.resolvePrefixFor().
     *
     * @return the chosen facility name, or null if cancelled
     */
    private String chooseFacilityName() {
        System.out.println("\nSelect Facility Name:");
        for (int i = 0; i < FACILITY_NAME_OPTIONS.length; i++) {
            System.out.println((i + 1) + ". " + FACILITY_NAME_OPTIONS[i]);
        }
        System.out.println("0. Cancel");
        System.out.print("Enter choice: ");
        int choice = readInt();

        if (choice == 0) return null;

        if (choice >= 1 && choice < FACILITY_NAME_OPTIONS.length) {
            return FACILITY_NAME_OPTIONS[choice - 1];
        } else if (choice == FACILITY_NAME_OPTIONS.length) {
            System.out.print("Enter custom facility name: ");
            String custom = scanner.nextLine().trim();
            if (!facilityMaintenance.isValidFacilityName(custom)) {
                System.out.println("Facility name cannot be empty.");
                return chooseFacilityName();
            }
            return custom;
        } else {
            System.out.println("Invalid choice. Please try again.");
            return chooseFacilityName();
        }
    }

    /**
     * Prompts the user to pick a room type from the predefined list
     * or type a custom one. Returns null if the user cancels.
     *
     * @return the chosen room type, or null if cancelled
     */
    private String chooseRoomType() {
        System.out.println("\nSelect Room Type:");
        for (int i = 0; i < ROOM_TYPE_OPTIONS.length; i++) {
            System.out.println((i + 1) + ". " + ROOM_TYPE_OPTIONS[i]);
        }
        System.out.println("0. Cancel");
        System.out.print("Enter choice: ");
        int choice = readInt();

        if (choice == 0) return null;

        if (choice >= 1 && choice < ROOM_TYPE_OPTIONS.length) {
            return ROOM_TYPE_OPTIONS[choice - 1];
        } else if (choice == ROOM_TYPE_OPTIONS.length) {
            System.out.print("Enter custom room type: ");
            String custom = scanner.nextLine().trim();
            if (!facilityMaintenance.isValidRoomType(custom)) {
                System.out.println("Room type cannot be empty.");
                return chooseRoomType();
            }
            return custom;
        } else {
            System.out.println("Invalid choice. Please try again.");
            return chooseRoomType();
        }
    }

    /**
     * Safe integer reader — loops until the user enters a valid number.
     * Prevents InputMismatchException from Scanner.nextInt().
     * Identical pattern to readInt() in UserMaintenanceUI.
     *
     * @return the validated integer entered by the user
     */
    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.nextLine();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // flush newline
        return value;
    }
}