package boundary;

import entity.Booking;
import java.util.Scanner;

public class BookingMaintenanceUI {

    private Scanner scanner = new Scanner(System.in);

    // MENU
    public int getMenuChoice() {

        System.out.println("\n=== BOOKING MENU ===");
        System.out.println("1. Create Booking");
        System.out.println("2. Cancel Booking");
        System.out.println("3. Update Booking");
        System.out.println("4. Show Available Time Slots");
        System.out.println("5. View All Bookings");
        System.out.println("0. Exit");

        System.out.print("Enter choice: ");
        return scanner.nextInt();
    }

    // INPUT BOOKING DETAILS
    public Booking inputBookingDetails() {

        System.out.print("Booking ID: ");
        int bookingID = scanner.nextInt();

        System.out.print("User ID: ");
        int userID = scanner.nextInt();

        System.out.print("Facility ID: ");
        int facilityID = scanner.nextInt();

        System.out.print("Date (YYYY-MM-DD): ");
        String date = scanner.next();

        System.out.print("Time Slot (e.g. 08:00-10:00): ");
        String timeSlot = scanner.next();

        return new Booking(bookingID, userID, facilityID, date, timeSlot);
    }

    // INPUT BOOKING ID (for delete/update)
    public int inputBookingID() {
        System.out.print("Enter Booking ID: ");
        return scanner.nextInt();
    }

    // INPUT FACILITY ID
    public int inputFacilityID() {
        System.out.print("Enter Facility ID: ");
        return scanner.nextInt();
    }

    // INPUT DATE
    public String inputDate() {
        System.out.print("Enter Date (YYYY-MM-DD): ");
        return scanner.next();
    }

    // DISPLAY MESSAGE
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // DISPLAY ALL BOOKINGS
    public void displayAllBookings(String bookingList) {

        System.out.println("\n=== BOOKING LIST ===");
        System.out.println("ID\tUser\tFacility\tDate\tTime");

        if (bookingList == null || bookingList.isEmpty()) {
            System.out.println("No bookings found.");
        } else {
            System.out.println(bookingList);
        }
    }
}