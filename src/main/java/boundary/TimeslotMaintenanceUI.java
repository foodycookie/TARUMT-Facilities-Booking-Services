package boundary;

import adt.SortedArrayList;
import control.TimeslotMaintenance;
import entity.Facility;
import entity.Timeslot;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Scanner;

/**
 * ECB Role: Boundary
 * - Handles ALL console I/O for the timeslot module.
 * - Communicates ONLY with TimeslotMaintenance (control) and the actor (user/staff).
 * - Zero business logic lives here — it only collects input, delegates to control,
 *   and formats output.
 */
public class TimeslotMaintenanceUI {

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    // Column widths for the slot table
    private static final int COL_NO       = 4;
    private static final int COL_SLOT_ID  = 22;
    private static final int COL_FACILITY = 6;
    private static final int COL_ROOM     = 16;
    private static final int COL_DATE     = 12;
    private static final int COL_START    = 7;
    private static final int COL_END      = 7;
    private static final int COL_STATUS   = 10;
    private static final int COL_BY       = 14;

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    private final TimeslotMaintenance control;
    private final Scanner             scanner;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public TimeslotMaintenanceUI(TimeslotMaintenance control) {
        this.control = control;
        this.scanner = new Scanner(System.in);
    }

    // =========================================================================
    // ENTRY POINT — main menu
    // =========================================================================

    /**
     * Launches the Timeslot Management main menu.
     * Staff-facing: full CRUD + block/unblock.
     *
     * @param facilities Pass-through from the calling module so we can filter
     *                   slots by facilityName / roomType.
     * @param staffId    Logged-in staff member's ID.
     */
    public void start(SortedArrayList<Facility> facilities, String staffId) {
        boolean running = true;

        while (running) {
            printMainMenu();
            int choice = readInt("Enter choice: ", 1, 5);

            switch (choice) {
                case 1 -> menuGenerate(facilities);
                case 2 -> menuView(facilities);
                case 3 -> menuBlock(facilities, staffId);
                case 4 -> menuUnblock(facilities);
                case 5 -> menuDelete(facilities);
                case 0 -> running = false;
            }
        }
    }

    // =========================================================================
    // MAIN MENU PRINT
    // =========================================================================

    private void printMainMenu() {
        printDivider("=", 60);
        System.out.println("         TIMESLOT MANAGEMENT");
        printDivider("=", 60);
        System.out.println("  1. Generate Slots (Create)");
        System.out.println("  2. View Slots     (Read)");
        System.out.println("  3. Block Slots    (Update — Mark Unavailable)");
        System.out.println("  4. Unblock Slots  (Update — Restore Available)");
        System.out.println("  5. Delete Slots   (Delete Available Slots)");
        System.out.println("  0. Back");
        printDivider("=", 60);
    }

    // =========================================================================
    // 1 — GENERATE (CREATE)
    // =========================================================================

    private void menuGenerate(SortedArrayList<Facility> facilities) {
        printDivider("-", 60);
        System.out.println("  GENERATE SLOTS");
        printDivider("-", 60);
        System.out.println("  Generate for:");
        System.out.println("  1. All facilities");
        System.out.println("  2. By Facility Name");
        System.out.println("  3. By Room Type");
        System.out.println("  4. Individual Room (Facility ID)");
        System.out.println("  0. Back");

        int scope = readInt("Select scope: ", 0, 4);
        if (scope == 0) return;

        LocalDate date = readDate("Enter date to generate (dd-MM-yyyy) [today/tomorrow only]: ");
        if (date == null) return;

        LocalDate today    = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        if (!date.equals(today) && !date.equals(tomorrow)) {
            printError("Date must be today (" + today.format(DATE_FMT)
                    + ") or tomorrow (" + tomorrow.format(DATE_FMT) + ").");
            return;
        }

        int added;

        switch (scope) {
            case 1 -> {
                added = control.generateDaySlotsForMultipleFacilities(facilities, date);
                printSuccess("Generated " + added + " slot(s) for ALL facilities on "
                        + date.format(DATE_FMT) + ".");
            }
            case 2 -> {
                String name = readFacilityName(facilities);
                if (name == null) return;
                added = control.generateDaySlotsByFacilityName(facilities, name, date);
                printSuccess("Generated " + added + " slot(s) for [" + name + "] on "
                        + date.format(DATE_FMT) + ".");
            }
            case 3 -> {
                String type = readRoomType(facilities);
                if (type == null) return;
                added = control.generateDaySlotsByRoomType(facilities, type, date);
                printSuccess("Generated " + added + " slot(s) for room type ["
                        + type + "] on " + date.format(DATE_FMT) + ".");
            }
            case 4 -> {
                int fid = readFacilityId("Enter Facility ID: ");
                added = control.generateDaySlotsForOneFacility(fid, date);
                printSuccess("Generated " + added + " slot(s) for Facility " + fid
                        + " on " + date.format(DATE_FMT) + ".");
            }
        }

        pause();
    }

    // =========================================================================
    // 2 — VIEW (READ)
    // =========================================================================

    private void menuView(SortedArrayList<Facility> facilities) {
        printDivider("-", 60);
        System.out.println("  VIEW SLOTS");
        printDivider("-", 60);
        System.out.println("  View by:");
        System.out.println("  1. All slots on a date");
        System.out.println("  2. By Facility Name on a date");
        System.out.println("  3. By Room Type on a date");
        System.out.println("  4. Individual Room on a date");
        System.out.println("  0. Back");

        int scope = readInt("Select scope: ", 0, 4);
        if (scope == 0) return;

        LocalDate date = readDate("Enter date (dd-MM-yyyy): ");
        if (date == null) return;

        SortedArrayList<Timeslot> slots;

        switch (scope) {
            case 1 -> {
                slots = control.getSlotsByDate(date);
                printSlotTable(slots, facilities, "All Slots on " + date.format(DATE_FMT));
            }
            case 2 -> {
                String name = readFacilityName(facilities);
                if (name == null) return;
                slots = control.getAllSlots(facilities, name, date);
                printSlotTable(slots, facilities,
                        "[" + name + "] Slots on " + date.format(DATE_FMT));
            }
            case 3 -> {
                String type = readRoomType(facilities);
                if (type == null) return;
                slots = control.getSlotsByRoomTypeAndDate(facilities, type, date);
                printSlotTable(slots, facilities,
                        "Room Type [" + type + "] Slots on " + date.format(DATE_FMT));
            }
            case 4 -> {
                int fid = readFacilityId("Enter Facility ID: ");
                slots = control.getSlotsByFacilityAndDate(fid, date);
                printSlotTable(slots, facilities,
                        "Facility " + fid + " Slots on " + date.format(DATE_FMT));
            }
            default -> { return; }
        }

        pause();
    }

    // =========================================================================
    // 3 — BLOCK (UPDATE)
    // =========================================================================

    private void menuBlock(SortedArrayList<Facility> facilities, String staffId) {
        printDivider("-", 60);
        System.out.println("  BLOCK SLOTS");
        printDivider("-", 60);
        System.out.println("  Block by:");
        System.out.println("  1. By Facility Name on a date (bulk)");
        System.out.println("  2. By Room Type on a date (bulk)");
        System.out.println("  3. Individual Room — whole day");
        System.out.println("  4. Individual Slot (single block)");
        System.out.println("  0. Back");

        int scope = readInt("Select scope: ", 0, 4);
        if (scope == 0) return;

        String reason = readNonEmpty("Enter reason for blocking: ");
        int    count;

        switch (scope) {
            case 1 -> {
                LocalDate date = readDate("Enter date (dd-MM-yyyy): ");
                if (date == null) return;
                String name = readFacilityName(facilities);
                if (name == null) return;
                count = control.blockAllSlotsByFacilityName(facilities, name, date, staffId);
                printSuccess("Blocked " + count + " slot(s) for [" + name + "] on "
                        + date.format(DATE_FMT) + ".");
            }
            case 2 -> {
                LocalDate date = readDate("Enter date (dd-MM-yyyy): ");
                if (date == null) return;
                String type = readRoomType(facilities);
                if (type == null) return;
                count = control.blockAllSlotsByRoomType(facilities, type, date, staffId);
                printSuccess("Blocked " + count + " slot(s) for room type [" + type
                        + "] on " + date.format(DATE_FMT) + ".");
            }
            case 3 -> {
                LocalDate date = readDate("Enter date (dd-MM-yyyy): ");
                if (date == null) return;
                int fid = readFacilityId("Enter Facility ID: ");
                count = control.blockAllSlotsForFacility(fid, date, staffId);
                printSuccess("Blocked " + count + " slot(s) for Facility " + fid
                        + " on " + date.format(DATE_FMT) + ".");
            }
            case 4 -> {
                String slotId = readNonEmpty("Enter Slot ID (e.g. TS-3-20250402-0830): ");
                boolean ok = control.blockOneTimeslot(slotId, staffId);
                if (ok) printSuccess("Slot " + slotId + " blocked.");
                else    printError("Could not block slot. It may not exist or is already booked.");
            }
        }

        pause();
    }

    // =========================================================================
    // 4 — UNBLOCK (UPDATE)
    // =========================================================================

    private void menuUnblock(SortedArrayList<Facility> facilities) {
        printDivider("-", 60);
        System.out.println("  UNBLOCK SLOTS");
        printDivider("-", 60);
        System.out.println("  Unblock by:");
        System.out.println("  1. By Facility Name on a date (bulk)");
        System.out.println("  2. By Room Type on a date (bulk)");
        System.out.println("  3. Individual Room — whole day");
        System.out.println("  4. Individual Slot (single block)");
        System.out.println("  0. Back");

        int scope = readInt("Select scope: ", 0, 4);
        if (scope == 0) return;

        int count;

        switch (scope) {
            case 1 -> {
                LocalDate date = readDate("Enter date (dd-MM-yyyy): ");
                if (date == null) return;
                String name = readFacilityName(facilities);
                if (name == null) return;
                count = control.unblockAllSlotsByFacilityName(facilities, name, date);
                printSuccess("Unblocked " + count + " slot(s) for [" + name + "] on "
                        + date.format(DATE_FMT) + ".");
            }
            case 2 -> {
                LocalDate date = readDate("Enter date (dd-MM-yyyy): ");
                if (date == null) return;
                String type = readRoomType(facilities);
                if (type == null) return;
                count = control.unblockAllSlotsByRoomType(facilities, type, date);
                printSuccess("Unblocked " + count + " slot(s) for room type [" + type
                        + "] on " + date.format(DATE_FMT) + ".");
            }
            case 3 -> {
                LocalDate date = readDate("Enter date (dd-MM-yyyy): ");
                if (date == null) return;
                int fid = readFacilityId("Enter Facility ID: ");
                count = control.unblockAllSlotsForFacility(fid, date);
                printSuccess("Unblocked " + count + " slot(s) for Facility " + fid
                        + " on " + date.format(DATE_FMT) + ".");
            }
            case 4 -> {
                String slotId = readNonEmpty("Enter Slot ID: ");
                boolean ok = control.unblockOneTimeslot(slotId);
                if (ok) printSuccess("Slot " + slotId + " unblocked.");
                else    printError("Could not unblock slot. It may not exist or is not blocked.");
            }
        }

        pause();
    }

    // =========================================================================
    // 5 — DELETE
    // =========================================================================

    private void menuDelete(SortedArrayList<Facility> facilities) {
        printDivider("-", 60);
        System.out.println("  DELETE AVAILABLE SLOTS");
        System.out.println("  (Only AVAILABLE slots are removed. BOOKED/BLOCKED slots");
        System.out.println("   are protected and must be handled separately.)");
        printDivider("-", 60);
        System.out.println("  Delete by:");
        System.out.println("  1. By Facility Name on a date");
        System.out.println("  2. By Room Type on a date");
        System.out.println("  3. Individual Room on a date");
        System.out.println("  0. Back");

        int scope = readInt("Select scope: ", 0, 3);
        if (scope == 0) return;

        LocalDate date = readDate("Enter date (dd-MM-yyyy): ");
        if (date == null) return;

        // Confirm before destructive action
        System.out.print("  !! Confirm deletion? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("  Deletion cancelled.");
            pause();
            return;
        }

        int count;

        switch (scope) {
            case 1 -> {
                String name = readFacilityName(facilities);
                if (name == null) return;
                count = control.deleteAvailableTimeslotsForMultipleFacilities(facilities, name, date);
                printSuccess("Deleted " + count + " available slot(s) for [" + name
                        + "] on " + date.format(DATE_FMT) + ".");
            }
            case 2 -> {
                String type = readRoomType(facilities);
                if (type == null) return;
                count = control.deleteAvailableSlotsByRoomType(facilities, type, date);
                printSuccess("Deleted " + count + " available slot(s) for room type ["
                        + type + "] on " + date.format(DATE_FMT) + ".");
            }
            case 3 -> {
                int fid = readFacilityId("Enter Facility ID: ");
                count = control.deleteAllTimeslotsForOneFacility(fid, date);
                printSuccess("Deleted " + count + " available slot(s) for Facility "
                        + fid + " on " + date.format(DATE_FMT) + ".");
            }
        }

        pause();
    }

    // =========================================================================
    // TABLE DISPLAY
    // =========================================================================

    /**
     * Prints a formatted table of timeslots.
     * Each row shows: No | SlotID | FacID | RoomName | Date | Start | End | Status | BookedBy
     */
    public void printSlotTable(SortedArrayList<Timeslot> slots,
                                SortedArrayList<Facility> facilities,
                                String title) {
        printDivider("=", 110);
        System.out.println("  " + title);
        printDivider("=", 110);

        // Header
        System.out.printf("%-" + COL_NO      + "s | "
                        + "%-" + COL_SLOT_ID  + "s | "
                        + "%-" + COL_FACILITY + "s | "
                        + "%-" + COL_ROOM     + "s | "
                        + "%-" + COL_DATE     + "s | "
                        + "%-" + COL_START    + "s | "
                        + "%-" + COL_END      + "s | "
                        + "%-" + COL_STATUS   + "s | "
                        + "%-" + COL_BY       + "s%n",
                "No.", "Slot ID", "Fac.ID", "Room Name", "Date",
                "Start", "End", "Status", "Booked/Blocked By");

        printDivider("-", 110);

        if (slots.isEmpty()) {
            System.out.println("  (No slots found)");
            printDivider("=", 110);
            return;
        }

        Iterator<Timeslot> it = slots.getIterator();
        int rowNum = 1;

        while (it.hasNext()) {
            Timeslot slot    = it.next();
            String   roomName = resolveRoomName(slot.getFacilityId(), facilities);
            String   bookedBy = slot.getBookedBy() != null ? slot.getBookedBy() : "-";
            String   statusStr = formatStatus(slot.getStatus());

            System.out.printf("%-" + COL_NO      + "d | "
                            + "%-" + COL_SLOT_ID  + "s | "
                            + "%-" + COL_FACILITY + "s | "
                            + "%-" + COL_ROOM     + "s | "
                            + "%-" + COL_DATE     + "s | "
                            + "%-" + COL_START    + "s | "
                            + "%-" + COL_END      + "s | "
                            + "%-" + COL_STATUS   + "s | "
                            + "%-" + COL_BY       + "s%n",
                    rowNum++,
                    slot.getTimeslotId(),
                    slot.getFacilityId(),
                    truncate(roomName, COL_ROOM),
                    slot.getDate().format(DATE_FMT),
                    slot.getStartTime().format(TIME_FMT),
                    slot.getEndTime().format(TIME_FMT),
                    statusStr,
                    truncate(bookedBy, COL_BY));
        }

        printDivider("-", 110);
        System.out.println("  Total: " + slots.getNumberOfEntries() + " slot(s)");
        printDivider("=", 110);
    }

    // =========================================================================
    // INPUT HELPERS
    // =========================================================================

    /** Reads an integer within [min, max], reprompting on invalid input. */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print("  " + prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
                System.out.println("  Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input. Please enter a number.");
            }
        }
    }

    /** Reads a facility ID (positive integer). */
    private int readFacilityId(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            String line = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val > 0) return val;
                System.out.println("  Facility ID must be positive.");
            } catch (NumberFormatException e) {
                System.out.println("  Invalid input.");
            }
        }
    }

    /** Reads a date in dd-MM-yyyy format, returns null if user types 'back'. */
    private LocalDate readDate(String prompt) {
        while (true) {
            System.out.print("  " + prompt + " (or type 'back'): ");
            String line = scanner.nextLine().trim();

            if (line.equalsIgnoreCase("back")) return null;

            try {
                return LocalDate.parse(line, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("  Invalid date format. Use dd-MM-yyyy (e.g. 02-04-2025).");
            }
        }
    }

    /** Reads a non-empty string. */
    private String readNonEmpty(String prompt) {
        while (true) {
            System.out.print("  " + prompt);
            String line = scanner.nextLine().trim();
            if (!line.isEmpty()) return line;
            System.out.println("  Input cannot be empty.");
        }
    }

    /**
     * Displays a numbered list of distinct facilityNames from the facility list
     * and lets the user pick one.
     */
    private String readFacilityName(SortedArrayList<Facility> facilities) {
        SortedArrayList<String> names = new SortedArrayList<>();
        Iterator<Facility> it = facilities.getIterator();

        while (it.hasNext()) {
            String name = it.next().getFacilityName();
            if (!names.contains(name)) names.add(name);
        }

        if (names.isEmpty()) {
            printError("No facilities found.");
            return null;
        }

        System.out.println("  Available facility names:");
        for (int i = 1; i <= names.getNumberOfEntries(); i++) {
            System.out.println("    " + i + ". " + names.getEntry(i));
        }

        int choice = readInt("Select facility name: ", 1, names.getNumberOfEntries());
        return names.getEntry(choice);
    }

    /**
     * Displays a numbered list of distinct roomTypes from the facility list
     * and lets the user pick one.
     */
    private String readRoomType(SortedArrayList<Facility> facilities) {
        SortedArrayList<String> types = new SortedArrayList<>();
        Iterator<Facility> it = facilities.getIterator();

        while (it.hasNext()) {
            String type = it.next().getRoomType();
            if (!types.contains(type)) types.add(type);
        }

        if (types.isEmpty()) {
            printError("No room types found.");
            return null;
        }

        System.out.println("  Available room types:");
        for (int i = 1; i <= types.getNumberOfEntries(); i++) {
            System.out.println("    " + i + ". " + types.getEntry(i));
        }

        int choice = readInt("Select room type: ", 1, types.getNumberOfEntries());
        return types.getEntry(choice);
    }

    // =========================================================================
    // OUTPUT HELPERS
    // =========================================================================

    private void printDivider(String ch, int length) {
        System.out.println(ch.repeat(length));
    }

    private void printSuccess(String msg) {
        System.out.println("\n  [OK] " + msg);
    }

    private void printError(String msg) {
        System.out.println("\n  [!!] " + msg);
    }

    private void pause() {
        System.out.print("\n  Press Enter to continue...");
        scanner.nextLine();
    }

    /** Looks up the roomName for a given facilityId string from the facility list. */
    private String resolveRoomName(String facilityId, SortedArrayList<Facility> facilities) {
        Iterator<Facility> it = facilities.getIterator();
        while (it.hasNext()) {
            Facility f = it.next();
            if (String.valueOf(f.getFacilityId()).equals(facilityId)) {
                return f.getRoomName();
            }
        }
        return "Unknown";
    }

    /** Truncates a string to fit within a column width. */
    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen - 1) + "…";
    }

    /** Returns a padded, readable status label. */
    private String formatStatus(Timeslot.Status status) {
        return switch (status) {
            case AVAILABLE -> "AVAILABLE";
            case BOOKED    -> "BOOKED";
            case BLOCKED   -> "BLOCKED";
        };
    }
}