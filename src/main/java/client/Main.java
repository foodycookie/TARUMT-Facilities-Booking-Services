package client;

import boundary.MainMenuUI;
import control.AdminMaintenance;
import control.FacilityMaintenance;
import control.UserMaintenance;
import entity.Admin;
import entity.Facility;
import entity.User;
import utility.InputOutputHelper;

/**
 * @author Lai Yu Hui, Ong Hao Howard, Tay Tian You, Tiw Hong Xuan
 */

public class Main {
    public static void main(String[] args) {                
        generateTestData();
                
        MainMenuUI mainMenu = new MainMenuUI();
        mainMenu.start();
    }
    
    public static void generateTestData() {
        FacilityMaintenance facilityMaintenance = new FacilityMaintenance();
        
        Facility f1 = new Facility("C001", InputOutputHelper.FNAME_CYBER, InputOutputHelper.roomTypesCyberList.getEntry(1), "CC100");
        Facility f2 = new Facility("C002", InputOutputHelper.FNAME_CYBER, InputOutputHelper.roomTypesCyberList.getEntry(2), "CC101");
        Facility f3 = new Facility("C003", InputOutputHelper.FNAME_CYBER, InputOutputHelper.roomTypesCyberList.getEntry(3), "CC102");
        Facility f4 = new Facility("C004", InputOutputHelper.FNAME_CYBER, InputOutputHelper.roomTypesCyberList.getEntry(1), "CC200");
        Facility f5 = new Facility("C005", InputOutputHelper.FNAME_CYBER, InputOutputHelper.roomTypesCyberList.getEntry(2), "CC201");
        
        Facility f6 = new Facility("L001", InputOutputHelper.FNAME_LIBRARY, InputOutputHelper.roomTypesLibraryList.getEntry(1), "L100");
        Facility f7 = new Facility("L002", InputOutputHelper.FNAME_LIBRARY, InputOutputHelper.roomTypesLibraryList.getEntry(2), "L101");
        Facility f8 = new Facility("L003", InputOutputHelper.FNAME_LIBRARY, InputOutputHelper.roomTypesLibraryList.getEntry(3), "L102");
        Facility f9 = new Facility("L004", InputOutputHelper.FNAME_LIBRARY, InputOutputHelper.roomTypesLibraryList.getEntry(4), "L200");
        Facility f10 = new Facility("L005", InputOutputHelper.FNAME_LIBRARY, InputOutputHelper.roomTypesLibraryList.getEntry(1), "L201");
        
        Facility f11 = new Facility("S001", InputOutputHelper.FNAME_SPORTS, InputOutputHelper.roomTypesSportsList.getEntry(1), "S1");
        Facility f12 = new Facility("S002", InputOutputHelper.FNAME_SPORTS, InputOutputHelper.roomTypesSportsList.getEntry(2), "S2");
        Facility f13 = new Facility("S003", InputOutputHelper.FNAME_SPORTS, InputOutputHelper.roomTypesSportsList.getEntry(3), "S3");
        Facility f14 = new Facility("S004", InputOutputHelper.FNAME_SPORTS, InputOutputHelper.roomTypesSportsList.getEntry(4), "S4");
        Facility f15 = new Facility("S005", InputOutputHelper.FNAME_SPORTS, InputOutputHelper.roomTypesSportsList.getEntry(1), "S5");
        
        Facility f16 = new Facility("O001", InputOutputHelper.FNAME_OTHER, InputOutputHelper.roomTypesOtherList.getEntry(1), "Toilet 1");
        Facility f17 = new Facility("O002", InputOutputHelper.FNAME_OTHER, InputOutputHelper.roomTypesOtherList.getEntry(1), "Toilet 2");
        Facility f18 = new Facility("O003", InputOutputHelper.FNAME_OTHER, InputOutputHelper.roomTypesOtherList.getEntry(1), "Random Room");
        
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
        facilityMaintenance.addFacility(f11);
        facilityMaintenance.addFacility(f12);
        facilityMaintenance.addFacility(f13);
        facilityMaintenance.addFacility(f14);
        facilityMaintenance.addFacility(f15);
        facilityMaintenance.addFacility(f16);
        facilityMaintenance.addFacility(f17);
        facilityMaintenance.addFacility(f18);
        
        UserMaintenance userMaintenance = new UserMaintenance();
        
        User u1 = new User("U000001", "TAY");
        User u2 = new User("U000002", "JASON");
        User u3 = new User("U000003", "SARAH");
        User u4 = new User("U000004", "DANIEL");
        User u5 = new User("U000005", "EMILY");

        userMaintenance.addUser(u1);
        userMaintenance.addUser(u2);
        userMaintenance.addUser(u3);
        userMaintenance.addUser(u4);
        userMaintenance.addUser(u5);
        
        AdminMaintenance adminMaintenance = new AdminMaintenance();

        Admin a1 = new Admin("A0001", "ALICE");
        Admin a2 = new Admin("A0002", "BOB");
        Admin a3 = new Admin("A0003", "CHARLIE");
        Admin a4 = new Admin("A0004", "DAVID");
        Admin a5 = new Admin("A0005", "EVELYN");

        adminMaintenance.addAdmin(a1);
        adminMaintenance.addAdmin(a2);
        adminMaintenance.addAdmin(a3);
        adminMaintenance.addAdmin(a4);
        adminMaintenance.addAdmin(a5);
    }
}