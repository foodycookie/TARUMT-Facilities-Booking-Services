package boundary;

import adt.SortedArrayList;
import control.BookingMaintenance;
import control.FacilityMaintenance;
import control.TimeslotMaintenance;
import control.UserMaintenance;
import entity.Facility;
import entity.Timeslot;
import entity.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * @author Lai Yu Hui
 */

public class BookingMaintenanceUI {
    private final Scanner scanner = new Scanner(System.in);
    private final BookingMaintenance bookingControl = new BookingMaintenance();
    private final FacilityMaintenance facilityControl = new FacilityMaintenance();
    private TimeslotMaintenance timeslotControl = new TimeslotMaintenance();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public void start() {
        int choice;

        do {
            System.out.println("\n========== BOOKING MENU ==========");
            System.out.println("1. Add Booking");
            System.out.println("2. Display Current Booking");
            System.out.println("3. Cancel Booking");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            choice = readChoice(0, 3);

            switch (choice) {
                case 1 -> addBookingFlow();
                case 2 -> displayCurrentBookingFlow();
                case 3 -> cancelBookingFlow();
                case 0 -> System.out.println("Returning...");
            }
        } while (choice != 0);
    }

    private void addBookingFlow() {
        User currentUser = UserMaintenance.currentUser;

        if (currentUser == null) {
            System.out.println("\nNo current user selected. Please select user first in User module.");
            return;
        }

        System.out.println("\n========== ADD BOOKING ==========");
        System.out.println("Current User: " + currentUser.getUserName() + " (" + currentUser.getUserId() + ")");

        SortedArrayList<Facility> facilityList = facilityControl.getAllFacilities();

        if (facilityList == null || facilityList.isEmpty()) {
            System.out.println("No facilities found.");
            return;
        }

        Facility chosenFacility = readFacility(facilityList);
        if (chosenFacility == null) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        System.out.println("\nChoose booking date:");
        System.out.println("1. " + today.format(DATE_FORMAT));
        System.out.println("2. " + tomorrow.format(DATE_FORMAT));
        System.out.print("Enter choice: ");
        int dateChoice = readChoice(1, 2);

        LocalDate selectedDate = (dateChoice == 1) ? today : tomorrow;

        SortedArrayList<Timeslot> timeslotList = timeslotControl.getTimeslotsForOneFacility(chosenFacility, selectedDate);

        if (timeslotList == null || timeslotList.isEmpty()) {
            System.out.println("\nNo timeslot found for selected facility and date.");
            return;
        }

        printTimeslotList(timeslotList, chosenFacility, selectedDate);

        boolean booked = menuBookForChosenFacility(
                chosenFacility,
                timeslotList,
                currentUser.getUserId(),
                currentUser.getUserName()
        );

        if (booked) {
            SortedArrayList<Timeslot> refreshedList =
                    timeslotControl.getTimeslotsForOneFacility(chosenFacility, selectedDate);
        }
    }

    private void displayCurrentBookingFlow() {
        System.out.println(bookingControl.displayCurrentUserBookings());
    }

    private void cancelBookingFlow() {
        User currentUser = UserMaintenance.currentUser;

        if (currentUser == null) {
            System.out.println("\nNo current user selected. Please select user first in User module.");
            return;
        }

        System.out.println("\n========== CANCEL BOOKING ==========");
        System.out.println("Current User: " + currentUser.getUserName() + " (" + currentUser.getUserId() + ")");

        System.out.println(bookingControl.displayCurrentUserBookings());

        System.out.print("Enter Booking ID to cancel: ");
        String bookingId = scanner.nextLine().trim();

        String confirm = askYesNo("Confirm cancel booking? (Y/N): ");

        if (confirm.equalsIgnoreCase("Y")) {
            boolean cancelled = bookingControl.cancelCurrentUserBooking(bookingId);

            if (cancelled) {
                timeslotControl.reloadFromFile();
                System.out.println("Booking cancelled successfully.");
            } else {
                System.out.println("Failed to cancel booking.");
            }
        } else {
            System.out.println("Cancel operation aborted.");
        }
    }

    private boolean menuBookForChosenFacility(Facility facility, SortedArrayList<Timeslot> timeslotList, String userId, String userName) {
        if (timeslotList == null || timeslotList.isEmpty()) {
            System.out.println("\nNo timeslot found");
            return false;
        }

        System.out.print("Select slot No. to book: ");
        int timeslotSelection = readChoice(1, timeslotList.getNumberOfEntries());
        Timeslot chosenTimeslot = timeslotList.getEntry(timeslotSelection);

        if (chosenTimeslot.isBooked()) {
            System.out.println("\nSlot is already BOOKED");
            return false;
        }

        if (chosenTimeslot.isBlocked()) {
            System.out.println("\nSlot is already BLOCKED");
            return false;
        }

        System.out.println("\n---------- BOOKING PREVIEW ----------");
        System.out.println("User ID      : " + userId);
        System.out.println("User Name    : " + userName);
        System.out.println("Facility ID  : " + facility.getFacilityId());
        System.out.println("Facility Name: " + facility.getFacilityName());
        System.out.println("Room Type    : " + facility.getRoomType());
        System.out.println("Room Name    : " + facility.getRoomName());
        System.out.println("Date         : " + chosenTimeslot.getDate());
        System.out.println("Timeslot ID  : " + chosenTimeslot.getTimeslotId());
        System.out.println("Time         : " + chosenTimeslot.getStartTime().format(TIME_FORMAT)
                + " - " + chosenTimeslot.getEndTime().format(TIME_FORMAT));
        System.out.println("-------------------------------------");

        String confirm = askYesNo("Confirm booking? (Y/N): ");

        if (confirm.equalsIgnoreCase("Y")) {
            boolean booked = bookingControl.addBooking(chosenTimeslot);

            if (booked) {
                System.out.println("Booking added successfully.");
                return true;
            } else {
                System.out.println("Failed to add booking.");
                return false;
            }
        } else {
            System.out.println("Booking cancelled.");
            return false;
        }
    }

    private void printTimeslotList(SortedArrayList<Timeslot> timeslotList, Facility facility, LocalDate date) {
        timeslotControl = new TimeslotMaintenance();
        System.out.println("\n========== AVAILABLE TIMESLOTS ==========");
        System.out.println("Facility : " + facility.getRoomName());
        System.out.println("Date     : " + date.format(DATE_FORMAT));
        System.out.printf("%-4s %-10s %-10s %-12s%n", "No.", "Start", "End", "Status");

        for (int i = 1; i <= timeslotList.getNumberOfEntries(); i++) {
            Timeslot timeslot = timeslotList.getEntry(i);

            String status = switch (timeslot.getStatus()) {
                case BOOKED -> "BOOKED";
                case BLOCKED -> "BLOCKED";
                default -> "AVAILABLE";
            };

            System.out.printf("%-4d %-10s %-10s %-12s%n",
                    i,
                    timeslot.getStartTime().format(TIME_FORMAT),
                    timeslot.getEndTime().format(TIME_FORMAT),
                    status);
        }
    }

    private Facility readFacility(SortedArrayList<Facility> facilityList) {
        System.out.println("\nAvailable Facilities:");

        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility facility = facilityList.getEntry(i);
            System.out.printf("%2d. %-30s %-30s %s%n",
                    i,
                    facility.getFacilityName(),
                    facility.getRoomType(),
                    facility.getRoomName());
        }

        System.out.print("Select facility: ");
        int choice = readChoice(1, facilityList.getNumberOfEntries());

        return facilityList.getEntry(choice);
    }

    private int readChoice(int min, int max) {
        int choice;

        while (true) {
            while (!scanner.hasNextInt()) {
                System.out.print("Invalid input. Enter a number: ");
                scanner.nextLine();
            }

            choice = scanner.nextInt();
            scanner.nextLine();

            if (choice >= min && choice <= max) {
                return choice;
            }

            System.out.print("Invalid choice. Enter " + min + " to " + max + ": ");
        }
    }

    private String askYesNo(String prompt) {
        String answer;

        while (true) {
            System.out.print(prompt);
            answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("N")) {
                return answer;
            }

            System.out.println("Invalid input. Enter Y or N.");
        }
    }
}