package control;

import adt.*;
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

    public boolean isValidStudentId(String studentId) {
        return studentId != null && studentId.matches("\\d{2}[A-Z]{3}\\d{5}");
    }

    public boolean isValidStudentName(String studentName) {
        return studentName != null && !studentName.trim().isEmpty();
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

    public boolean studentIdExists(String studentId) {
        for (int i = 1; i <= userList.getNumberOfEntries(); i++){
            User user = userList.getEntry(i);
            if (user != null && user.getStudentId().equalsIgnoreCase(studentId)){
                return true;
            }
        }
        return false;
    }

    public User findUserByUserId(String userId) {
        for (int i = 1; i <= userList.getNumberOfEntries(); i++){
            User user = userList.getEntry(i);
            if (user != null && user.getUserId().equalsIgnoreCase(userId)){
                return user;
            }
        }
        return null;
    }

    public User findUserByStudentId(String studentId) {
        for (int i = 1; i <= userList.getNumberOfEntries(); i++){
            User user = userList.getEntry(i);
            if (user != null && user.getStudentId().equalsIgnoreCase(studentId)){
                return user;
            }
        }
        return null;
    }

    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }

        if (!isValidStudentId(user.getStudentId())) {
            return false;
        }

        if (!isValidStudentName(user.getStudentName())) {
            return false;
        }

        if (studentIdExists(user.getStudentId())) {
            return false;
        }

        boolean added = userList.add(user);

        if (added) {
            saveToFile();
        }

        return added;
    }

    public boolean updateUser(String userId, String newStudentId, String newStudentName) {
        User existingUser = findUserByUserId(userId);

        if (existingUser == null) {
            return false;
        }

        if (!isValidStudentId(newStudentId) || !isValidStudentName(newStudentName)) {
            return false;
        }

        if (!existingUser.getStudentId().equalsIgnoreCase(newStudentId) && studentIdExists(newStudentId)) {
            return false;
        }

        User updatedUser = new User(userId, newStudentId, newStudentName);

        boolean removed = userList.remove(existingUser);
        if (!removed) {
            return false;
        }

        boolean added = userList.add(updatedUser);
        if (added) {
            saveToFile();
            return true;
        } else {
            // rollback just in case
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

        sb.append("\n==============================================================\n");
        sb.append(String.format("%-10s %-15s %-25s\n", "User ID", "Student ID", "Student Name"));
        sb.append("==============================================================\n");

        if (userList.isEmpty()) {
            sb.append(String.format("%-10s %-15s %-25s\n", "-", "-", "No User records found"));
        } else {
            for (int i = 1; i <= userList.getNumberOfEntries(); i++){
                User user = userList.getEntry(i);
                if (user != null){
                    sb.append(String.format("%-10s %-15s %-25s\n", user.getUserId(), user.getStudentId(), user.getStudentName()));
                }
            }
        }

        sb.append("==============================================================\n");
        return sb.toString();
    }
}

