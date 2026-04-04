package boundary;

import adt.SortedArrayList;
import control.FacilityMaintenance;
import control.TimeslotMaintenance;
import control.UserMaintenance;
import entity.Facility;
import entity.Timeslot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;

public class TimeslotMaintenanceUI {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private static final int COLUMN_INDEX = 4;
    private static final int COLUMN_WIDTH_ROOM_NAME = 20;
    private static final int COLUMN_WIDTH_START_TIME = 6;

    private final TimeslotMaintenance timeslotMaintenance;
    private final FacilityMaintenance facilityMaintenance;
    private final UserMaintenance userMaintenance;
    private final Scanner scanner;

    public TimeslotMaintenanceUI() {
        this.timeslotMaintenance = new TimeslotMaintenance();
        this.facilityMaintenance = new FacilityMaintenance();
        this.userMaintenance = new UserMaintenance();
        this.scanner = new Scanner(System.in);
    }
    
    private static final LocalTime[] TIME_MARKS = buildTimeMarks();

    private static LocalTime[] buildTimeMarks() {
        int count = 0;
        LocalTime cursor = Timeslot.DAY_START;
        
        while (cursor.isBefore(Timeslot.DAY_END)) {
            count++;
            cursor = cursor.plusMinutes(Timeslot.MINUTES_PER_BLOCK);
        }
        
        LocalTime[] marks = new LocalTime[count];
        cursor = Timeslot.DAY_START;
        
        for (int i = 0; i < count; i++) {
            marks[i] = cursor;
            cursor = cursor.plusMinutes(Timeslot.MINUTES_PER_BLOCK);
        }
        
        return marks;
    }

    public void mainMenuForUser() {
        boolean running = true;

        while (running) {
            System.out.println("--- Main Menu ---");
            System.out.println("1. View Slots");
            System.out.println("0. Back");
            
            int choice = readInt("Enter choice: ", 0, 1);

            switch (choice) {
                case 1 -> menuView();
                case 0 -> running = false;
            }
        }
    }
    
    public void mainMenuForAdmin(SortedArrayList<Facility> facilityList, String adminId, String adminName) {
        boolean running = true;

        while (running) {
            System.out.println("--- Timeslot Management ---");
            System.out.println("1. View Slots");
            System.out.println("2. Generate Slots");
            System.out.println("3. Block Slots");
            System.out.println("4. Unblock Slots");
            System.out.println("5. Delete Slots");
            System.out.println("0. Back");
            
            int choice = readInt("Enter choice: ", 0, 5);

            switch (choice) {
                case 1 -> menuView();
                case 2 -> menuGenerate();
                case 3 -> menuBlock(facilityList, adminId, adminName);
                case 4 -> menuUnblock(facilityList);
                case 5 -> menuDelete(facilityList);
                case 0 -> running = false;
            }
        }
    }
    
    // -----------------------------------------
    // CREATE
    // -----------------------------------------
    
    private void menuGenerate() {
        System.out.println("--- Generate Slots ---");
        System.out.println("Generate for:");
        System.out.println("1. All Facilities");
        System.out.println("2. By Facility Name");
        System.out.println("3. By Room Type");
        System.out.println("4. Individual Room");
        System.out.println("0. Back");

        int facilityScopeSelection = readInt("Select scope: ", 0, 4);
        if (facilityScopeSelection == 0) return;
        
        LocalDate date = null;
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        System.out.println("--- Generate Slots ---");
        System.out.println("Choose a date (today/tomorrow only):");
        System.out.println("1. " + today.format(DATE_FORMAT));
        System.out.println("2. " + tomorrow.format(DATE_FORMAT));
        System.out.println("0. Back");
        
        int dateSelection = readInt("Select date: ", 0, 2);
        if (dateSelection == 0) return;
        
        if (dateSelection == 1) {
            date = today;
        }
        
        else if (dateSelection == 2) {
            date = tomorrow;
        }

        SortedArrayList<Facility> targetFacilityList;
        int added;

        switch (facilityScopeSelection) {
            case 1 -> {
                targetFacilityList = facilityMaintenance.getAllFacilities();
                
                added = timeslotMaintenance.generateDaySlotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("\nGenerated " + added + " slot(s) for all facilities on " + date.format(DATE_FORMAT));
            }

            case 2 -> {
                String facilityName = readFacilityName(facilityMaintenance.getAllFacilities());
                if (facilityName == null) return;

                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(facilityName);
                
                added = timeslotMaintenance.generateDaySlotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("\nGenerated " + added + " slot(s) for [" + facilityName + "] on " + date.format(DATE_FORMAT));
            }

            case 3 -> {
                String roomType = readRoomType(facilityMaintenance.getAllFacilities());
                if (roomType == null) return;

                targetFacilityList = facilityMaintenance.getFacilitiesByRoomType(roomType);
                
                added = timeslotMaintenance.generateDaySlotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("\nGenerated " + added + " slot(s) for room type [" + roomType + "] on " + date.format(DATE_FORMAT));
            }

            case 4 -> {
                Facility facility = readFacility(facilityMaintenance.getAllFacilities());
                if (facility == null) return;

                added = timeslotMaintenance.generateDaySlotsForOneFacility(facility, date);
                
                System.out.println("\nGenerated " + added + " slot(s) for " + facility.getRoomName() + " on " + date.format(DATE_FORMAT));
            }
        }

        pause();
    }

    // -----------------------------------------
    // READ
    // -----------------------------------------

    private void menuView() {
        System.out.println("--- View Slots ---");
        System.out.println("Select Facility Name:");
        System.out.println("1. Cyber Centre Room");
        System.out.println("2. Library Room");
        System.out.println("3. Sports Facilities");
        System.out.println("4. Other");
        System.out.println("5. All Facilities");
        System.out.println("0. Back");

        int facilityScopeSelection = readInt("Select facility: ", 0, 5);
        if (facilityScopeSelection == 0) return;
        
        LocalDate date = null;
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        System.out.println("Choose a date (today/tomorrow):");
        System.out.println("1. " + today.format(DATE_FORMAT));
        System.out.println("2. " + tomorrow.format(DATE_FORMAT));
        System.out.println("0. Back");
        
        int dateSelection = readInt("Select date: ", 0, 2);
        if (dateSelection == 0) return;
        
        if (dateSelection == 1) {
            date = today;
        }
        
        else if (dateSelection == 2) {
            date = tomorrow;
        }

        SortedArrayList<Facility> targetFacilityList;
        String tableTitle;

        switch (facilityScopeSelection) {

            case 1 -> {
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName("Cyber Centre");
                tableTitle = "Cyber Centre Discussion Room — " + date.format(DATE_FORMAT);
            }
            
            case 2 -> {
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName("Library Discussion");
                tableTitle = "Library Discussion Room — " + date.format(DATE_FORMAT);
            }
            
            case 3 -> {
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName("Sports Facilities");
                tableTitle = "Sports Facilities — " + date.format(DATE_FORMAT);
            }

            case 4 -> {
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName("Other");
                tableTitle = "Other — " + date.format(DATE_FORMAT);
            }
            
            default -> {
                targetFacilityList = facilityMaintenance.getAllFacilities();
                tableTitle = "All Facilities — " + date.format(DATE_FORMAT);
            }
        }

        SortedArrayList<Timeslot> timeslotList = timeslotMaintenance.getTimeslotsForMultipleFacilities(targetFacilityList, date);
        
        printSlotTable(timeslotList, tableTitle, date);
        
        if (timeslotList.isEmpty()) {
            pause();
            return;
        }

        // Rebuild the seenFacilitilyList list in the same order as the table
//        SortedArrayList<Facility> seenFacilitilyList = new SortedArrayList<>();
//        Iterator<Timeslot> timeslotIterator = slots.getIterator();
//        while (timeslotIterator.hasNext()) {
//            Facility facility = timeslotIterator.next().getFacility();
//            if (!seenFacilitilyList.contains(facility)) seenFacilitilyList.add(facility);
//        }

        System.out.println("\nSelect a room index to view available slots, or 0 to go back:");
        
        int roomSelection = readInt("Room No.: ", 0, targetFacilityList.getNumberOfEntries());
        if (roomSelection == 0) return;

        Facility chosenFacility = targetFacilityList.getEntry(roomSelection);
        
        if (true) {
            printAllBlocksForRoomForAdmin(chosenFacility, date, "A001", "Admin Test Hi");
        }
        
        else {
            printAvailableBlocksForRoomForUser(chosenFacility, date);
        }
        
//        if (userMaintenance.currentUser.isAdmin()) {
//            printAllBlocksForRoomForAdmin(chosenFacility, date, currentUser.getAdminId(), currentUser.getAdminName());
//        }
//        
//        else {
//            printAvailableBlocksForRoomForUser(chosenFacility, date);
//        }
    }

    private void printAvailableBlocksForRoomForUser(Facility facility, LocalDate date) {
        System.out.println("Available slots for: " + facility.getRoomName());
        
        SortedArrayList<Timeslot> availableTimeslot = timeslotMaintenance.getTimeslotsForOneFacility(facility, date);

        if (availableTimeslot.isEmpty()) {
            System.out.println("No available slots for this room");
            return;
        }

        // Display consecutive availableTimeslot ranges like 08:00 – 10:00 covers 2 blocks
        // Group into consecutive runs so user sees natural booking windows
        int index = 1;
        int i = 1;
        
        while (i <= availableTimeslot.getNumberOfEntries()) {
            Timeslot start = availableTimeslot.getEntry(i);
            Timeslot end   = start;

            // Extend the run while blocks are consecutive and count <= MAX_CONSECUTIVE_BLOCKS
            int runLength = 1;
            
            while (i + runLength <= availableTimeslot.getNumberOfEntries() && runLength < TimeslotMaintenance.MAX_CONSECUTIVE_BLOCKS) {
                Timeslot next = availableTimeslot.getEntry(i + runLength);
                
                if (end.getEndTime().equals(next.getStartTime())) {
                    end = next;
                    runLength++;
                } 
                
                else {
                    break;
                }
            }

            // Print each possible booking window starting from this block
            // If 2 consecutive blocks exist: show 1-block, 2-block options
            for (int len = 1; len <= runLength; len++) {
                Timeslot endSlot = availableTimeslot.getEntry(i + len - 1);
                
                System.out.printf("%2d. %s – %s  (%d block(s), %d min)%n",
                        index++,
                        start.getStartTime().format(TIME_FORMAT),
                        endSlot.getEndTime().format(TIME_FORMAT),
                        len,
                        len * Timeslot.MINUTES_PER_BLOCK);
            }

            i++;
        }
    }
    
    private void printAllBlocksForRoomForAdmin(Facility facility, LocalDate date, String adminId, String adminName) {
        SortedArrayList<Timeslot> availableTimeslot = timeslotMaintenance.getTimeslotsForOneFacility(facility, date);
        
        if (availableTimeslot.isEmpty()) {
            System.out.println("No slots found for this room");
            return;
        }

        boolean managing = true;
        
        while (managing) {
            System.out.println("All slots for: " + facility.getRoomName());
            System.out.printf("%-4s %-10s %-10s %-12s %-15s%n", "No.", "Start", "End", "Status", "Booked/Blocked By");

            for (int i = 1; i <= availableTimeslot.getNumberOfEntries(); i++) {
                Timeslot timeslot = availableTimeslot.getEntry(i);
                String by = timeslot.getBlockedByName()!= null ? timeslot.getBlockedByName() : "-";
                String status = switch (timeslot.getStatus()) {
                    case BOOKED   -> "[BOOKED]   ";
                    case BLOCKED  -> "[BLOCKED]  ";
                    default       -> "[AVAILABLE]";
                };

                System.out.printf("  %-4d %-10s %-10s %-12s %-15s%n",
                        i,
                        timeslot.getStartTime().format(TIME_FORMAT),
                        timeslot.getEndTime().format(TIME_FORMAT),
                        status,
                        by);
            }

            System.out.println("Total: " + availableTimeslot.getNumberOfEntries() + " slot(s)");

            System.out.println("Actions:");
            System.out.println("1. Block a slot");
            System.out.println("2. Unblock a slot");
            System.out.println("3. Delete a slot");
            System.out.println("0. Back");

            int actionSelection = readInt("  Select action: ", 0, 3);

            switch (actionSelection) {
                case 1 -> menuBlockForChosenFacility(availableTimeslot, adminId, adminName);
                case 2 -> menuUnblockForChosenFacility(availableTimeslot);
                case 3 -> menuDeleteForChosenFacility(availableTimeslot);
                case 0 -> managing = false;
            }
        }
    }

    // -----------------------------------------
    // UPDATE
    // -----------------------------------------

    private void menuBlock(SortedArrayList<Facility> facilityList, String adminId, String adminName) {
        System.out.println("--- Block Slots ---");
        System.out.println("Block by:");
        System.out.println("1. By Facility Name for a Day");
        System.out.println("2. By Room Type for a Day");
        System.out.println("3. Individual Room for a Day");
        System.out.println("0. Back");

        int facilityScope = readInt("Select scope: ", 0, 4);
        if (facilityScope == 0) return;
        
        LocalDate date = null;
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        System.out.println("Choose a date (today/tomorrow):");
        System.out.println("1. " + today.format(DATE_FORMAT));
        System.out.println("2. " + tomorrow.format(DATE_FORMAT));
        System.out.println("0. Back");
        
        int dateSelection = readInt("Select date: ", 0, 2);
        if (dateSelection == 0) return;
        
        if (dateSelection == 1) {
            date = today;
        }
        
        else if (dateSelection == 2) {
            date = tomorrow;
        }
        
        SortedArrayList<Facility> targetFacilityList;
        int count;
        
        switch (facilityScope) {
            case 1 -> {
                String facilityName = readFacilityName(facilityList);
                if (facilityName == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(facilityName);
                
                count = timeslotMaintenance.blockMultipleTimeslotsForMultipleFacilities(targetFacilityList, date, adminId, adminName);
                
                System.out.println("Blocked " + count + " slot(s) for [" + facilityName + "] on " + date.format(DATE_FORMAT));
            }
            
            case 2 -> {
                String roomType = readRoomType(facilityList);
                if (roomType == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByRoomType(roomType);
                
                count = timeslotMaintenance.blockMultipleTimeslotsForMultipleFacilities(targetFacilityList, date, adminId, adminName);
                
                System.out.println("Blocked " + count + " slot(s) for room type [" + roomType + "] on " + date.format(DATE_FORMAT));
            }
            
            case 3 -> {
                Facility facility = readFacility(facilityMaintenance.getAllFacilities());
                if (facility == null) return;
                
                count = timeslotMaintenance.blockMultipleTimeslotsForOneFacility(facility, date, adminId, adminName);

                System.out.println("Blocked " + count + " slot(s) for Facility " + facility.getRoomName()+ " on " + date.format(DATE_FORMAT) + ".");
            }
        }

        pause();
    }

    private void menuBlockForChosenFacility(SortedArrayList<Timeslot> timeslotList, String adminId, String adminName) {
        int timeslotSelection = readInt("  Select slot No. to block: ", 1, timeslotList.getNumberOfEntries());
        Timeslot chosenTimeslot = timeslotList.getEntry(timeslotSelection);

        if (chosenTimeslot.isBlocked()) {
            System.out.println("Slot is already BLOCKED");
            return;
        }
        
        boolean status = timeslotMaintenance.blockOneTimeslot(chosenTimeslot.getTimeslotId(), adminId, adminName);

        if (status) {
            System.out.println("Slot " + chosenTimeslot.getStartTime().format(TIME_FORMAT) + " blocked successfully");
        }
        else {
            System.out.println("Failed to block slot");
        }
    }
    
    private void menuUnblock(SortedArrayList<Facility> facilityList) {        
        System.out.println("--- Unblock Slots ---");
        System.out.println("Unblock by:");
        System.out.println("1. By Facility Name for a Day");
        System.out.println("2. By Room Type for a Day");
        System.out.println("3. Individual Room for a Day");
        System.out.println("0. Back");

        int facilityScope = readInt("Select scope: ", 0, 4);
        if (facilityScope == 0) return;
        
        LocalDate date = null;
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        System.out.println("Choose a date (today/tomorrow):");
        System.out.println("1. " + today.format(DATE_FORMAT));
        System.out.println("2. " + tomorrow.format(DATE_FORMAT));
        System.out.println("0. Back");
        
        int dateSelection = readInt("Select date: ", 0, 2);
        if (dateSelection == 0) return;
        
        if (dateSelection == 1) {
            date = today;
        }
        
        else if (dateSelection == 2) {
            date = tomorrow;
        }

        SortedArrayList<Facility> targetFacilityList;
        int count;
        
        switch (facilityScope) {
            case 1 -> {
                String facilityName = readFacilityName(facilityList);
                if (facilityName == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(facilityName);
                
                count = timeslotMaintenance.unblockMultipleTimeslotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("Unblocked " + count + " slot(s) for [" + facilityName + "] on " + date.format(DATE_FORMAT));
            }
            
            case 2 -> {
                String roomType = readRoomType(facilityList);
                if (roomType == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByRoomType(roomType);
                
                count = timeslotMaintenance.unblockMultipleTimeslotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("Unblocked " + count + " slot(s) for room type [" + roomType + "] on " + date.format(DATE_FORMAT));
            }
            
            case 3 -> {
                Facility facility = readFacility(facilityMaintenance.getAllFacilities());
                if (facility == null) return;
                
                count = timeslotMaintenance.unblockMultipleTimeslotsForOneFacility(facility, date);

                System.out.println("Unblocked " + count + " slot(s) for Facility " + facility.getRoomName()+ " on " + date.format(DATE_FORMAT) + ".");
            }
        }

        pause();
    }
    
    private void menuUnblockForChosenFacility(SortedArrayList<Timeslot> timeslotList) {
        int timeslotSelection = readInt("  Select slot No. to unblock: ", 1, timeslotList.getNumberOfEntries());
        Timeslot chosenTimeslot = timeslotList.getEntry(timeslotSelection);

        if (!chosenTimeslot.isBlocked()) {
            System.out.println("Slot is not BLOCKED");
            return;
        }

        boolean status = timeslotMaintenance.unblockOneTimeslot(chosenTimeslot.getTimeslotId());

        if (status) {
            System.out.println("Slot " + chosenTimeslot.getStartTime().format(TIME_FORMAT) + " unblocked successfully");
        }
        else {
            System.out.println("Failed to unblock slot");
        }
    }
    
    // -----------------------------------------
    // DELETE
    // -----------------------------------------
    
    private void menuDelete(SortedArrayList<Facility> facilityList) {        
        System.out.println("--- Delete AVAILABLE Slots ---");
        System.out.println("Delete by:");
        System.out.println("1. By Facility Name for a Day");
        System.out.println("2. By Room Type for a Day");
        System.out.println("3. Individual Room for a Day");
        System.out.println("0. Back");

        int facilityScope = readInt("Select scope: ", 0, 4);
        if (facilityScope == 0) return;
        
        LocalDate date = null;
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        System.out.println("Choose a date (today/tomorrow):");
        System.out.println("1. " + today.format(DATE_FORMAT));
        System.out.println("2. " + tomorrow.format(DATE_FORMAT));
        System.out.println("0. Back");
        
        int dateSelection = readInt("Select date: ", 0, 2);
        if (dateSelection == 0) return;
        
        if (dateSelection == 1) {
            date = today;
        }
        
        else if (dateSelection == 2) {
            date = tomorrow;
        }
        
        System.out.print("Confirm delete? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled");
            return;
        }

        SortedArrayList<Facility> targetFacilityList;
        int count;
        
        switch (facilityScope) {
            case 1 -> {
                String facilityName = readFacilityName(facilityList);
                if (facilityName == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(facilityName);
                
                count = timeslotMaintenance.deleteAvailableTimeslotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("Deleted " + count + " slot(s) for [" + facilityName + "] on " + date.format(DATE_FORMAT));
            }
            
            case 2 -> {
                String roomType = readRoomType(facilityList);
                if (roomType == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByRoomType(roomType);
                
                count = timeslotMaintenance.deleteAvailableTimeslotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("Deleted " + count + " slot(s) for room type [" + roomType + "] on " + date.format(DATE_FORMAT));
            }
            
            case 3 -> {
                Facility facility = readFacility(facilityMaintenance.getAllFacilities());
                if (facility == null) return;
                
                count = timeslotMaintenance.deleteAvailableTimeslotsForOneFacility(facility, date);

                System.out.println("Deleted " + count + " slot(s) for Facility " + facility.getRoomName()+ " on " + date.format(DATE_FORMAT) + ".");
            }
        }

        pause();
    }
    
    private void menuDeleteForChosenFacility(SortedArrayList<Timeslot> timeslotList) {
        int timeslotSelection = readInt("  Select slot No. to delete: ", 1, timeslotList.getNumberOfEntries());
        Timeslot chosenTimeslot = timeslotList.getEntry(timeslotSelection);

        if (chosenTimeslot.isBooked()) {
            System.out.println("Cannot delete a BOOKED slot. Cancel the booking first");
            return;
        }
        
        if (chosenTimeslot.isBlocked()) {
            System.out.println("Cannot delete a BLOCKED slot. Unblock the booking first");
            return;
        }

        System.out.print("Confirm delete? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (!confirm.equalsIgnoreCase("Y")) {
            System.out.println("Deletion cancelled");
            return;
        }

        boolean status = timeslotMaintenance.deleteOneTimeslot(chosenTimeslot.getTimeslotId());

        if (status) {
            System.out.println("Slot " + chosenTimeslot.getStartTime().format(TIME_FORMAT) + " deleted successfully");
        }
        else {
            System.out.println("Failed to delete slot");
        }
    }

    // -----------------------------------------
    // TABLE UTILITY
    // -----------------------------------------
    
    private void printSlotTable(SortedArrayList<Timeslot> timeslotList, String title, LocalDate date) {
        System.out.println("--- " + title + " ---");
        System.out.println("Legend:  --=Available   /=Booked   X=Blocked");

        System.out.printf("%-" + COLUMN_INDEX + "s | %-" + COLUMN_WIDTH_ROOM_NAME + "s", "No.", "Room Name");
        
        for (LocalTime mark : TIME_MARKS) {
            System.out.printf(" | %-" + COLUMN_WIDTH_START_TIME + "s", mark.format(TIME_FORMAT));
        }
        System.out.println();
        printDivider("-", calcTableWidth());

        if (timeslotList.isEmpty()) {
            System.out.println("(No slots found)");
            printDivider("=", calcTableWidth());
            return;
        }

        SortedArrayList<Facility> seenFacilitilyList = new SortedArrayList<>();
        Iterator<Timeslot> timeslotIterator = timeslotList.getIterator();
        
        while (timeslotIterator.hasNext()) {
            Facility facility = timeslotIterator.next().getFacility();
            
            if (!seenFacilitilyList.contains(facility)) {
                seenFacilitilyList.add(facility);
            }
        }

        int rowNum = 1;
        Iterator<Facility> facilityIterator = seenFacilitilyList.getIterator();
        
        while (facilityIterator.hasNext()) {
            Facility facility = facilityIterator.next();

            System.out.printf("%-" + COLUMN_INDEX + "d | %-" + COLUMN_WIDTH_ROOM_NAME + "s", rowNum++, truncate(facility.getRoomName(), COLUMN_WIDTH_ROOM_NAME));

            for (LocalTime mark : TIME_MARKS) {
                Timeslot timeslot = findSlot(facility, date, mark);
                String cell = buildCell(timeslot);
                System.out.printf(" | %-" + COLUMN_WIDTH_START_TIME + "s", cell);
            }
            
            System.out.println();
        }

        printDivider("-", calcTableWidth());
        System.out.println("Total rooms: " + seenFacilitilyList.getNumberOfEntries());
        printDivider("=", calcTableWidth());
    }

    private Timeslot findSlot(Facility facility, LocalDate date, LocalTime startTime) {
        SortedArrayList<Timeslot> timeslotList = timeslotMaintenance.getTimeslotsForOneFacility(facility, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            Timeslot timeslot = iterator.next();
            
            if (timeslot.getStartTime().equals(startTime)) {
                return timeslot;
            }
        }
        
        return null;
    }
    
    private String buildCell(Timeslot timeslot) {
        if (timeslot == null) return "  -  ";
        if (timeslot.isBlocked()) return "  X  ";
        if (timeslot.isBooked()) return "  /  ";
        return "  -- ";
    }

    private int calcTableWidth() {
        return COLUMN_INDEX + 3 + COLUMN_WIDTH_ROOM_NAME + (TIME_MARKS.length * (COLUMN_WIDTH_START_TIME + 3));
    }

    // -----------------------------------------
    // INPUT UTILITY
    // -----------------------------------------

    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) return value;
                System.out.println("Please enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number");
            }
        }
    }

    private Facility readFacility(SortedArrayList<Facility> facilityList) {
        System.out.println("Available facilities:");
        
        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility facility = facilityList.getEntry(i);
            
            System.out.printf("%2d. %-30s %-30s %s%n", i, facility.getFacilityName(), facility.getRoomType(), facility.getRoomName());
        }

        int choice = readInt("Select facility: ", 1, facilityList.getNumberOfEntries());
        
        return facilityList.getEntry(choice);
    }
    
    private String readFacilityName(SortedArrayList<Facility> facilityList) {
        SortedArrayList<String> facilityNameList = new SortedArrayList<>();
        Iterator<Facility> iterator = facilityList.getIterator();

        while (iterator.hasNext()) {
            String facilityName = iterator.next().getFacilityName();
            if (!facilityNameList.contains(facilityName)) facilityNameList.add(facilityName);
        }

        if (facilityNameList.isEmpty()) {
            System.out.println("No facilities found");
            return null;
        }

        System.out.println("Available facility names:");
        
        for (int i = 1; i <= facilityNameList.getNumberOfEntries(); i++) {
            System.out.println(i + ". " + facilityNameList.getEntry(i));
        }

        int choice = readInt("Select facility name: ", 1, facilityNameList.getNumberOfEntries());
        
        return facilityNameList.getEntry(choice);
    }

    private String readRoomType(SortedArrayList<Facility> facilityList) {
        SortedArrayList<String> roomTypeList = new SortedArrayList<>();
        Iterator<Facility> iterator = facilityList.getIterator();

        while (iterator.hasNext()) {
            String roomType = iterator.next().getRoomType();
            if (!roomTypeList.contains(roomType)) roomTypeList.add(roomType);
        }

        if (roomTypeList.isEmpty()) {
            System.out.println("No room types found");
            return null;
        }

        System.out.println("Available room types:");
        
        for (int i = 1; i <= roomTypeList.getNumberOfEntries(); i++) {
            System.out.println(i + ". " + roomTypeList.getEntry(i));
        }

        int choice = readInt("Select room type: ", 1, roomTypeList.getNumberOfEntries());
        
        return roomTypeList.getEntry(choice);
    }
    
    // -----------------------------------------
    // OUTPUT UTILITY
    // -----------------------------------------
    
    private void printDivider(String symbol, int length) {
        System.out.println(symbol.repeat(length));
    }
    
    private void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    // Truncates a string to facilityIterator within a column width
    private String truncate(String string, int maxLength) {
        if (string == null) return "";
        return string.length() <= maxLength ? string : string.substring(0, maxLength - 1) + "…";
    }
}