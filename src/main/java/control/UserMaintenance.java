package control;

import adt.SortedArrayList;
import dao.UserDAO;
import entity.User;

/**
 *
 * @author TAY TIAN YOU
 */

public class UserMaintenance {
    private SortedArrayList<User> userList;
    private UserDAO userDAO;

    public UserMaintenance() {
        userDAO = new UserDAO("user.dat");
        userList = userDAO.retrieveFromFile();
    }
    
    public boolean isEmpty() {
        return userList.isEmpty();
    }

    public int getNumberOfUsers() {
        return userList.getNumberOfEntries();
    }

    public boolean isValidStudentId(String id) {
        return id != null && id.matches("\\d{2}[A-Z]{3}\\d{5}");
    }

    public boolean isValidStaffId(String id) {
        return id != null && id.matches("P\\d{4}");
    }

    public boolean isValidUserName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    public boolean isValidRole(String role) {
        return role != null &&
               (role.equalsIgnoreCase("Student") || role.equalsIgnoreCase("Staff"));
    }

    public String generateUserId() {
        int max = 0;

        for (int i = 1; i <= userList.getNumberOfEntries(); i++) {
            User user = userList.getEntry(i);
            if (user != null) {
                String userId = user.getUserId();

                if (userId != null && userId.startsWith("U")) {
                    try {
                        int num = Integer.parseInt(userId.substring(1));
                        if (num > max) {
                            max = num;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid old data
                    }
                }
            }
        }

        return "U" + String.format("%06d", max + 1);
    }
    
    public boolean roleIdExists(String roleId) {
        for (int i = 1; i <= userList.getNumberOfEntries(); i++) {
            User user = userList.getEntry(i);
            if (user != null && user.getRoleId().equalsIgnoreCase(roleId)) {
                return true;
            }
        }
        return false;
    }

    public User findUserByUserId(String userId) {
        for (int i = 1; i <= userList.getNumberOfEntries(); i++) {
            User user = userList.getEntry(i);
            if (user != null && user.getUserId().equalsIgnoreCase(userId)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByRoleId(String roleId) {
        for (int i = 1; i <= userList.getNumberOfEntries(); i++) {
            User user = userList.getEntry(i);
            if (user != null && user.getRoleId().equalsIgnoreCase(roleId)) {
                return user;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }

        if (!isValidUserName(user.getUserName()) || !isValidRole(user.getRole())) {
            return false;
        }

        if (user.getRole().equalsIgnoreCase("Student")) {
            if (!isValidStudentId(user.getRoleId())) {
                return false;
            }
        } else if (user.getRole().equalsIgnoreCase("Staff")) {
            if (!isValidStaffId(user.getRoleId())) {
                return false;
            }
        }

        if (roleIdExists(user.getRoleId())) {
            return false;
        }

        boolean added = userList.add(user);

        if (added) {
            saveToFile();
        }

        return added;
    }

    public boolean updateUser(String userId, String newRoleId, String newUserName, String newRole) {
        User existingUser = findUserByUserId(userId);

        if (existingUser == null) {
            return false;
        }

        if (!isValidUserName(newUserName) || !isValidRole(newRole)) {
            return false;
        }

        if (newRole.equalsIgnoreCase("Student")) {
            if (!isValidStudentId(newRoleId)) {
                return false;
            }
        } else if (newRole.equalsIgnoreCase("Staff")) {
            if (!isValidStaffId(newRoleId)) {
                return false;
            }
        }

        if (!existingUser.getRoleId().equalsIgnoreCase(newRoleId) && roleIdExists(newRoleId)) {
            return false;
        }

        User updatedUser = new User(userId, newRoleId, newUserName, newRole);

        boolean removed = userList.remove(existingUser);
        if (!removed) {
            return false;
        }

        boolean added = userList.add(updatedUser);
        if (added) {
            saveToFile();
            return true;
        } else {
            userList.add(existingUser);
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        User existingUser = findUserByUserId(userId);

        if (existingUser == null) {
            return false;
        }

        boolean deleted = userList.remove(existingUser);

        if (deleted) {
            saveToFile();
        }

        return deleted;
    }

    public void saveToFile() {
        userDAO.saveToFile(userList);
    }

    public void reloadFromFile() {
        userList = userDAO.retrieveFromFile();
    }

    public String displayAllUsers() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n====================================================================\n");
        sb.append(String.format("%-10s %-15s %-25s %-10s\n", "User ID", "Role ID", "Name", "Role"));
        sb.append("====================================================================\n");

        if (userList.isEmpty()) {
            sb.append(String.format("%-10s %-15s %-25s %-10s\n", "-", "-", "No records found", "-"));
        } else {
            for (int i = 1; i <= userList.getNumberOfEntries(); i++) {
                User user = userList.getEntry(i);
                if (user != null) {
                    sb.append(String.format("%-10s %-15s %-25s %-10s\n",
                            user.getUserId(),
                            user.getRoleId(),
                            user.getUserName(),
                            user.getRole()));
                }
            }
        }

        sb.append("====================================================================\n");
        return sb.toString();
    }
}

