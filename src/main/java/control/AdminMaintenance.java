package control;

import adt.SortedArrayList;
import dao.AdminDAO;
import entity.Admin;

/**
 *
 * @author TAY TIAN YOU
 */

public class AdminMaintenance {
    private SortedArrayList<Admin> adminList;
    private final AdminDAO adminDAO;

    public AdminMaintenance() {
        adminDAO = new AdminDAO("admin.dat");
        adminList = adminDAO.retrieveFromFile();
    }

    public boolean isEmpty() {
        return adminList.isEmpty();
    }

    public int getNumberOfAdmins() {
        return adminList.getNumberOfEntries();
    }

    public boolean isValidAdminName(String adminName) {
        return adminName != null && !adminName.trim().isEmpty();
    }

    public String generateAdminId() {
        int max = 0;

        for (int i = 1; i <= adminList.getNumberOfEntries(); i++) {
            Admin admin = adminList.getEntry(i);
            if (admin != null) {
                String id = admin.getAdminId();
                if (id != null && id.startsWith("A")) {
                    try {
                        int num = Integer.parseInt(id.substring(1));
                        if (num > max) {
                            max = num;
                        }
                    } catch (NumberFormatException e) {
                        // ignore invalid old data
                    }
                }
            }
        }

        return "A" + String.format("%04d", max + 1);
    }

    public Admin findAdminByAdminId(String adminId) {
        for (int i = 1; i <= adminList.getNumberOfEntries(); i++) {
            Admin admin = adminList.getEntry(i);
            if (admin != null && admin.getAdminId().equalsIgnoreCase(adminId)) {
                return admin;
            }
        }
        return null;
    }

    public boolean addAdmin(Admin admin) {
        if (admin == null || !isValidAdminName(admin.getAdminName())) {
            return false;
        }

        boolean added = adminList.add(admin);
        if (added) {
            saveToFile();
        }
        return added;
    }

    public boolean updateAdminName(String adminId, String newAdminName) {
        Admin existingAdmin = findAdminByAdminId(adminId);

        if (existingAdmin == null || !isValidAdminName(newAdminName)) {
            return false;
        }

        Admin updatedAdmin = new Admin(existingAdmin.getAdminId(), newAdminName);

        boolean removed = adminList.remove(existingAdmin);
        if (!removed) {
            return false;
        }

        boolean added = adminList.add(updatedAdmin);
        if (added) {
            saveToFile();
            return true;
        } else {
            adminList.add(existingAdmin);
            return false;
        }
    }

    public String displayAllAdmins() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n====================================================\n");
        sb.append(String.format("%-12s %-25s %-10s\n", "Admin ID", "Name", "Role"));
        sb.append("====================================================\n");

        if (adminList.isEmpty()) {
            sb.append(String.format("%-12s %-25s %-10s\n", "-", "No admin records found", "-"));
        } else {
            for (int i = 1; i <= adminList.getNumberOfEntries(); i++) {
                Admin admin = adminList.getEntry(i);
                if (admin != null) {
                    sb.append(String.format("%-12s %-25s %-10s\n",
                            admin.getAdminId(),
                            admin.getAdminName(),
                            admin.getRole()));
                }
            }
        }

        sb.append("====================================================\n");
        return sb.toString();
    }

    public void saveToFile() {
        adminDAO.saveToFile(adminList);
    }

    public void reloadFromFile() {
        adminList = adminDAO.retrieveFromFile();
    }
}