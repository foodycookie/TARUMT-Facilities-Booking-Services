

package entity;

import java.io.Serializable;

/**
 * @author Tay Tian You
 */

public class User implements Comparable<User>, Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String userName;
    private String role;

    public User() {
        this.role = "User";
    }

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        this.role = "User";
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getRole() {
        return role;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int compareTo(User other) {
        return this.userId.compareToIgnoreCase(other.userId);
    }

    @Override
    public String toString() {
        return String.format("User ID: %s, Name: %s, Role: %s", userId, userName, role);
    }
    
    public boolean isUser() {
        return this.role.equalsIgnoreCase("User");
    }
}