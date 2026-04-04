package utility;

import java.util.Scanner;

public class Validation {  
    
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
