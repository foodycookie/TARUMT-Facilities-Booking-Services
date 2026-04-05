package entity;

import java.io.Serializable;

/*
 * Tay Tian You
*/

public class Admin implements Comparable<Admin>, Serializable{
    private static final long serialVersionUID = 1L;

    private String adminId;
    private String adminName;
    private String role;
    
    public Admin() {
        this.role = "Admin";
    }
    
    public Admin (String adminId, String adminName) {
        this.adminId = adminId;
        this.adminName = adminName;
        this.role = "Admin";
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public int compareTo(Admin other) {
        return this.adminId.compareToIgnoreCase(other.adminId);
    }

    @Override
    public String toString() {
        return String.format("Admin ID: %s, Name: %s, Role: %s", adminId, adminName, role);
    }
    
    public boolean isAdmin() {
        return this.role.equalsIgnoreCase("Admin");
    }   
}