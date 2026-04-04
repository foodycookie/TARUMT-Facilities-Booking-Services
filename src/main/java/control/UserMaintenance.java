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
    private final UserDAO userDAO;

    public static User currentUser;

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

    public boolean isValidUserName(String userName) {
        return userName != null && !userName.trim().isEmpty();
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
                        // ignore invalid old data
                    }
                }
            }
        }

        return "U" + String.format("%06d", max + 1);
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

    public boolean addUser(User user) {
        if (user == null) {
            return false;
        }

        if (!isValidUserName(user.getUserName())) {
            return false;
        }

        boolean added = userList.add(user);

        if (added) {
            saveToFile();
        }

        return added;
    }

    public boolean addNewUser(String userName) {
        if (!isValidUserName(userName)) {
            return false;
        }

        String generatedUserId = generateUserId();
        User newUser = new User(generatedUserId, userName);

        return addUser(newUser);
    }

    public boolean updateUserName(String userId, String newUserName) {
        User existingUser = findUserByUserId(userId);

        if (existingUser == null) {
            return false;
        }

        if (!isValidUserName(newUserName)) {
            return false;
        }

        User updatedUser = new User(existingUser.getUserId(), newUserName);

        boolean removed = userList.remove(existingUser);

        if (!removed) {
            return false;
        }

        boolean added = userList.add(updatedUser);

        if (added) {
            saveToFile();
            return true;
        } else {
            userList.add(existingUser); // rollback
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

    public boolean selectCurrentUser(String userId) {
        User user = findUserByUserId(userId);

        if (user == null) {
            return false;
        }

        currentUser = user;
        return true;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean hasCurrentUser() {
        return currentUser != null;
    }

    public boolean updateCurrentUserName(String newName) {
        if (currentUser == null) {
            return false;
        }

        if (newName == null || newName.trim().isEmpty()) {
            newName = currentUser.getUserName();
        }

        boolean updated = updateUserName(currentUser.getUserId(), newName);

        if (updated) {
            currentUser = findUserByUserId(currentUser.getUserId());
        }

        return updated;
    }

    public String displayAllUsers() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n====================================================\n");
        sb.append(String.format("%-12s %-25s %-10s\n", "User ID", "Name", "Role"));
        sb.append("====================================================\n");

        if (userList.isEmpty()) {
            sb.append(String.format("%-12s %-25s %-10s\n", "-", "No user records found", "-"));
        } else {
            for (int i = 1; i <= userList.getNumberOfEntries(); i++) {
                User user = userList.getEntry(i);

                if (user != null) {
                    sb.append(String.format("%-12s %-25s %-10s\n",
                            user.getUserId(),
                            user.getUserName(),
                            user.getRole()));
                }
            }
        }

        sb.append("====================================================\n");
        return sb.toString();
    }

    public void saveToFile() {
        userDAO.saveToFile(userList);
    }

    public void reloadFromFile() {
        userList = userDAO.retrieveFromFile();
    }
}