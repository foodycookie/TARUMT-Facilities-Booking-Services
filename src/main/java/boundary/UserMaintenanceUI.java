package boundary;

import control.UserMaintenance;
import entity.User;
import java.util.Scanner;

/**
 *
 * @author TAY TIAN YOU
 */
public class UserMaintenanceUI {
    private Scanner scanner = new Scanner(System.in);
    private UserMaintenance userMaintenance = new UserMaintenance(); 
    
    public void start(){
        int choice;

        do {
            System.out.println("\n========== USER MANAGEMENT MENU ==========");
            System.out.println("1. Display All Users");
            System.out.println("2. Add User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> displayUserMenu();
                case 2 -> addUser();
                case 3 -> updateUser();
                case 4 -> deleteUser();
                case 0 -> System.out.println("Exiting User Management Module...");    
                        //WILL BE CHANGE like Redirect to Timetable  
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private int readInt() {
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Enter a number: ");
            scanner.nextLine();
        }

        int value = scanner.nextInt();
        scanner.nextLine(); // clear buffer
        return value; 
    }

    private void displayUserMenu() {
        int choice;

            do {
                System.out.println(userMaintenance.displayAllUsers());
                System.out.println("1. Update User");
                System.out.println("2. Delete User");
                System.out.println("3. Back");
                System.out.print("Enter choice: ");
                choice = readInt();

                switch (choice) {
                    case 1 -> updateUser();
                    case 2 -> deleteUser();
                    case 3 -> System.out.println("Returning to main menu...");
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 3);    
    }

    private void addUser() {
        int choice;

        do {
            System.out.println("\n============ ADD USER ===========");

            System.out.print("Enter Student ID (nnwwwnnnnn): ");
            String studentId = scanner.nextLine().trim();

            if (!userMaintenance.isValidStudentId(studentId)) {
                System.out.println("Invalid Student ID format. Example: 24ABC12345");
                choice = 2;
                continue;
            }

            if (userMaintenance.studentIdExists(studentId)) {
                System.out.println("Student ID already exists.");
                choice = 2;
                continue;
            }

            System.out.print("Enter Student Name: ");
            String studentName = scanner.nextLine().trim();

            if (!userMaintenance.isValidStudentName(studentName)) {
                System.out.println("Student name cannot be empty.");
                choice = 2;
                continue;
            }

            String generatedUserId = userMaintenance.generateUserId();
            User tempUser = new User(generatedUserId, studentId, studentName);

            System.out.println("\n----------- USER PROFILE PREVIEW ------------");
            System.out.println(tempUser);
            System.out.println("---------------------------------------------");
            System.out.println("1. Confirm and Save");
            System.out.println("2. Re-enter Details");
            System.out.println("3. Cancel");
            System.out.print("Enter choice: ");
            choice = readInt();

            switch (choice) {
                case 1 -> {
                    boolean added = userMaintenance.addUser(tempUser);
                    if (added) {
                        System.out.println("User added successfully.");

                        int nextChoice;
                        do {
                            System.out.println("\n1. Add Another User");
                            System.out.println("2. Back");
                            System.out.print("Enter choice: ");
                            nextChoice = readInt();
                            
                            if(nextChoice != 1 && nextChoice != 2){
                                System.out.println("Invalid choice.");
                            }
                        } while (nextChoice != 1 && nextChoice != 2);
                        
                        if (nextChoice == 1) {
                            addUser();
                        }else{
                            System.out.println("Returning to main menu...");
                            return;
                        }
                    } else {
                        System.out.println("Failed to add user.");
                        choice = 1;
                    }
                }

                case 2 -> System.out.println("Please re-enter user details.");

                case 3 -> System.out.println("Add user cancelled.");

                default -> {
                    System.out.println("Invalid choice.");
                    choice = 2;
                }
            }
        } while (choice == 2);
        
    }

    private void deleteUser() {
        System.out.println("\n=========== DELETE USER ============");
        System.out.println(userMaintenance.displayAllUsers());
        System.out.print("Enter User ID to delete: ");
        String userId = scanner.nextLine().trim();

        User existingUser = userMaintenance.findUserByUserId(userId);

        if (existingUser == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nUser Record to Delete");
        System.out.println(existingUser);

        System.out.println("\n1. Confirm Delete");
        System.out.println("2. Cancel");
        System.out.print("Enter choice: ");
        int choice = readInt();

        if (choice == 1) {
            boolean deleted = userMaintenance.deleteUser(userId);
            if (deleted) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Failed to delete user.");
            }
        } else {
            System.out.println("Delete cancelled.");
        }
    }

    private void updateUser() {
        System.out.println("\n=========== UPDATE USER ============");
        System.out.print("Enter User ID to update: ");
        String userId = scanner.nextLine().trim();

        User existingUser = userMaintenance.findUserByUserId(userId);

        if (existingUser == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nCurrent User Information");
        System.out.println(existingUser);

        // ===== Student ID =====
        System.out.print("\nEnter New Student ID (press Enter to keep current): ");
        String newStudentId = scanner.nextLine().trim();

        if (newStudentId.isEmpty()) {
            newStudentId = existingUser.getStudentId();
        } else {
            if (!userMaintenance.isValidStudentId(newStudentId)) {
                System.out.println("Invalid Student ID format.");
                return;
            }

            User duplicateStudent = userMaintenance.findUserByStudentId(newStudentId);
            if (duplicateStudent != null &&
                !duplicateStudent.getUserId().equalsIgnoreCase(userId)) {
                System.out.println("Student ID already exists.");
                return;
            }
        }

        // ===== Student Name =====
        System.out.print("Enter New Student Name (press Enter to keep current): ");
        String newStudentName = scanner.nextLine().trim();

        if (newStudentName.isEmpty()) {
            newStudentName = existingUser.getStudentName();
        }

        System.out.println("\n----------- UPDATED USER PREVIEW ------------");
        System.out.println("User ID    : " + userId);
        System.out.println("Student ID : " + newStudentId);
        System.out.println("Name       : " + newStudentName);
        System.out.println("---------------------------------------------");
        System.out.println("1. Confirm Update");
        System.out.println("2. Cancel");
        System.out.print("Enter choice: ");
        int choice = readInt();

        if (choice == 1) {
            boolean updated = userMaintenance.updateUser(userId, newStudentId, newStudentName);

            if (updated) {
                System.out.println("User updated successfully.");
            } else {
                System.out.println("Failed to update user.");
            }
        } else {
            System.out.println("Update cancelled.");
        }
    }
}
    