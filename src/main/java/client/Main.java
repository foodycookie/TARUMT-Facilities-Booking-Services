package client;

import adt.SortedArrayList;
import boundary.TimeslotMaintenanceUI;
import control.FacilityMaintenance;
import entity.Facility;

/**
 *
 * @author TAY TIAN YOU
 */
public class Main {
    public static void main(String[] args) {
        TimeslotMaintenanceUI timeslotMaintenanceUI = new TimeslotMaintenanceUI();
        FacilityMaintenance facilityMaintenance = new FacilityMaintenance();
        
        Facility f1 = new Facility("O002", "Other", "Other", "Toilet - O2");
        Facility f2 = new Facility("S002", "Sports Facilities", "Pickleball", "Pickleball Court 2");
        Facility f3 = new Facility("C001", "Cyber Centre Room", "Discussion Room (1 PC)", "CC100");
        Facility f4 = new Facility("L001", "Library Room", "Discussion Room", "LD1");
        Facility f5 = new Facility("C002", "Cyber Centre Room", "Discussion Room (2 PCs)", "CC101");
        Facility f6 = new Facility("S001", "Sports Facilities", "Swimming Pool", "Swimming Pool Slot 1");
        Facility f7 = new Facility("S002", "Sports Facilities", "Pickleball", "Pickleball Court 1");
        Facility f8 = new Facility("L002", "Library Room", "Individual Study Room", "LI1");
        Facility f9 = new Facility("O001", "Other", "Other", "Toilet - O1");
        Facility f10 = new Facility("C003", "Cyber Centre Room", "Discussion Room (1 PC)", "CC102");
        
        facilityMaintenance.addFacility(f1);
        facilityMaintenance.addFacility(f2);
        facilityMaintenance.addFacility(f3);
        facilityMaintenance.addFacility(f4);
        facilityMaintenance.addFacility(f5);
        facilityMaintenance.addFacility(f6);
        facilityMaintenance.addFacility(f7);
        facilityMaintenance.addFacility(f8);
        facilityMaintenance.addFacility(f9);
        facilityMaintenance.addFacility(f10);
        
        SortedArrayList<Facility> facilityList = facilityMaintenance.getAllFacilities();
        
        if (!facilityList.isEmpty()) {
            timeslotMaintenanceUI.mainMenuForAdmin(facilityList, "A001", "Admin Test Hi");
        }
    
        else {
            System.out.println("Bye");
        }
        }
//    public void main(String[] args){
//         int choice;
//
//        do {
//            System.out.println("\n========== MAIN MENU ==========");
//            System.out.println("1. USER AND ADMIN MANAGEMENT");
//            System.out.println("2. FACILITY MANAGEMENT");
//            System.out.println("3. BOOKING");
//            System.out.println("4. TIME SLOT");
//            System.out.println("0. Exit");
//            System.out.print("Enter choice: ");
//            choice = InputOutputHelper.readInt();
//           
//            switch (choice) {
//                case 1 :
//                    UserAdminMaintenanceUI UserUi = new UserAdminMaintenanceUI();
//                    UserUi.start();
//                    
//                case 2 :
//                    FacilityMaintenanceUI FacilityUi = new FacilityMaintenanceUI();
//                    FacilityUi.start();
//                    
//                case 3 :
//                    BookingMaintenance booking = new BookingMaintenance();
//                    booking.runBooking();
//                    
//                case 4 :
//                    System.out.println("No link yet...");
//                    
//                case 0 :
//                    System.out.println("Exiting User Management Module...");
//                    
//                default :
//                    System.out.println("Invalid choice. Please try again.");
//            }
//        } while (choice != 0);
//
//
//    }
}