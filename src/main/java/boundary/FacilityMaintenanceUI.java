package boundary;

import adt.SortedArrayList;
import control.FacilityMaintenance;
import entity.Facility;
import java.util.Scanner;

/*
 * Tiw Hong Xuan
*/

public class FacilityMaintenanceUI {
    private final Scanner            scanner            = new Scanner(System.in);
    private final FacilityMaintenance facilityMaintenance = new FacilityMaintenance();

    private static final String FNAME_CYBER   = "Cyber Centre";
    private static final String FNAME_LIBRARY = "Library";
    private static final String FNAME_SPORTS  = "Sports Facilities";
    private static final String FNAME_OTHER   = "Other";

    private static final SortedArrayList<String> roomTypesCyberList = new SortedArrayList<>();
    private static final SortedArrayList<String> roomTypesLibraryList = new SortedArrayList<>();
    private static final SortedArrayList<String> roomTypesSportsList = new SortedArrayList<>();
    private static final SortedArrayList<String> roomTypesOtherList = new SortedArrayList<>();

    static {
        roomTypesCyberList.add("Discussion Room (1 PC)");
        roomTypesCyberList.add("Discussion Room with Projector (2 PCs)");
        roomTypesCyberList.add("Discussion Room with Projector (2 PCs) [HDMI]");

        roomTypesLibraryList.add("Discussion Room (1 PC)");
        roomTypesLibraryList.add("Discussion Room with Projector (2 PCs)");
        roomTypesLibraryList.add("Individual Study Room");
        roomTypesLibraryList.add("Presentation Room");

        roomTypesSportsList.add("Badminton Court");
        roomTypesSportsList.add("Basketball Court");
        roomTypesSportsList.add("Pickleball Court");
        roomTypesSportsList.add("Swimming Pool");
        roomTypesSportsList.add("Volleyball Court");

        roomTypesOtherList.add("Other");
    }

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

    private void addFacility() {
        int choice;

        do {
            printSectionHeader("ADD FACILITY");

            String facilityName = chooseFacilityName();
            if (facilityName == null) {
                System.out.println("\n  Add facility cancelled.");
                return;
            }

            String roomType = chooseRoomType(facilityName);
            if (roomType == null) {
                System.out.println("\n  Add facility cancelled.");
                return;
            }

            System.out.print("\n  Enter Room Name (e.g. B002, Pickleball Court 1): ");
            String roomName = scanner.nextLine().trim();

            if (!facilityMaintenance.isValidRoomName(roomName)) {
                System.out.println("  [!!] Room name cannot be empty. Please try again.");
                choice = 2;
                continue;
            }

            if (facilityMaintenance.facilityExists(facilityName, roomType, roomName)) {
                System.out.println("  [!!] A facility with the same details already exists.");
                choice = 2;
                continue;
            }

            String generatedId = facilityMaintenance.generateFacilityId(facilityName);
            Facility preview   = new Facility(generatedId, facilityName, roomType, roomName);

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
                            addFacility();
                        } else {
                            System.out.println("\n  Returning to main menu...");
                        }
                        return;
                    } else {
                        System.out.println("\n  [!!] Failed to add facility. Please try again.");
                        choice = 2;
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
            newFacilityName     = existing.getFacilityName();
            facilityNameChanged = false;
        }

        String newRoomType;

        if (FNAME_OTHER.equalsIgnoreCase(newFacilityName)) {
            newRoomType = roomTypesOtherList.getEntry(1);

        } else if (facilityNameChanged) {
            System.out.println("\n  Facility name changed -- please select a new room type:");
            newRoomType = chooseRoomType(newFacilityName);
            if (newRoomType == null) {
                System.out.println("\n  Update cancelled.");
                return;
            }
        } else {
            System.out.println("\n  Update Room Type?");
            System.out.println("  0. Keep current (" + existing.getRoomType() + ")");
            SortedArrayList<String> types = getRoomTypesFor(newFacilityName);
            int typeCount = types.getNumberOfEntries();
            for (int i = 1; i <= typeCount; i++) {
                System.out.println("  " + i + ". " + types.getEntry(i));
            }
            System.out.print("  Enter choice: ");
            String rawType = scanner.nextLine().trim();

            if (rawType.isEmpty() || rawType.equals("0")) {
                newRoomType = existing.getRoomType();
            } else {
                try {
                    int opt = Integer.parseInt(rawType);
                    if (opt >= 1 && opt <= typeCount) {
                        newRoomType = types.getEntry(opt);
                    } else {
                        System.out.println("  [!!] Invalid option. Update cancelled.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("  [!!] Invalid option. Update cancelled.");
                    return;
                }
            }
        }

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
            case 0 -> { return null; }
            case 1 -> { return FNAME_CYBER; }
            case 2 -> { return FNAME_LIBRARY; }
            case 3 -> { return FNAME_SPORTS; }
            case 4 -> { return FNAME_OTHER; }
            default -> {
                System.out.println("\n  [!!] Invalid choice. Please enter 0 – 4.");
                return chooseFacilityName();
            }
        }
    }

    private SortedArrayList<String> getRoomTypesFor(String facilityName) {
        if (facilityName == null) return roomTypesLibraryList;

        if (facilityName.equalsIgnoreCase(FNAME_CYBER))   return roomTypesCyberList;
        if (facilityName.equalsIgnoreCase(FNAME_LIBRARY)) return roomTypesLibraryList;
        if (facilityName.equalsIgnoreCase(FNAME_SPORTS))  return roomTypesSportsList;
        if (facilityName.equalsIgnoreCase(FNAME_OTHER))   return roomTypesOtherList;

        SortedArrayList<String> fallback = new SortedArrayList<>();
        fallback.add("Other");
        return fallback;
    }

    private String chooseRoomType(String facilityName) {
        SortedArrayList<String> types = getRoomTypesFor(facilityName);
        int typeCount = types.getNumberOfEntries();

        System.out.println();
        printDivider();
        System.out.println("  Select Room Type");
        System.out.println("  Facility: " + facilityName);
        printDivider();
        for (int i = 1; i <= typeCount; i++) {
            System.out.println("  " + i + ". " + types.getEntry(i));
        }
        System.out.println("  0. Cancel");
        printDivider();
        System.out.print("  Enter choice: ");
        int choice = readInt();

        if (choice == 0) return null;

        if (choice >= 1 && choice <= typeCount) {
            return types.getEntry(choice);
        }

        System.out.println("  [!!] Invalid choice. Please try again.");
        return chooseRoomType(facilityName);
    }

    private void printSectionHeader(String title) {
        System.out.println();
        System.out.println("  ============================================================");
        System.out.println("    " + title);
        System.out.println("  ============================================================");
    }

    private void printDivider() {
        System.out.println("  ----------------------------------------------------------");
    }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("  [!!] Invalid input. Please enter a number: ");
            scanner.nextLine();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }
}