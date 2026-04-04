package boundary;

import adt.SortedArrayList;
import control.FacilityMaintenance;
import entity.Facility;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Boundary (UI) layer for the Facility module.
 *
 * <p><b>Facility ID format (auto-generated, never typed by user):</b>
 * <ul>
 *   <li>C001, C002, … — Cyber Centre</li>
 *   <li>L001, L002, … — Library (Discussion Rooms &amp; Individual Study Rooms)</li>
 *   <li>S001, S002, … — Sports Facilities</li>
 *   <li>O001, O002, … — Other (miscellaneous)</li>
 * </ul>
 *
 * <p><b>Facility name selection flow:</b><br>
 * The top-level menu shows four direct options — no sub-menus at any level.
 * Choosing "Other" (option 4) immediately assigns the facility name as "Other",
 * auto-sets the room type to "Other", and generates an O-prefixed ID (O001, O002, …).
 * No further input is required for the "Other" category.
 *
 * <p><b>Room type selection:</b><br>
 * Room types are <em>always</em> filtered by the currently selected facility
 * name. Choosing "Cyber Centre" will only ever present Cyber room types;
 * choosing "Sports Facilities" will only present sports court types.
 * This prevents nonsensical combinations such as a "Pickleball Court"
 * inside a Cyber Centre.
 *
 * <p><b>Structural conventions (consistent with UserMaintenanceUI):</b>
 * <ul>
 *   <li>One {@link Scanner} shared across the entire instance.</li>
 *   <li>One {@link FacilityMaintenance} control object.</li>
 *   <li>{@link #readInt()} guard prevents {@code InputMismatchException}.</li>
 *   <li>{@link #start()} is the entry point called from Main or a parent menu.</li>
 * </ul>
 *
 */

public class FacilityMaintenanceUI {

    // ====================================================================== //
    //  Fields                                                                 //
    // ====================================================================== //

    private final Scanner            scanner            = new Scanner(System.in);
    private final FacilityMaintenance facilityMaintenance = new FacilityMaintenance();

    // ====================================================================== //
    //  Facility name constants                                                //
    //  These are the exact strings stored in Facility.facilityName and used  //
    //  by FacilityMaintenance.resolvePrefixFor() to derive the ID prefix.    //
    // ====================================================================== //

    /** Stored facilityName for the Cyber Centre category (prefix C). */
    private static final String FNAME_CYBER   = "Cyber Centre";

    /**
     * Stored facilityName for all library rooms (prefix L).
     * Covers both discussion rooms and individual study rooms —
     * the specific room type is captured in the Room Type field.
     */
    private static final String FNAME_LIBRARY = "Library";

    /** Stored facilityName for sports courts (prefix S). */
    private static final String FNAME_SPORTS  = "Sports Facilities";

    /**
     * Stored facilityName for miscellaneous / uncategorised facilities (prefix O).
     * Selecting "Other" in the facility name menu immediately uses this string —
     * no free-text prompt is shown. IDs are generated as O001, O002, …
     */
    private static final String FNAME_OTHER   = "Other";

    // ====================================================================== //
    //  Room type option arrays — one per facility name                        //
    //  Each array ends with an "Other" entry that lets the user type a        //
    //  custom room type not covered by the predefined list.                   //
    // ====================================================================== //

    /**
     * Room types available under {@value #FNAME_CYBER}.
     * Cyber Centre rooms are either standard (1 PC) or projector-equipped (2 PCs).
     */
    private static final String[] ROOM_TYPES_CYBER = {
        "Discussion Room (1 PC)",
        "Discussion Room with Projector (2 PCs)",
        "Other (enter manually)"
    };

    /**
     * Room types available under {@value #FNAME_LIBRARY}.
     * Covers discussion rooms, individual study rooms, seminar rooms, and projector rooms.
     */
    private static final String[] ROOM_TYPES_LIBRARY = {
        "Discussion Room (1 PC)",
        "Discussion Room with Projector (2 PCs)",
        "Individual Study Room",
        "Seminar Room",
        "Other (enter manually)"
    };

    /**
     * Room types available under {@value #FNAME_SPORTS}.
     * Each entry corresponds to a distinct court type at TARUMT.
     */
    private static final String[] ROOM_TYPES_SPORTS = {
        "Pickleball Court",
        "Badminton Court",
        "Basketball Court",
        "Other (enter manually)"
    };

    /**
     * Room types available under {@value #FNAME_OTHER}.
     * "Other" is a placeholder category — no room type selection or free-text
     * entry is shown. The room type is automatically set to "Other".
     */
    private static final String[] ROOM_TYPES_OTHER = {
        "Other"
    };

    // ====================================================================== //
    //  Entry point                                                            //
    // ====================================================================== //

    /**
     * Launches the Facility Management main menu loop.
     * Called from {@code Main} or a parent navigation class.
     */
    public void start() {
        int choice;

        do {
            printSectionHeader("FACILITY MANAGEMENT MENU");
            System.out.println("  1. Display All Facilities");
            System.out.println("  2. Add Facility");
            System.out.println("  3. Search Facility");
            System.out.println("  4. Update Facility");
            System.out.println("  5. Delete Facility");
            System.out.println("  0. Exit");
            printDivider();
            System.out.print("  Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> displayAllFacilities();
                case 2 -> addFacility();
                case 3 -> searchFacility();
                case 4 -> updateFacility();
                case 5 -> deleteFacility();
                case 0 -> System.out.println("\n  Exiting Facility Management Module...");
                default -> System.out.println("\n  [!!] Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    // ====================================================================== //
    //  1 — DISPLAY ALL                                                        //
    // ====================================================================== //

    /**
     * Prints the full facility table then offers quick-action sub-options so
     * the user does not have to return to the main menu for simple operations.
     */
    private void displayAllFacilities() {
        int choice;

        do {
            System.out.println(facilityMaintenance.displayAllFacilities());
            System.out.println("  1. Add Facility");
            System.out.println("  2. Update Facility");
            System.out.println("  3. Delete Facility");
            System.out.println("  4. Back");
            System.out.print("  Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> addFacility();
                case 2 -> updateFacility();
                case 3 -> deleteFacility();
                case 4 -> System.out.println("\n  Returning to main menu...");
                default -> System.out.println("\n  [!!] Invalid choice. Please try again.");
            }
        } while (choice != 4);
    }

    // ====================================================================== //
    //  2 — ADD                                                                //
    // ====================================================================== //

    /**
     * Guides the user through a three-step wizard to add a new facility:
     * <ol>
     *   <li>Choose facility name (top-level category → optional sub-category).</li>
     *   <li>Choose room type — list is filtered to only show types valid for
     *       the chosen facility name.</li>
     *   <li>Enter room name / code (free text, e.g. "B002").</li>
     * </ol>
     * A preview is shown before saving and the user may re-enter all details,
     * confirm and save, or cancel entirely.
     *
     * <p>The facility ID is auto-generated from the facility name:
     * Cyber → C prefix, Library/Study → L prefix, Sports → S prefix.
     */
    private void addFacility() {
        int choice;

        do {
            printSectionHeader("ADD FACILITY");

            // ── Step 1: Facility Name ─────────────────────────────────────
            String facilityName = chooseFacilityName();
            if (facilityName == null) {
                System.out.println("\n  Add facility cancelled.");
                return;
            }

            // ── Step 2: Room Type (contextual to the chosen facility name) ──
            String roomType = chooseRoomType(facilityName);
            if (roomType == null) {
                System.out.println("\n  Add facility cancelled.");
                return;
            }

            // ── Step 3: Room Name / Code ───────────────────────────────────
            System.out.print("\n  Enter Room Name (e.g. B002, Pickleball Court 1): ");
            String roomName = scanner.nextLine().trim();

            if (!facilityMaintenance.isValidRoomName(roomName)) {
                System.out.println("  [!!] Room name cannot be empty. Please try again.");
                choice = 2; // force re-entry
                continue;
            }

            // Duplicate check — O(log n) via SortedArrayList.contains() → binarySearch()
            if (facilityMaintenance.facilityExists(facilityName, roomType, roomName)) {
                System.out.println("  [!!] A facility with the same details already exists.");
                choice = 2;
                continue;
            }

            // Auto-generate ID — prefix is derived from facilityName
            String generatedId = facilityMaintenance.generateFacilityId(facilityName);
            Facility preview   = new Facility(generatedId, facilityName, roomType, roomName);

            // ── Preview ────────────────────────────────────────────────────
            System.out.println();
            printDivider();
            System.out.println("  FACILITY PREVIEW");
            printDivider();
            System.out.printf("  %-14s: %s%n", "Facility ID",   generatedId);
            System.out.printf("  %-14s: %s%n", "Facility Name", facilityName);
            System.out.printf("  %-14s: %s%n", "Room Type",     roomType);
            System.out.printf("  %-14s: %s%n", "Room Name",     roomName);
            printDivider();
            System.out.println("  1. Confirm and Save");
            System.out.println("  2. Re-enter Details");
            System.out.println("  3. Cancel");
            System.out.print("  Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> {
                    if (facilityMaintenance.addFacility(preview)) {
                        System.out.println("\n  [OK] Facility added successfully. (ID: " + generatedId + ")");

                        // Ask whether to add another facility immediately
                        int next;
                        do {
                            System.out.println("\n  1. Add Another Facility");
                            System.out.println("  2. Back to Main Menu");
                            System.out.print("  Enter choice: ");
                            next = readInt();
                            if (next != 1 && next != 2) {
                                System.out.println("  [!!] Invalid choice.");
                            }
                        } while (next != 1 && next != 2);

                        if (next == 1) {
                            addFacility(); // tail-recurse for a clean fresh start
                        } else {
                            System.out.println("\n  Returning to main menu...");
                        }
                        return;
                    } else {
                        System.out.println("\n  [!!] Failed to add facility. Please try again.");
                        choice = 2; // let the loop retry
                    }
                }
                case 2 -> System.out.println("\n  Please re-enter all facility details.");
                case 3 -> System.out.println("\n  Add facility cancelled.");
                default -> {
                    System.out.println("\n  [!!] Invalid choice.");
                    choice = 2;
                }
            }
        } while (choice == 2);
    }

    // ====================================================================== //
    //  3 — SEARCH                                                             //
    // ====================================================================== //

    /**
     * Offers three search modes:
     * <ol>
     *   <li>By facility name keyword (partial match, linear scan).</li>
     *   <li>By room name keyword (partial match, linear scan).</li>
     *   <li>By exact facility ID (e.g. {@code L001}, {@code C002}).</li>
     * </ol>
     */
    private void searchFacility() {
        printSectionHeader("SEARCH FACILITY");
        System.out.println("  1. Search by Facility Name");
        System.out.println("  2. Search by Room Name");
        System.out.println("  3. Search by Facility ID (e.g. L001, C002, S003)");
        System.out.println("  0. Back");
        System.out.print("  Enter choice: ");
        int choice = readInt();

        switch (choice) {
            case 1 -> {
                System.out.print("  Enter facility name keyword: ");
                String keyword = scanner.nextLine().trim();
                SortedArrayList<Facility> results =
                        facilityMaintenance.searchByFacilityName(keyword);

                if (results.isEmpty()) {
                    System.out.println("\n  [!!] No facilities found matching: \"" + keyword + "\".");
                } else {
                    System.out.println(facilityMaintenance.displayFacilityList(results));
                }
            }
            case 2 -> {
                System.out.print("  Enter room name keyword: ");
                String keyword = scanner.nextLine().trim();
                SortedArrayList<Facility> results =
                        facilityMaintenance.searchByRoomName(keyword);

                if (results.isEmpty()) {
                    System.out.println("\n  [!!] No facilities found with room name matching: \"" + keyword + "\".");
                } else {
                    System.out.println(facilityMaintenance.displayFacilityList(results));
                }
            }
            case 3 -> {
                System.out.print("  Enter Facility ID (e.g. L001, C002, S003): ");
                String idStr  = scanner.nextLine().trim().toUpperCase();
                Facility found = facilityMaintenance.findByFacilityId(idStr);

                if (found == null) {
                    System.out.println("\n  [!!] No facility found with ID: " + idStr);
                } else {
                    System.out.println();
                    printDivider();
                    System.out.println("  FACILITY RECORD");
                    printDivider();
                    System.out.printf("  %-14s: %s%n", "Facility ID",   found.getFacilityId());
                    System.out.printf("  %-14s: %s%n", "Facility Name", found.getFacilityName());
                    System.out.printf("  %-14s: %s%n", "Room Type",     found.getRoomType());
                    System.out.printf("  %-14s: %s%n", "Room Name",     found.getRoomName());
                    printDivider();
                }
            }
            case 0 -> System.out.println("\n  Returning to main menu...");
            default -> System.out.println("\n  [!!] Invalid choice. Please try again.");
        }
    }

    // ====================================================================== //
    //  4 — UPDATE                                                             //
    // ====================================================================== //

    /**
     * Updates an existing facility record.
     *
     * <p>The user first selects the record by ID, then optionally changes any
     * combination of facility name, room type, and room name. Pressing Enter
     * with an empty input on any field keeps the current value.
     *
     * <p><b>Room type scoping:</b> if the user changes the facility name, the
     * room type menu immediately switches to show only types valid for the
     * <em>new</em> facility name. If the facility name is kept, the room type
     * menu shows types valid for the <em>existing</em> facility name.
     *
     * <p><b>Note on facility ID:</b> the ID prefix (L / C / S) is derived at
     * creation time and is never changed during an update. If you need to move
     * a room to a different category, delete it and add it again.
     */
    private void updateFacility() {
        printSectionHeader("UPDATE FACILITY");
        System.out.println(facilityMaintenance.displayAllFacilities());
        System.out.print("  Enter Facility ID to update (e.g. L001, C002, S003): ");
        String idStr   = scanner.nextLine().trim().toUpperCase();
        Facility existing = facilityMaintenance.findByFacilityId(idStr);

        if (existing == null) {
            System.out.println("\n  [!!] Facility not found.");
            return;
        }

        System.out.println();
        printDivider();
        System.out.println("  CURRENT FACILITY INFORMATION");
        printDivider();
        System.out.printf("  %-14s: %s (unchanged during update)%n",
                "Facility ID", existing.getFacilityId());
        System.out.printf("  %-14s: %s%n", "Facility Name", existing.getFacilityName());
        System.out.printf("  %-14s: %s%n", "Room Type",     existing.getRoomType());
        System.out.printf("  %-14s: %s%n", "Room Name",     existing.getRoomName());
        printDivider();

        // ── Update Facility Name ──────────────────────────────────────────
        System.out.println("\n  Update Facility Name?");
        System.out.println("  0. Keep current (" + existing.getFacilityName() + ")");
        System.out.println("  1. Change to a different facility category");
        System.out.print("  Enter choice: ");
        int nameChoice = readInt();

        String newFacilityName;
        boolean facilityNameChanged;

        if (nameChoice == 1) {
            newFacilityName     = chooseFacilityName();
            facilityNameChanged = true;
            if (newFacilityName == null) {
                System.out.println("\n  Update cancelled.");
                return;
            }
        } else {
            // 0 or anything else → keep current
            newFacilityName     = existing.getFacilityName();
            facilityNameChanged = false;
        }

        // -- Update Room Type (scoped to newFacilityName) --
        // "Other" is a placeholder: room type is always auto-set, no prompt shown.
        // If facility name changed, user MUST pick a new room type.
        // If unchanged, user may press 0 to keep the current room type.
        String newRoomType;

        if (FNAME_OTHER.equalsIgnoreCase(newFacilityName)) {
            // Placeholder category -- room type is locked to "Other", no user input needed
            newRoomType = ROOM_TYPES_OTHER[0];

        } else if (facilityNameChanged) {
            System.out.println("\n  Facility name changed -- please select a new room type:");
            newRoomType = chooseRoomType(newFacilityName);
            if (newRoomType == null) {
                System.out.println("\n  Update cancelled.");
                return;
            }
        } else {
            // Facility name unchanged -- show contextual options but allow "keep current"
            System.out.println("\n  Update Room Type?");
            System.out.println("  0. Keep current (" + existing.getRoomType() + ")");
            String[] types = getRoomTypesFor(newFacilityName);
            for (int i = 0; i < types.length; i++) {
                System.out.println("  " + (i + 1) + ". " + types[i]);
            }
            System.out.print("  Enter choice: ");
            String rawType = scanner.nextLine().trim();

            if (rawType.isEmpty() || rawType.equals("0")) {
                newRoomType = existing.getRoomType();
            } else {
                try {
                    int opt = Integer.parseInt(rawType);
                    if (opt >= 1 && opt < types.length) {
                        // One of the predefined, non-"Other" options
                        newRoomType = types[opt - 1];
                    } else if (opt == types.length) {
                        // Last entry is always "Other (enter manually)"
                        System.out.print("  Enter custom room type: ");
                        newRoomType = scanner.nextLine().trim();
                        if (!facilityMaintenance.isValidRoomType(newRoomType)) {
                            System.out.println("  [!!] Room type cannot be empty. Update cancelled.");
                            return;
                        }
                    } else {
                        System.out.println("  [!!] Invalid option. Update cancelled.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    // Treat raw text as a direct custom entry
                    newRoomType = rawType;
                    if (!facilityMaintenance.isValidRoomType(newRoomType)) {
                        System.out.println("  [!!] Room type cannot be empty. Update cancelled.");
                        return;
                    }
                }
            }
        }

        // ── Update Room Name ──────────────────────────────────────────────
        System.out.print("\n  Enter New Room Name (press Enter to keep \""
                + existing.getRoomName() + "\"): ");
        String newRoomName = scanner.nextLine().trim();
        if (newRoomName.isEmpty()) {
            newRoomName = existing.getRoomName();
        }
        if (!facilityMaintenance.isValidRoomName(newRoomName)) {
            System.out.println("  [!!] Room name cannot be empty. Update cancelled.");
            return;
        }

        // ── Preview & Confirm ─────────────────────────────────────────────
        System.out.println();
        printDivider();
        System.out.println("  UPDATED FACILITY PREVIEW");
        printDivider();
        System.out.printf("  %-14s: %s (unchanged)%n", "Facility ID",   existing.getFacilityId());
        System.out.printf("  %-14s: %s%n",              "Facility Name", newFacilityName);
        System.out.printf("  %-14s: %s%n",              "Room Type",     newRoomType);
        System.out.printf("  %-14s: %s%n",              "Room Name",     newRoomName);
        printDivider();
        System.out.println("  1. Confirm Update");
        System.out.println("  2. Cancel");
        System.out.print("  Enter choice: ");
        int confirm = readInt();

        if (confirm == 1) {
            boolean updated = facilityMaintenance.updateFacility(
                    idStr, newFacilityName, newRoomType, newRoomName);

            if (updated) {
                System.out.println("\n  [OK] Facility updated successfully.");
            } else {
                System.out.println("\n  [!!] Failed to update facility. "
                        + "The new details may conflict with an existing record.");
            }
        } else {
            System.out.println("\n  Update cancelled.");
        }
    }

    // ====================================================================== //
    //  5 — DELETE                                                             //
    // ====================================================================== //

    /**
     * Displays all facilities, prompts for a facility ID, shows the matching
     * record, and asks for confirmation before permanently deleting it.
     */
    private void deleteFacility() {
        printSectionHeader("DELETE FACILITY");
        System.out.println(facilityMaintenance.displayAllFacilities());
        System.out.print("  Enter Facility ID to delete (e.g. L001, C002, S003): ");
        String idStr   = scanner.nextLine().trim().toUpperCase();
        Facility existing = facilityMaintenance.findByFacilityId(idStr);

        if (existing == null) {
            System.out.println("\n  [!!] Facility not found.");
            return;
        }

        System.out.println();
        printDivider();
        System.out.println("  FACILITY RECORD TO DELETE");
        printDivider();
        System.out.printf("  %-14s: %s%n", "Facility ID",   existing.getFacilityId());
        System.out.printf("  %-14s: %s%n", "Facility Name", existing.getFacilityName());
        System.out.printf("  %-14s: %s%n", "Room Type",     existing.getRoomType());
        System.out.printf("  %-14s: %s%n", "Room Name",     existing.getRoomName());
        printDivider();
        System.out.println("  1. Confirm Delete");
        System.out.println("  2. Cancel");
        System.out.print("  Enter choice: ");
        int choice = readInt();

        if (choice == 1) {
            if (facilityMaintenance.deleteFacility(existing.getFacilityId())) {
                System.out.println("\n  [OK] Facility deleted successfully.");
            } else {
                System.out.println("\n  [!!] Failed to delete facility.");
            }
        } else {
            System.out.println("\n  Delete cancelled.");
        }
    }

    // ====================================================================== //
    //  FACILITY NAME SELECTION — flat single-level menu                      //
    // ====================================================================== //

    /**
     * Facility name selection menu — flat, five options, no sub-menus.
     *
     * <pre>
     * ── Select Facility Name ──────────────────────────────────
     *   1. Cyber Centre Discussion Room      (ID prefix: C)
     *   2. Library Discussion Room           (ID prefix: L)
     *   3. Individual Study Room             (ID prefix: L)
     *   4. Sports Facilities                 (ID prefix: S)
     *   5. Other                             (ID prefix: O)
     *   Enter facility type number (0 to exit):
     * </pre>
     *
     * <p>Choosing option 5 ("Other") immediately returns {@link #FNAME_OTHER}
     * with no further prompts. The ID will be assigned as O001, O002, … by
     * {@code FacilityMaintenance.generateFacilityId()}.
     *
     * @return the chosen {@code facilityName} string (one of the five
     *         {@code FNAME_*} constants), or {@code null} if the user exits
     */
    private String chooseFacilityName() {
        System.out.println();
        printDivider();
        System.out.println("  Select Facility Name:");
        printDivider();
        System.out.println("  1. Cyber Centre                      (ID prefix: C)");
        System.out.println("  2. Library                           (ID prefix: L)");
        System.out.println("  3. Sports Facilities                 (ID prefix: S)");
        System.out.println("  4. Other                             (ID prefix: O)");
        printDivider();
        System.out.print("  Enter facility type number (0 to exit): ");
        int choice = readInt();

        switch (choice) {
            case 0 -> { return null; }            // caller interprets null as "cancel"
            case 1 -> { return FNAME_CYBER; }
            case 2 -> { return FNAME_LIBRARY; }
            case 3 -> { return FNAME_SPORTS; }
            case 4 -> { return FNAME_OTHER; }     // immediately assigned, no sub-menu
            default -> {
                System.out.println("\n  [!!] Invalid choice. Please enter 0 – 4.");
                return chooseFacilityName();
            }
        }
    }

    // ====================================================================== //
    //  ROOM TYPE SELECTION — contextual to facilityName                       //
    // ====================================================================== //

    /**
     * Returns the array of valid room type options for the given facility name.
     *
     * <p>This is the single point of truth for the facility-name → room-type
     * mapping. Adding a new room type for an existing category only requires
     * editing the corresponding constant array above; adding a new facility
     * category requires a new constant array and a new case here.
     *
     * @param facilityName one of the five {@code FNAME_*} constants
     * @return a non-null {@code String[]} of room type options, where the last
     *         entry is always {@code "Other (enter manually)"}
     */
    private String[] getRoomTypesFor(String facilityName) {
        if (facilityName == null) return ROOM_TYPES_LIBRARY;  // safe default

        if (facilityName.equalsIgnoreCase(FNAME_CYBER))   return ROOM_TYPES_CYBER;
        if (facilityName.equalsIgnoreCase(FNAME_LIBRARY)) return ROOM_TYPES_LIBRARY;
        if (facilityName.equalsIgnoreCase(FNAME_SPORTS))  return ROOM_TYPES_SPORTS;
        if (facilityName.equalsIgnoreCase(FNAME_OTHER))   return ROOM_TYPES_OTHER;

        // Fallback for any unrecognised name: offer a single "Other" option
        return new String[]{ "Other (enter manually)" };
    }

    /**
     * Room type selection menu, scoped to only show types valid for
     * {@code facilityName}.
     *
     * <p>The last entry in every room type array is always
     * {@code "Other (enter manually)"}; selecting it opens a free-text prompt
     * so that new room types can be recorded without a code change.
     *
     * <p>Choosing {@code 0} cancels and returns {@code null} — the caller
     * treats this as "cancel add/update".
     *
     * <pre>
     * Example output when facilityName = "Sports Facilities":
     *
     * ── Select Room Type ──────────────────────
     *   Facility: Sports Facilities
     * ──────────────────────────────────────────
     *   1. Pickleball Court
     *   2. Badminton Court
     *   3. Basketball Court
     *   4. Other (enter manually)
     *   0. Cancel
     *   Enter choice:
     * </pre>
     *
     * @param facilityName the facility name selected in the previous step;
     *                     determines which room types are displayed
     * @return the chosen room type string, or {@code null} if cancelled
     */
    private String chooseRoomType(String facilityName) {
        String[] types = getRoomTypesFor(facilityName);

        System.out.println();
        printDivider();
        System.out.println("  Select Room Type");
        System.out.println("  Facility: " + facilityName);
        printDivider();
        for (int i = 0; i < types.length; i++) {
            System.out.println("  " + (i + 1) + ". " + types[i]);
        }
        System.out.println("  0. Cancel");
        printDivider();
        System.out.print("  Enter choice: ");
        int choice = readInt();

        if (choice == 0) return null;

        if (choice >= 1 && choice < types.length) {
            // One of the predefined, non-"Other" room types
            return types[choice - 1];
        }

        if (choice == types.length) {
            // "Other (enter manually)" — the last entry in every types array
            System.out.print("  Enter custom room type: ");
            String custom = scanner.nextLine().trim();
            if (!facilityMaintenance.isValidRoomType(custom)) {
                System.out.println("  [!!] Room type cannot be empty.");
                return chooseRoomType(facilityName); // retry
            }
            return custom;
        }

        // Out-of-range number
        System.out.println("  [!!] Invalid choice. Please try again.");
        return chooseRoomType(facilityName); // retry
    }

    // ====================================================================== //
    //  DISPLAY HELPERS                                                        //
    // ====================================================================== //

    /**
     * Prints a section header with consistent formatting.
     *
     * @param title the section title, rendered in upper-case
     */
    private void printSectionHeader(String title) {
        System.out.println();
        System.out.println("  ============================================================");
        System.out.println("    " + title);
        System.out.println("  ============================================================");
    }

    /** Prints a lightweight horizontal divider. */
    private void printDivider() {
        System.out.println("  ----------------------------------------------------------");
    }

    // ====================================================================== //
    //  INPUT HELPER                                                           //
    // ====================================================================== //

    /**
     * Safe integer reader — loops until the user enters a valid integer.
     * Prevents {@code InputMismatchException} from {@code Scanner.nextInt()}.
     * Uses the same pattern as {@code UserMaintenanceUI.readInt()}.
     *
     * @return the valid integer entered by the user
     */
    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("  [!!] Invalid input. Please enter a number: ");
            scanner.nextLine();
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // flush trailing newline from the input buffer
        return value;
    }
}