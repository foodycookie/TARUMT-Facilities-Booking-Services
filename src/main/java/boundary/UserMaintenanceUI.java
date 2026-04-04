package boundary;

import control.UserMaintenance;
import entity.User;
import java.util.Scanner;
import utility.Validation;

/**
 *
 * @author TAY TIAN YOU
 */
public class UserMaintenanceUI {
    private Scanner scanner = new Scanner(System.in);
    private UserMaintenance userMaintenance = new UserMaintenance(); 
    
    public void start() {
        int choice;

        do {
            System.out.println("\n========== USER MANAGEMENT MENU ==========");
            System.out.println("1. Display All Users");
            System.out.println("2. Add User");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("0. Exit");
            System.out.print("Enter choice: ");
            choice = Validation.readInt();

            switch (choice) {
                case 1 -> System.out.println(userMaintenance.displayAllUsers());
                case 2 -> addUser();
                case 3 -> updateUser();
                case 4 -> deleteUser();
                case 0 -> System.out.println("Exiting User Management Module...");
                default -> System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private void addUser() {
        int choice;

        do {
            System.out.println("\n========== ADD USER ==========");

            String role = "";
            String roleId = "";
            String userName = "";

            int roleChoice;
            do {
                System.out.println("1. Student");
                System.out.println("2. Staff");
                System.out.print("Choose role: ");
                roleChoice = Validation.readInt();

                if (roleChoice != 1 && roleChoice != 2) {
                    System.out.println("Invalid choice. Please enter 1 or 2 only.");
                }
            } while (roleChoice != 1 && roleChoice != 2);
            
            String roleExp;
            
            if (roleChoice == 1) {
                role = "Student";
                roleExp = "(nnWWWnnnnn)";
            } else {
                role = "Staff";
                roleExp = "(P1234)";
            }

            System.out.print("Enter " + role + " ID" + roleExp + ": ");
            roleId = scanner.nextLine().trim();
            
            if (role.equals("Student")) {
                if (!Validation.isValidStudentId(roleId)) {
                    System.out.println("Invalid Student ID format " + roleExp);
                    choice = 2;
                    continue;
                }
            } else {
                if (!Validation.isValidStaffId(roleId)) {
                    System.out.println("Invalid Staff ID format " + roleExp);
                    choice = 2;
                    continue;
                }
            }

            if (userMaintenance.roleIdExists(roleId)) {
                System.out.println("Role ID already exists.");
                choice = 2;
                continue;
            }

            System.out.print("Enter Name: ");
            userName = scanner.nextLine().trim();

            if (!Validation.isValidUserName(userName)) {
                System.out.println("Name cannot be empty.");
                choice = 2;
                continue;
            }

            String generatedUserId = userMaintenance.generateUserId();
            User tempUser = new User(generatedUserId, roleId, userName, role);

            System.out.println("\n---------- USER PROFILE PREVIEW ----------");
            System.out.println(tempUser);
            System.out.println("------------------------------------------");
            System.out.println("1. Confirm and Save");
            System.out.println("2. Re-enter Details");
            System.out.println("3. Cancel");
            System.out.print("Enter choice: ");
            choice = Validation.readInt();

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
                            nextChoice = Validation.readInt();

                            if (nextChoice != 1 && nextChoice != 2) {
                                System.out.println("Invalid choice. Please enter 1 or 2 only.");
                            }
                        } while (nextChoice != 1 && nextChoice != 2);

                        if (nextChoice == 1) {
                            choice = 1;
                        } else {
                            System.out.println("Returning to main menu...");
                            return;
                        }
                    } else {
                        System.out.println("Failed to add user.");
                        choice = 2;
                    }
                }
                case 2 -> System.out.println("Please re-enter user details.");
                case 3 -> System.out.println("Add user cancelled.");
                default -> {
                    System.out.println("Invalid choice.");
                    choice = 2;
                }
            }

        } while (choice == 2 || choice == 1);
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
        int choice = Validation.readInt();

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
        System.out.println("\n========== UPDATE USER ==========");
        System.out.println(userMaintenance.displayAllUsers());
        System.out.print("Enter User ID to update: ");
        String userId = scanner.nextLine().trim();

        User existingUser = userMaintenance.findUserByUserId(userId);

        if (existingUser == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.println("\nCurrent User Information");
        System.out.println(existingUser);

        String newRole = existingUser.getRole();

        System.out.print("\nEnter New Role ID (press Enter to keep current): ");
        String newRoleId = scanner.nextLine().trim();

        if (newRoleId.isEmpty()) {
            newRoleId = existingUser.getRoleId();
        } else {
            if (newRole.equalsIgnoreCase("Student")) {
                if (!Validation.isValidStudentId(newRoleId)) {
                    System.out.println("Invalid Student ID format.");
                    return;
                }
            } else {
                if (!Validation.isValidStaffId(newRoleId)) {
                    System.out.println("Invalid Staff ID format.");
                    return;
                }
            }

            User duplicateUser = userMaintenance.findUserByRoleId(newRoleId);
            if (duplicateUser != null && !duplicateUser.getUserId().equalsIgnoreCase(userId)) {
                System.out.println("Role ID already exists.");
                return;
            }
        }

        System.out.print("Enter New Name (press Enter to keep current): ");
        String newUserName = scanner.nextLine().trim();

        if (newUserName.isEmpty()) {
            newUserName = existingUser.getUserName();
        }

        System.out.println("\n---------- UPDATED USER PREVIEW ----------");
        System.out.println("User ID  : " + userId);
        System.out.println("Role ID  : " + newRoleId);
        System.out.println("Name     : " + newUserName);
        System.out.println("Role     : " + newRole);
        System.out.println("------------------------------------------");
        System.out.println("1. Confirm Update");
        System.out.println("2. Cancel");
        System.out.print("Enter choice: ");
        int choice = Validation.readInt();

        if (choice == 1) {
            boolean updated = userMaintenance.updateUser(userId, newRoleId, newUserName, newRole);

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
    