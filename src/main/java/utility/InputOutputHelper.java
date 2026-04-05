package utility;

import adt.SortedArrayList;
import java.util.Scanner;

public class InputOutputHelper {
    public static final String FNAME_CYBER   = "Cyber Centre";
    public static final String FNAME_LIBRARY = "Library";
    public static final String FNAME_SPORTS  = "Sports Facilities";
    public static final String FNAME_OTHER   = "Other";

    public static final SortedArrayList<String> roomTypesCyberList = new SortedArrayList<>();
    public static final SortedArrayList<String> roomTypesLibraryList = new SortedArrayList<>();
    public static final SortedArrayList<String> roomTypesSportsList = new SortedArrayList<>();
    public static final SortedArrayList<String> roomTypesOtherList = new SortedArrayList<>();

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
    
    public static void displayInvalidChoiceMessage() {
        System.out.println("\nInvalid choice");
    }

    public static void displayExitMessage() {
        System.out.println("\nExiting system");
    }
  
    public static boolean isValidStudentId(String id) {
        return id != null && id.matches("\\d{2}[A-Z]{3}\\d{5}");
    }

    public static boolean isValidStaffId(String id) {
        return id != null && id.matches("P\\d{4}");
    }

    public static boolean isValidUserName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public static boolean isValidRole(String role) {
        return role != null &&
               (role.equalsIgnoreCase("Student") || role.equalsIgnoreCase("Staff"));
    }
    
    public static int readInt() {
        
     Scanner scanner = new Scanner(System.in);
    
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.nextLine();
        }

        int value = scanner.nextInt();
        scanner.nextLine(); // clear buffer
        return value; 
    }
}
