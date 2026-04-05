package boundary;

import control.AdminMaintenance;
import control.UserMaintenance;
import entity.Admin;
import entity.User;
import java.util.Scanner;

/**
 * @author Tay Tiaan You
 */

public class UserAdminMaintenanceUI {
    private final Scanner scanner = new Scanner(System.in);
    private final UserMaintenance userControl;
    private final AdminMaintenance adminControl;

    public UserAdminMaintenanceUI(UserMaintenance userControl, AdminMaintenance adminControl) {
        this.userControl = userControl;
        this.adminControl = adminControl;
    }    

    public void start() {
        int choice;

        do {
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. User");
            System.out.println("2. Admin");
            System.out.println("0. Back");
            System.out.print("Choose your role: ");
            choice = readChoice(0, 2);

            switch (choice) {
                case 1 -> userMenu();
                case 2 -> adminMenu();
                case 0 -> System.out.println("Returning to Main Menu..");
            }
        } while (choice != 0);
    }

    private void userMenu() {
        int choice;

        do {
            System.out.println("\n========= USER MENU ==========");
            System.out.println("1. Add new User");
            System.out.println("2. Select current user");
            System.out.println("0. Back");
            System.out.print("Enter a number: ");
            choice = readChoice(0, 2);

            switch (choice) {
                case 1 -> addUserFlow();
                case 2 -> selectUserFlow();
                case 0 -> System.out.println("Returning to Main Menu...");
            }
        } while (choice != 0);
    }

    private void adminMenu() {
        int choice;

        do {
            System.out.println("\n========= ADMIN MENU ==========");
            System.out.println("1. Add new Admin");
            System.out.println("2. Select current admin");
            System.out.println("0. Back");
            System.out.print("Enter a number: ");
            choice = readChoice(0, 2);

            switch (choice) {
                case 1 -> addAdminFlow();
                case 2 -> selectAdminFlow();
                case 0 -> System.out.println("Returning to Main Menu...");
            }
        } while (choice != 0);
    }

    private void addUserFlow() {
        String repeat;

        do {
            String generatedUserId = userControl.generateUserId();

            System.out.println("\n========== ADD NEW USER ==========");
            System.out.println("Generated User ID: " + generatedUserId);

            System.out.print("Enter User Name: ");
            String userName = scanner.nextLine().trim();

            if (!userControl.isValidUserName(userName)) {
                System.out.println("Name cannot be empty.");
                repeat = "Y";
                continue;
            }

            User tempUser = new User(generatedUserId, userName);

            System.out.println("\n---------- TEMP PROFILE ----------");
            System.out.println(tempUser);
            System.out.println("----------------------------------");
            System.out.println("1. Confirm and Save");
            System.out.println("2. Re-enter");
            System.out.println("3. Cancel");
            System.out.print("Enter choice: ");
            int confirmChoice = readChoice(1, 3);

            switch (confirmChoice) {
                case 1 -> {
                    boolean added = userControl.addUser(tempUser);
                    if (added) {
                        System.out.println("User added successfully.");
                        repeat = askYesNo("Add another user? (Y/N): ");
                    } else {
                        System.out.println("Failed to add user.");
                        repeat = askYesNo("Try again? (Y/N): ");
                    }
                }
                case 2 -> repeat = "Y";
                default -> {
                    System.out.println("Add user cancelled.");
                    repeat = "N";
                }
            }

        } while (repeat.equalsIgnoreCase("Y"));
    }

    private void addAdminFlow() {
        String repeat;

        do {
            String generatedAdminId = adminControl.generateAdminId();

            System.out.println("\n========== ADD NEW ADMIN ==========");
            System.out.println("Generated Admin ID: " + generatedAdminId);

            System.out.print("Enter Admin Name: ");
            String adminName = scanner.nextLine().trim();

            if (!adminControl.isValidAdminName(adminName)) {
                System.out.println("Name cannot be empty.");
                repeat = "Y";
                continue;
            }

            Admin tempAdmin = new Admin(generatedAdminId, adminName);

            System.out.println("\n---------- TEMP PROFILE ----------");
            System.out.println(tempAdmin);
            System.out.println("----------------------------------");
            System.out.println("1. Confirm and Save");
            System.out.println("2. Re-enter");
            System.out.println("3. Cancel");
            System.out.print("Enter choice: ");
            int confirmChoice = readChoice(1, 3);

            switch (confirmChoice) {
                case 1 -> {
                    boolean added = adminControl.addAdmin(tempAdmin);
                    if (added) {
                        System.out.println("Admin added successfully.");
                        repeat = askYesNo("Add another admin? (Y/N): ");
                    } else {
                        System.out.println("Failed to add admin.");
                        repeat = askYesNo("Try again? (Y/N): ");
                    }
                }
                case 2 -> repeat = "Y";
                default -> {
                    System.out.println("Add admin cancelled.");
                    repeat = "N";
                }
            }

        } while (repeat.equalsIgnoreCase("Y"));
    }

    private void selectUserFlow() {
        System.out.println("\n=========== SELECT USER ===========");
        System.out.print("Enter your User ID: ");
        String userId = scanner.nextLine().trim();
        
        boolean selected = userControl.selectCurrentUser(userId);
        
        if (!selected) {
            System.out.println("User not found.");
            return;
        }

        User currentUser = UserMaintenance.currentUser;

        int choice;
        do {
            System.out.println("\n============= " + currentUser.getUserName() + " =============");
            System.out.println("1. Profile");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            choice = readChoice(0, 2);

            switch (choice) {
                case 1 -> userProfileMenu(currentUser);
                case 0 -> System.out.println("Returning to User Menu...");
            }

            currentUser = UserMaintenance.currentUser   ;
        } while (choice != 0);
    }

    private void selectAdminFlow() {
        System.out.println("\n=========== SELECT ADMIN ===========");
        System.out.print("Enter your Admin ID: ");
        String adminId = scanner.nextLine().trim();
        
        boolean selected = adminControl.selectCurrentAdmin(adminId);
        
        if (!selected) {
            System.out.println("Admin not found.");
            return;
        }

        Admin currentAdmin = AdminMaintenance.currentAdmin;

        int choice;
        do {
            System.out.println("\n============= " + currentAdmin.getAdminName() + " =============");
            System.out.println("1. Profile");
            System.out.println("2. Manage User");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            choice = readChoice(0, 3);

            switch (choice) {
                case 1 -> adminProfileMenu(currentAdmin);
                case 2 -> adminManageUserMenu();
                case 0 -> System.out.println("Returning to Admin Menu...");
            }

            currentAdmin = AdminMaintenance.currentAdmin;
        } while (choice != 0);
    }

    private void userProfileMenu(User currentUser) {
        int choice;

        do {
            System.out.println("\n========== USER PROFILE ==========");
            System.out.println(currentUser);
            System.out.println("\n1. Update");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            choice = readChoice(0, 1);

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new name (press Enter to keep current): ");
                    String newName = scanner.nextLine().trim();

                    if (newName.isEmpty()) {
                        newName = currentUser.getUserName();
                    }

                    boolean updated = userControl.updateUserName(currentUser.getUserId(), newName);

                    if (updated) {
                        System.out.println("User profile updated successfully.");
                        currentUser = userControl.findUserByUserId(currentUser.getUserId());
                    } else {
                        System.out.println("Failed to update user profile.");
                    }
                }
                case 0 -> System.out.println("Returning...");
            }
        } while (choice != 0);
    }

    private void adminProfileMenu(Admin currentAdmin) {
        int choice;

        do {
            System.out.println("\n========== ADMIN PROFILE ==========");
            System.out.println(currentAdmin);
            System.out.println("\n1. Update");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            choice = readChoice(0, 1);

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter new name (press Enter to keep current): ");
                    String newName = scanner.nextLine().trim();

                    if (newName.isEmpty()) {
                        newName = currentAdmin.getAdminName();
                    }

                    boolean updated = adminControl.updateAdminName(currentAdmin.getAdminId(), newName);

                    if (updated) {
                        System.out.println("Admin profile updated successfully.");
                        currentAdmin = adminControl.findAdminByAdminId(currentAdmin.getAdminId());
                    } else {
                        System.out.println("Failed to update admin profile.");
                    }
                }
                case 0 -> System.out.println("Returning...");
            }
        } while (choice != 0);
    }

    private void adminManageUserMenu() {
        int choice;

        do {
            System.out.println("\n========== USER MANAGEMENT MENU ==========");
            System.out.println("1. Display All User");
            System.out.println("2. Add User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("0. Back");
            System.out.print("Enter choice: ");
            choice = readChoice(0, 4);

            switch (choice) {
                case 1 -> System.out.println(userControl.displayAllUsers());
                case 2 -> addUserFlow();
                case 3 -> adminUpdateUserFlow();
                case 4 -> adminDeleteUserFlow();
                case 0 -> System.out.println("Returning to Admin Page...");
            }
        } while (choice != 0);
    }

    private void adminUpdateUserFlow() {
        System.out.print("Enter User ID to update: ");
        String userId = scanner.nextLine().trim();

        User existingUser = userControl.findUserByUserId(userId);

        if (existingUser == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nCurrent User Information");
        System.out.println(existingUser);

        System.out.print("\nEnter new name (press Enter to keep current): ");
        String newName = scanner.nextLine().trim();

        if (newName.isEmpty()) {
            newName = existingUser.getUserName();
        }

        boolean updated = userControl.updateUserName(userId, newName);

        if (updated) {
            System.out.println("User updated successfully.");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    private void adminDeleteUserFlow() {
        System.out.print("Enter User ID to delete: ");
        String userId = scanner.nextLine().trim();

        User existingUser = userControl.findUserByUserId(userId);

        if (existingUser == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nUser Record");
        System.out.println(existingUser);

        System.out.print("Confirm delete? (Y/N): ");
        String confirm = askYesNo("");

        if (confirm.equalsIgnoreCase("Y")) {
            boolean deleted = userControl.deleteUser(userId);

            if (deleted) {
                System.out.println("User deleted successfully.");
            } else {
                System.out.println("Failed to delete user.");
            }
        } else {
            System.out.println("Delete cancelled.");
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
            if (!prompt.isEmpty()) {
                System.out.print(prompt);
            }

            answer = scanner.nextLine().trim();

            if (answer.equalsIgnoreCase("Y") || answer.equalsIgnoreCase("N")) {
                return answer;
            }

            System.out.print("Invalid input. Enter Y or N: ");
        }
    }
}