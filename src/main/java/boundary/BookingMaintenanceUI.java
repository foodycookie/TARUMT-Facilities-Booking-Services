package boundary;

import control.BookingMaintenance;
import control.FacilityMaintenance;
import control.UserMaintenance;
import entity.Facility;
import entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 *
 * @author TAY TIAN YOU
 */
public class BookingMaintenanceUI {

    private final Scanner scanner = new Scanner(System.in);
    private final BookingMaintenance bookingControl = new BookingMaintenance();
    private final FacilityMaintenance facilityControl = new FacilityMaintenance();
    private final UserMaintenance userControl = new UserMaintenance();

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public void start() {
        int choice;

        do {
            System.out.println("\n========== BOOKING MENU ==========");
            System.out.println("1. Add Booking");
            System.out.println("2. Display Current Booking");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Display All Bookings");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            choice = readChoice(0, 4);

            switch (choice) {
                case 1 -> addBookingFlow();
                case 2 -> displayCurrentBookingFlow();
                case 3 -> cancelBookingFlow();
                case 4 -> System.out.println(bookingControl.displayAllBookings());
                case 0 -> System.out.println("Returning...");
            }

        } while (choice != 0);
    }

    private void addBookingFlow() {
        System.out.println("\n========== ADD BOOKING ==========");

        User currentUser = userControl.getCurrentUser();
        String userId;

        if (currentUser != null) {
            System.out.println("Current User: " + currentUser.getUserName() + " (" + currentUser.getUserId() + ")");
            String useCurrent = askYesNo("Use current user? (Y/N): ");

            if (useCurrent.equalsIgnoreCase("Y")) {
                userId = currentUser.getUserId();
            } else {
                System.out.print("Enter User ID: ");
                userId = scanner.nextLine().trim();
            }
        } else {
            System.out.print("Enter User ID: ");
            userId = scanner.nextLine().trim();
        }

        if (!bookingControl.isExistingUser(userId)) {
            System.out.println("User ID does not exist in user.dat.");
            return;
        }

        System.out.println("\nAvailable Facilities:");
        System.out.println(facilityControl.displayAllFacilities());

        System.out.print("Enter Facility ID: ");
        String facilityId = scanner.nextLine().trim().toUpperCase();

        Facility facility = bookingControl.findFacility(facilityId);

        if (facility == null) {
            System.out.println("Facility not found.");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);

        System.out.println("\nChoose Booking Date:");
        System.out.println("1. " + today.format(DISPLAY_DATE_FORMAT));
        System.out.println("2. " + tomorrow.format(DISPLAY_DATE_FORMAT));
        System.out.print("Enter choice: ");
        int dateChoice = readChoice(1, 2);

        LocalDate selectedDate = (dateChoice == 1) ? today : tomorrow;

        System.out.println("\nChoose Start Time:");
        int startIndex = 1;
        LocalTime[] startTimes = new LocalTime[10];
        LocalTime timeCursor = LocalTime.of(9, 0);

        for (int i = 0; i < 10; i++) {
            startTimes[i] = timeCursor;
            System.out.println((i + 1) + ". " + timeCursor.format(TIME_FORMAT));
            timeCursor = timeCursor.plusHours(1);
        }

        System.out.print("Enter choice: ");
        int startChoice = readChoice(1, 10);
        LocalTime startTime = startTimes[startChoice - 1];

        System.out.println("\nDuration:");
        System.out.println("1. 1 hour");
        System.out.println("2. 2 hours");
        System.out.print("Enter choice: ");
        int durationChoice = readChoice(1, 2);

        if (durationChoice == 2 && startTime.plusHours(2).isAfter(LocalTime.of(19, 0))) {
            System.out.println("2-hour booking exceeds operation time.");
            return;
        }

        String date = selectedDate.format(DATE_FORMAT);
        String timeSlot = bookingControl.buildTimeSlot(startTime, durationChoice);

        if (bookingControl.hasBookingConflict(facilityId, date, timeSlot)) {
            System.out.println("Booking conflict detected. Selected time slot is already taken.");
            return;
        }

        System.out.println("\n---------- BOOKING PREVIEW ----------");
        System.out.println("User ID      : " + userId);
        System.out.println("Facility ID  : " + facility.getFacilityId());
        System.out.println("Facility Name: " + facility.getFacilityName());
        System.out.println("Room Type    : " + facility.getRoomType());
        System.out.println("Room Name    : " + facility.getRoomName());
        System.out.println("Date         : " + date);
        System.out.println("Time Slot    : " + timeSlot);
        System.out.println("-------------------------------------");

        String confirm = askYesNo("Confirm booking? (Y/N): ");

        if (confirm.equalsIgnoreCase("Y")) {
            boolean added = bookingControl.addBooking(userId, facilityId, date, timeSlot);

            if (added) {
                System.out.println("Booking added successfully.");
            } else {
                System.out.println("Failed to add booking.");
            }
        } else {
            System.out.println("Booking cancelled.");
        }
    }

    private void displayCurrentBookingFlow() {
        System.out.println("\n====== DISPLAY CURRENT BOOKING ======");

        User currentUser = userControl.getCurrentUser();
        String userId;

        if (currentUser != null) {
            System.out.println("Current User: " + currentUser.getUserName() + " (" + currentUser.getUserId() + ")");
            String useCurrent = askYesNo("Display booking for current user? (Y/N): ");

            if (useCurrent.equalsIgnoreCase("Y")) {
                userId = currentUser.getUserId();
            } else {
                System.out.print("Enter User ID: ");
                userId = scanner.nextLine().trim();
            }
        } else {
            System.out.print("Enter User ID: ");
            userId = scanner.nextLine().trim();
        }

        if (!bookingControl.isExistingUser(userId)) {
            System.out.println("User ID does not exist.");
            return;
        }

        System.out.println(bookingControl.displayBookingsByUser(userId));
    }

    private void cancelBookingFlow() {
        System.out.println("\n========== CANCEL BOOKING ==========");

        User currentUser = userControl.getCurrentUser();
        String userId;

        if (currentUser != null) {
            System.out.println("Current User: " + currentUser.getUserName() + " (" + currentUser.getUserId() + ")");
            String useCurrent = askYesNo("Cancel booking for current user? (Y/N): ");

            if (useCurrent.equalsIgnoreCase("Y")) {
                userId = currentUser.getUserId();
            } else {
                System.out.print("Enter User ID: ");
                userId = scanner.nextLine().trim();
            }
        } else {
            System.out.print("Enter User ID: ");
            userId = scanner.nextLine().trim();
        }

        if (!bookingControl.isExistingUser(userId)) {
            System.out.println("User ID does not exist.");
            return;
        }

        System.out.println(bookingControl.displayBookingsByUser(userId));

        System.out.print("Enter Booking ID to cancel: ");
        String bookingId = scanner.nextLine().trim().toUpperCase();

        String confirm = askYesNo("Confirm cancel booking? (Y/N): ");

        if (confirm.equalsIgnoreCase("Y")) {
            boolean cancelled = bookingControl.cancelBookingByUser(bookingId, userId);

            if (cancelled) {
                System.out.println("Booking cancelled successfully.");
            } else {
                System.out.println("Failed to cancel booking. Booking may not exist or does not belong to the user.");
            }
        } else {
            System.out.println("Cancel operation aborted.");
        }
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