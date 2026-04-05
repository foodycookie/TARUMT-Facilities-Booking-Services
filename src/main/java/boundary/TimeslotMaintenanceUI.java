package boundary;

import adt.SortedArrayList;
import control.AdminMaintenance;
import control.FacilityMaintenance;
import control.TimeslotMaintenance;
import control.UserMaintenance;
import entity.Admin;
import entity.Facility;
import entity.Timeslot;
import entity.User;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Scanner;
import utility.InputOutputHelper;

/*
 * Ong Hao Howard
*/

public class TimeslotMaintenanceUI {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private static final int COLUMN_INDEX = 4;
    private static final int COLUMN_WIDTH_ROOM_NAME = 30;
    private static final int COLUMN_WIDTH_ROOM_TYPE = 30;
    private static final int COLUMN_WIDTH_START_TIME = 13;

    private final TimeslotMaintenance timeslotMaintenance;
    private final FacilityMaintenance facilityMaintenance;
    private final UserMaintenance userMaintenance;
    private final AdminMaintenance adminMaintenance;
    private final Scanner scanner;

    public TimeslotMaintenanceUI(TimeslotMaintenance timeslotMaintenance) {
        this.timeslotMaintenance = timeslotMaintenance;
        this.facilityMaintenance = new FacilityMaintenance();
        this.adminMaintenance = new AdminMaintenance();
        this.userMaintenance = new UserMaintenance();
        this.scanner = new Scanner(System.in);
    }
    
    private static final SortedArrayList<LocalTime> TIME_MARKS = buildTimeMarks();

    private static SortedArrayList<LocalTime> buildTimeMarks() {
        SortedArrayList<LocalTime> marks = new SortedArrayList<>();
        LocalTime cursor = Timeslot.DAY_START;
        
        while (cursor.isBefore(Timeslot.DAY_END)) {
            marks.add(cursor);
            cursor = cursor.plusMinutes(Timeslot.MINUTES_PER_BLOCK);
        }
        
        return marks;
    }

    public void mainMenuForUser() {
        boolean running = true;

        while (running) {
            System.out.println("\n--- Main Menu ---");
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
            System.out.println("\n--- Timeslot Management ---");
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
        System.out.println("\n--- Generate Slots ---");
        System.out.println("Generate for:");
        System.out.println("1. All Facilities");
        System.out.println("2. By Facility Name");
        System.out.println("3. By Room Type");
        System.out.println("4. Individual Room");
        System.out.println("0. Back");

        int facilityScopeSelection = readInt("Select scope: ", 0, 4);
        if (facilityScopeSelection == 0) return;
                
        LocalDate date = readDate();
        
        if (date == null) {
            return;
        }

        SortedArrayList<Facility> targetFacilityList = null;
        int added;

        switch (facilityScopeSelection) {
            case 1 -> {
                targetFacilityList = facilityMaintenance.getAllFacilities();
                
                added = timeslotMaintenance.generateDaySlotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("\nGenerated " + added + " slot(s) for all facilities on " + date.format(DATE_FORMAT));
            }

            case 2 -> {
                String facilityName = readFacilityName();
                if (facilityName == null) return;

                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(facilityName);
                
                added = timeslotMaintenance.generateDaySlotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("\nGenerated " + added + " slot(s) for [" + facilityName + "] on " + date.format(DATE_FORMAT));
            }

            case 3 -> {
                String roomType = readRoomType();
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
        System.out.println("\n--- View Slots ---");
        System.out.println("Select Facility Name:");
        System.out.println("1. " + InputOutputHelper.FNAME_CYBER);
        System.out.println("2. " + InputOutputHelper.FNAME_LIBRARY);
        System.out.println("3. " + InputOutputHelper.FNAME_SPORTS);
        System.out.println("4. " + InputOutputHelper.FNAME_OTHER);
        System.out.println("5. All Facilities");
        System.out.println("0. Back");

        int facilityScopeSelection = readInt("Select facility: ", 0, 5);
        if (facilityScopeSelection == 0) return;
        
        LocalDate date = readDate();
        
        if (date == null) {
            return;
        }

        SortedArrayList<Facility> targetFacilityList = null;
        String tableTitle = "";

        switch (facilityScopeSelection) {

            case 1 -> {
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(InputOutputHelper.FNAME_CYBER);
                tableTitle = InputOutputHelper.FNAME_CYBER + " - " + date.format(DATE_FORMAT);
            }
            
            case 2 -> {
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(InputOutputHelper.FNAME_LIBRARY);
                tableTitle = InputOutputHelper.FNAME_LIBRARY + " - " + date.format(DATE_FORMAT);
            }
            
            case 3 -> {
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(InputOutputHelper.FNAME_SPORTS);
                tableTitle = InputOutputHelper.FNAME_SPORTS + " - " + date.format(DATE_FORMAT);
            }

            case 4 -> {
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(InputOutputHelper.FNAME_OTHER);
                tableTitle = InputOutputHelper.FNAME_OTHER + " - " + date.format(DATE_FORMAT);
            }
            
            case 5 -> {
                targetFacilityList = facilityMaintenance.getAllFacilities();
                tableTitle = "All Facilities - " + date.format(DATE_FORMAT);
            }
        }

        SortedArrayList<Timeslot> timeslotList = timeslotMaintenance.getTimeslotsForMultipleFacilities(targetFacilityList, date);
        
        if (timeslotList.isEmpty()) {
            System.out.println("\nNo timeslot found");
            pause();
            return;
        }
        
        SortedArrayList<Facility> displayedFacilityList = printSlotTable(timeslotList, tableTitle, date);
        
        if (displayedFacilityList.isEmpty()) {
            pause();
            return;
        }
        
        System.out.println("Select a room index to view available slots, or 0 to go back:");
        
        int roomSelection = readInt("Room No.: ", 0, displayedFacilityList.getNumberOfEntries());
        if (roomSelection == 0) return;

        Facility chosenFacility = displayedFacilityList.getEntry(roomSelection);
        
        Admin currentAdmin = adminMaintenance.getCurrentAdmin();
        User currentUser = userMaintenance.getCurrentUser();
        
        if (currentAdmin != null) {
            menuDisplayAllBlockForAdmin(chosenFacility, date, adminMaintenance.getCurrentAdmin().getAdminId(), adminMaintenance.getCurrentAdmin().getAdminName());
        } 
        
        else if (currentUser != null) {
            menuDisplayAllBlockForUser(chosenFacility, date, userMaintenance.getCurrentUser().getUserId(), userMaintenance.getCurrentUser().getUserId());
        } 
        
        else {
            System.out.println("\nPlease select a current User or Admin first in User / Admin Module.");
        }
    }
    
    private void menuDisplayAllBlockForUser(Facility facility, LocalDate date, String userId, String userName) {
        boolean browsing = true;
        
        while (browsing) {
            SortedArrayList<Timeslot> availableTimeslot = timeslotMaintenance.getTimeslotsForOneFacility(facility, date);

            if (availableTimeslot == null || availableTimeslot.isEmpty()) {
                System.out.println("\nNo slots found for this room");
                return;
            }
            
            System.out.println("\nAll slots for: " + facility.getRoomName());
            System.out.printf("%-4s %-10s %-10s %-12s%n", "No.", "Start", "End", "Status");

            for (int i = 1; i <= availableTimeslot.getNumberOfEntries(); i++) {
                Timeslot timeslot = availableTimeslot.getEntry(i);
                String status = switch (timeslot.getStatus()) {
                    case BOOKED -> "[BOOKED]";
                    case BLOCKED -> "[BLOCKED]";
                    default -> "[AVAILABLE]";
                };

                System.out.printf("%-4d %-10s %-10s %-12s%n",
                        i,
                        timeslot.getStartTime().format(TIME_FORMAT),
                        timeslot.getEndTime().format(TIME_FORMAT),
                        status);
            }

            System.out.println("Total: " + availableTimeslot.getNumberOfEntries() + " slot(s)");
            
            pause();
            return;
        }
    }
    
    private void menuDisplayAllBlockForAdmin(Facility facility, LocalDate date, String adminId, String adminName) {
        boolean browsing = true;
        
        while (browsing) {
            SortedArrayList<Timeslot> availableTimeslot = timeslotMaintenance.getTimeslotsForOneFacility(facility, date);
        
            if (availableTimeslot == null || availableTimeslot.isEmpty()) {
                System.out.println("\nNo slots found for this room");
                return;
            }
            
            System.out.println("\nAll slots for: " + facility.getRoomName());
            System.out.printf("%-4s %-10s %-10s %-12s %-15s %-15s%n", "No.", "Start", "End", "Status", "Booked By", "Blocked By");

            for (int i = 1; i <= availableTimeslot.getNumberOfEntries(); i++) {
                Timeslot timeslot = availableTimeslot.getEntry(i);
                String bookedBy = timeslot.getBookedByName()!= null ? timeslot.getBookedByName() : "-";
                String blockedBy = timeslot.getBlockedByName()!= null ? timeslot.getBlockedByName() : "-";
                String status = switch (timeslot.getStatus()) {
                    case BOOKED -> "[BOOKED]";
                    case BLOCKED -> "[BLOCKED]";
                    default -> "[AVAILABLE]";
                };

                System.out.printf("%-4d %-10s %-10s %-12s %-15s %-15s%n",
                        i,
                        timeslot.getStartTime().format(TIME_FORMAT),
                        timeslot.getEndTime().format(TIME_FORMAT),
                        status,
                        bookedBy,
                        blockedBy);
            }

            System.out.println("Total: " + availableTimeslot.getNumberOfEntries() + " slot(s)");

            System.out.println("Actions:");
            System.out.println("1. Block a slot");
            System.out.println("2. Unblock a slot");
            System.out.println("3. Delete a slot");
            System.out.println("0. Back");

            int actionSelection = readInt("\nSelect action: ", 0, 3);

            switch (actionSelection) {
                case 1 -> menuBlockForChosenFacility(availableTimeslot, adminId, adminName);
                case 2 -> menuUnblockForChosenFacility(availableTimeslot);
                case 3 -> menuDeleteForChosenFacility(availableTimeslot);
                case 0 -> browsing = false;
            }
        }
    }

    // -----------------------------------------
    // UPDATE
    // -----------------------------------------

    private void menuBlock(SortedArrayList<Facility> facilityList, String adminId, String adminName) {
        if (facilityList == null || facilityList.isEmpty()) {
            System.out.println("\nNo facility found");
            return;
        }
                    
        System.out.println("\n--- Block Slots ---");
        System.out.println("Block by:");
        System.out.println("1. By Facility Name for a Day");
        System.out.println("2. By Room Type for a Day");
        System.out.println("3. Individual Room for a Day");
        System.out.println("0. Back");

        int facilityScope = readInt("Select scope: ", 0, 3);
        if (facilityScope == 0) return;
        
        LocalDate date = readDate();
        
        if (date == null) {
            return;
        }
        
        SortedArrayList<Facility> targetFacilityList = null;
        int count;
        
        switch (facilityScope) {
            case 1 -> {
                String facilityName = readFacilityName();
                if (facilityName == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(facilityName);
                
                count = timeslotMaintenance.blockMultipleTimeslotsForMultipleFacilities(targetFacilityList, date, adminId, adminName);
                
                System.out.println("\nBlocked " + count + " slot(s) for [" + facilityName + "] on " + date.format(DATE_FORMAT));
            }
            
            case 2 -> {
                String roomType = readRoomType();
                if (roomType == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByRoomType(roomType);
                
                count = timeslotMaintenance.blockMultipleTimeslotsForMultipleFacilities(targetFacilityList, date, adminId, adminName);
                
                System.out.println("\nBlocked " + count + " slot(s) for room type [" + roomType + "] on " + date.format(DATE_FORMAT));
            }
            
            case 3 -> {
                Facility facility = readFacility(facilityMaintenance.getAllFacilities());
                if (facility == null) return;
                
                count = timeslotMaintenance.blockMultipleTimeslotsForOneFacility(facility, date, adminId, adminName);

                System.out.println("\nBlocked " + count + " slot(s) for Facility " + facility.getRoomName()+ " on " + date.format(DATE_FORMAT) + ".");
            }
        }

        pause();
    }

    private void menuBlockForChosenFacility(SortedArrayList<Timeslot> timeslotList, String adminId, String adminName) {
        if (timeslotList == null || timeslotList.isEmpty()) {
            System.out.println("\nNo timeslot found");
            return;
        }
        
        int timeslotSelection = readInt("Select slot No. to block: ", 1, timeslotList.getNumberOfEntries());
        Timeslot chosenTimeslot = timeslotList.getEntry(timeslotSelection);

        if (chosenTimeslot.isBlocked()) {
            System.out.println("\nSlot is already BLOCKED");
            return;
        }
        
        boolean status = timeslotMaintenance.blockOneTimeslot(chosenTimeslot.getTimeslotId(), adminId, adminName);

        if (status) {
            System.out.println("\nSlot " + chosenTimeslot.getStartTime().format(TIME_FORMAT) + " blocked successfully");
        }
        else {
            System.out.println("\nFailed to block slot");
        }
    }
    
    private void menuUnblock(SortedArrayList<Facility> facilityList) {
        if (facilityList == null || facilityList.isEmpty()) {
            System.out.println("\nNo facility found");
            return;
        }
                
        System.out.println("\n--- Unblock Slots ---");
        System.out.println("Unblock by:");
        System.out.println("1. By Facility Name for a Day");
        System.out.println("2. By Room Type for a Day");
        System.out.println("3. Individual Room for a Day");
        System.out.println("0. Back");

        int facilityScope = readInt("Select scope: ", 0, 3);
        if (facilityScope == 0) return;
        
        LocalDate date = readDate();
        
        if (date == null) {
            return;
        }

        SortedArrayList<Facility> targetFacilityList = null;
        int count;
        
        switch (facilityScope) {
            case 1 -> {
                String facilityName = readFacilityName();
                if (facilityName == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(facilityName);
                
                count = timeslotMaintenance.unblockMultipleTimeslotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("Unblocked " + count + " slot(s) for [" + facilityName + "] on " + date.format(DATE_FORMAT));
            }
            
            case 2 -> {
                String roomType = readRoomType();
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
        if (timeslotList == null || timeslotList.isEmpty()) {
            System.out.println("\nNo timeslot found");
            return;
        }
                
        int timeslotSelection = readInt("Select slot No. to unblock: ", 1, timeslotList.getNumberOfEntries());
        Timeslot chosenTimeslot = timeslotList.getEntry(timeslotSelection);

        if (!chosenTimeslot.isBlocked()) {
            System.out.println("\nSlot is not BLOCKED");
            return;
        }

        boolean status = timeslotMaintenance.unblockOneTimeslot(chosenTimeslot.getTimeslotId());

        if (status) {
            System.out.println("\nSlot " + chosenTimeslot.getStartTime().format(TIME_FORMAT) + " unblocked successfully");
        }
        else {
            System.out.println("\nFailed to unblock slot");
        }
    }
    
    // -----------------------------------------
    // DELETE
    // -----------------------------------------
    
    private void menuDelete(SortedArrayList<Facility> facilityList) {
        if (facilityList == null || facilityList.isEmpty()) {
            System.out.println("\nNo facility found");
            return;
        }
                
        System.out.println("\n--- Delete AVAILABLE Slots ---");
        System.out.println("Delete by:");
        System.out.println("1. By Facility Name for a Day");
        System.out.println("2. By Room Type for a Day");
        System.out.println("3. Individual Room for a Day");
        System.out.println("0. Back");

        int facilityScope = readInt("Select scope: ", 0, 3);
        if (facilityScope == 0) return;
        
        LocalDate date = readDate();
        
        if (date == null) {
            return;
        }
        
        SortedArrayList<Facility> targetFacilityList = null;
        int count;
        
        switch (facilityScope) {
            case 1 -> {
                String facilityName = readFacilityName();
                if (facilityName == null) return;
                
                targetFacilityList = facilityMaintenance.getFacilitiesByFacilityName(facilityName);
                
                count = timeslotMaintenance.deleteAvailableTimeslotsForMultipleFacilities(targetFacilityList, date);
                
                System.out.println("Deleted " + count + " slot(s) for [" + facilityName + "] on " + date.format(DATE_FORMAT));
            }
            
            case 2 -> {
                String roomType = readRoomType();
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
        if (timeslotList == null || timeslotList.isEmpty()) {
            System.out.println("\nNo timeslot found");
            return;
        }
                
        int timeslotSelection = readInt("Select slot No. to delete: ", 1, timeslotList.getNumberOfEntries());
        Timeslot chosenTimeslot = timeslotList.getEntry(timeslotSelection);

        if (chosenTimeslot.isBooked()) {
            System.out.println("\nCannot delete a BOOKED slot. Cancel the booking first");
            return;
        }
        
        if (chosenTimeslot.isBlocked()) {
            System.out.println("\nCannot delete a BLOCKED slot. Unblock the booking first");
            return;
        }

        boolean status = timeslotMaintenance.deleteOneTimeslot(chosenTimeslot.getTimeslotId());

        if (status) {
            System.out.println("\nSlot " + chosenTimeslot.getStartTime().format(TIME_FORMAT) + " deleted successfully");
        }
        else {
            System.out.println("\nFailed to delete slot");
        }
    }

    // -----------------------------------------
    // TABLE UTILITY
    // -----------------------------------------
    
    private SortedArrayList<Facility> printSlotTable(SortedArrayList<Timeslot> timeslotList, String title, LocalDate date) {
        System.out.println();
        printDivider("-", calcTableWidth());
        System.out.println(title);
        System.out.println("Legend:   Blank=Available    /=Booked    X=Blocked   -=Unavailable");

        System.out.printf("%-" + COLUMN_INDEX + "s | %-" + COLUMN_WIDTH_ROOM_NAME + "s | %-" + COLUMN_WIDTH_ROOM_TYPE + "s", "No.", "Room Name", "Room Type");
        
        for (int i = 1; i <= TIME_MARKS.getNumberOfEntries(); i++) {
            LocalTime mark = TIME_MARKS.getEntry(i);
            System.out.printf(" | %-" + COLUMN_WIDTH_START_TIME + "s", mark.format(TIME_FORMAT) + " - " + mark.plusMinutes(Timeslot.MINUTES_PER_BLOCK));
        }
        
        System.out.println();
        printDivider("-", calcTableWidth());

        if (timeslotList == null || timeslotList.isEmpty()) {
            System.out.println("(No slots found)");
            printDivider("=", calcTableWidth());
            
            return new SortedArrayList<Facility>();
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

            System.out.printf("%-" + COLUMN_INDEX + "d | %-" + COLUMN_WIDTH_ROOM_NAME + "s | %-" + COLUMN_WIDTH_ROOM_TYPE + "s", rowNum++, facility.getRoomName(), facility.getRoomTypeCode(facility.getRoomType()));
            
            for (int i = 1; i <= TIME_MARKS.getNumberOfEntries(); i++) {
                LocalTime mark = TIME_MARKS.getEntry(i);
                Timeslot timeslot = findSlot(timeslotList, facility, mark);
                String cell = buildCell(timeslot);
                System.out.printf(" | %-" + COLUMN_WIDTH_START_TIME + "s", cell);
            }
            
            System.out.println();
        }

        printDivider("-", calcTableWidth());
        System.out.println("Total rooms: " + seenFacilitilyList.getNumberOfEntries());
        printDivider("=", calcTableWidth());
        
        return seenFacilitilyList;
    }

    private Timeslot findSlot(SortedArrayList<Timeslot> timeslotList, Facility facility, LocalTime startTime) {
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            Timeslot timeslot = iterator.next();
            
            if (timeslot.getFacility().getFacilityId().equals(facility.getFacilityId()) && timeslot.getStartTime().equals(startTime)) {
                return timeslot;
            }
        }
        
        return null;
    }
    
    private String buildCell(Timeslot timeslot) {
        if (timeslot == null) return "      -      ";
        if (timeslot.isBlocked()) return "      X      ";
        if (timeslot.isBooked()) return "      /      ";
        return "             ";
    }

    private int calcTableWidth() {
        return COLUMN_INDEX + 3 + COLUMN_WIDTH_ROOM_NAME + 3 + COLUMN_WIDTH_ROOM_TYPE + (TIME_MARKS.getNumberOfEntries() * (COLUMN_WIDTH_START_TIME + 3));
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
                System.out.println("\nPlease enter a number between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid input. Please enter a number");
            }
        }
    }

    private Facility readFacility(SortedArrayList<Facility> facilityList) {
        System.out.println("\nAvailable facilities:");
        
        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility facility = facilityList.getEntry(i);
            
            System.out.printf("%2d. %-30s %-30s %s%n", i, facility.getFacilityName(), facility.getRoomType(), facility.getRoomName());
        }

        int choice = readInt("Select facility: ", 1, facilityList.getNumberOfEntries());
        
        return facilityList.getEntry(choice);
    }
    
    private String readFacilityName() {
        System.out.println("\nSelect Facility Name:");
        System.out.println("1. " + InputOutputHelper.FNAME_CYBER);
        System.out.println("2. " + InputOutputHelper.FNAME_LIBRARY);
        System.out.println("3. " + InputOutputHelper.FNAME_SPORTS);
        System.out.println("4. " + InputOutputHelper.FNAME_OTHER);
        System.out.println("0. Back");

        int choice = readInt("Select: ", 0, 4);

        return switch (choice) {
            case 1 -> InputOutputHelper.FNAME_CYBER;
            case 2 -> InputOutputHelper.FNAME_LIBRARY;
            case 3 -> InputOutputHelper.FNAME_SPORTS;
            case 4 -> InputOutputHelper.FNAME_OTHER;
            default -> null;
        };
    }

    private String readRoomType() {
        String facilityName = readFacilityName();
        if (facilityName == null) return null;

        SortedArrayList<String> roomTypeList = getRoomTypeListByFacilityName(facilityName);

        System.out.println("\nSelect Room Type:");
        
        for (int i = 1; i <= roomTypeList.getNumberOfEntries(); i++) {
            System.out.println(i + ". " + roomTypeList.getEntry(i));
        }
        
        System.out.println("0. Back");

        int choice = readInt("Select: ", 0, roomTypeList.getNumberOfEntries());
        if (choice == 0) return null;

        return roomTypeList.getEntry(choice);
    }

    private SortedArrayList<String> getRoomTypeListByFacilityName(String facilityName) {
        return switch (facilityName) {
            case InputOutputHelper.FNAME_CYBER -> InputOutputHelper.roomTypesCyberList;
            case InputOutputHelper.FNAME_LIBRARY -> InputOutputHelper.roomTypesLibraryList;
            case InputOutputHelper.FNAME_SPORTS -> InputOutputHelper.roomTypesSportsList;
            default -> InputOutputHelper.roomTypesOtherList;
        };
    }
    
    private LocalDate readDate() {
        LocalDate today    = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        System.out.println("\nChoose a date:");
        System.out.println("1. " + today.format(DATE_FORMAT));
        System.out.println("2. " + tomorrow.format(DATE_FORMAT));
        System.out.println("0. Back");
        
        int sel = readInt("Select date: ", 0, 2);
        if (sel == 0) return null;
        return sel == 1 ? today : tomorrow;
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
}
