package boundary;

import adt.SortedArrayList;
import control.AdminMaintenance;
import control.FacilityMaintenance;
import control.UserMaintenance;
import entity.Admin;
import entity.Facility;
import entity.User;
import java.util.Scanner;

/**
 *
 * @author TAY TIAN YOU
 */
public class MainMenuUI {

    private final Scanner scanner = new Scanner(System.in);

    private final UserAdminMaintenanceUI userAdminUI = new UserAdminMaintenanceUI();
    private final FacilityMaintenanceUI facilityUI = new FacilityMaintenanceUI();
    private final TimeslotMaintenanceUI timeslotUI = new TimeslotMaintenanceUI();
    private final BookingMaintenanceUI bookingUI = new BookingMaintenanceUI();

    private final UserMaintenance userControl = new UserMaintenance();
    private final AdminMaintenance adminControl = new AdminMaintenance();
    private final FacilityMaintenance facilityControl = new FacilityMaintenance();

    public void start() {
        int choice;

        do {
            System.out.println("\n==================================================");
            System.out.println("         FACILITIES BOOKING SERVICES");
            System.out.println("==================================================");
            showCurrentLoginStatus();
            System.out.println("1. User / Admin Module");
            System.out.println("2. Facility Module");
            System.out.println("3. Timeslot Module");
            System.out.println("4. Booking Module");
            System.out.println("0. Exit");
            System.out.println("==================================================");
            System.out.print("Enter choice: ");

            choice = readChoice(0, 4);

            switch (choice) {
                case 1 -> userAdminUI.start();
                case 2 -> facilityUI.start();
                case 3 -> openTimeslotModule();
                case 4 -> openBookingModule();
                case 0 -> System.out.println("Exiting system...");
            }

        } while (choice != 0);
    }

    private void showCurrentLoginStatus() {
        User currentUser = userControl.getCurrentUser();
        Admin currentAdmin = adminControl.getCurrentAdmin();

        if (currentAdmin != null) {
            System.out.println("Current Login: Admin - " 
                    + currentAdmin.getAdminName() + " (" + currentAdmin.getAdminId() + ")");
        } else if (currentUser != null) {
            System.out.println("Current Login: User - " 
                    + currentUser.getUserName() + " (" + currentUser.getUserId() + ")");
        } else {
            System.out.println("Current Login: None");
        }
    }

    private void openTimeslotModule() {
        Admin currentAdmin = adminControl.getCurrentAdmin();
        User currentUser = userControl.getCurrentUser();
        SortedArrayList<Facility> facilityList = facilityControl.getAllFacilities();

        if (currentAdmin != null) {
            timeslotUI.mainMenuForAdmin(
                    facilityList,
                    currentAdmin.getAdminId(),
                    currentAdmin.getAdminName()
            );
        } else if (currentUser != null) {
            timeslotUI.mainMenuForUser();
        } else {
            System.out.println("\nPlease select a current User or Admin first in User / Admin Module.");
        }
    }

    private void openBookingModule() {
        User currentUser = userControl.getCurrentUser();

        if (currentUser == null) {
            System.out.println("\nPlease select a current User first in User / Admin Module.");
            return;
        }

        bookingUI.start();
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
}