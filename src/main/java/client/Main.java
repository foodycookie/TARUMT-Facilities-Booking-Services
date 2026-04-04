package client;

import boundary.UserAdminMaintenanceUI;
import boundary.FacilityMaintenanceUI;
import control.BookingMaintenance;
import utility.Validation;

/**
 *
 * @author TAY TIAN YOU
 */
public class Main {
    public void main(String[] args){
         int choice;

        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. USER AND ADMIN MANAGEMENT");
            System.out.println("2. FACILITY MANAGEMENT");
            System.out.println("3. BOOKING");
            System.out.println("4. TIME SLOT");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = Validation.readInt();
           
            switch (choice) {
                case 1 :
                    UserAdminMaintenanceUI UserUi = new UserAdminMaintenanceUI();
                    UserUi.start();
                    
                case 2 :
                    FacilityMaintenanceUI FacilityUi = new FacilityMaintenanceUI();
                    FacilityUi.start();
                    
                case 3 :
                    BookingMaintenance booking = new BookingMaintenance();
                    booking.runBooking();
                    
                case 4 :
                    System.out.println("No link yet...");
                    
                case 0 :
                    System.out.println("Exiting User Management Module...");
                    
                default :
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }
}