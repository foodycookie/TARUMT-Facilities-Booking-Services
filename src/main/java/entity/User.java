package entity;

import java.io.Serializable;

/**
 *
 * @author TAY TIAN YOU
 */


public class User implements Comparable<User>, Serializable {
    private static final long serialVersionUID = 1L;

    private String userId;
    private String roleId;
    private String userName;
    private String role;

    public User() {
    }

    public User(String userId, String roleId, String userName, String role) {
        this.userId = userId;
        this.roleId = roleId;
        this.userName = userName;
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoleId() {
        return roleId;
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

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof User)) return false;

        User other = (User) obj;
        return this.userId != null && this.userId.equalsIgnoreCase(other.userId);
    }

    @Override
    public String toString() {
        return String.format("User ID: %s, Role ID:  %s, Name: %s, Role: %s", userId, roleId, userName, role);
    }
}

