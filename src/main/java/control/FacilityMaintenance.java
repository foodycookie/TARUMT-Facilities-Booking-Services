package control;

import adt.SortedArrayList;
import dao.FacilityDAO;
import entity.Facility;

/*
 * Tiw Hong Xuan
*/

public class FacilityMaintenance {
    private static final String FACILITY_FILE = "data/facilities.dat";
    public static final String PREFIX_LIBRARY = "L";
    public static final String PREFIX_CYBER   = "C";
    public static final String PREFIX_SPORTS  = "S";
    public static final String PREFIX_OTHER   = "O";
    private static final int ID_PAD_WIDTH = 3;

    private SortedArrayList<Facility> facilityList;
    private final FacilityDAO facilityDAO;

    public FacilityMaintenance() {
        facilityDAO  = new FacilityDAO(FACILITY_FILE);
        facilityList = facilityDAO.retrieveFromFile();
    }

    public boolean isEmpty() {
        return facilityList.isEmpty();
    }

    public int getNumberOfFacilities() {
        return facilityList.getNumberOfEntries();
    }

    public SortedArrayList<Facility> getAllFacilities() {
        return facilityList;
    }

    public SortedArrayList<Facility> getFacilitiesByFacilityName(String facilityName) {
        SortedArrayList<Facility> results = new SortedArrayList<>();
        if (facilityName == null) return results;

        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility f = facilityList.getEntry(i);
            if (f == null) continue;
            if (f.getFacilityName().equalsIgnoreCase(facilityName)) {
                results.add(f);
            }
        }

        return results;
    }

    public SortedArrayList<Facility> getFacilitiesByRoomType(String roomType) {
        SortedArrayList<Facility> results = new SortedArrayList<>();
        if (roomType == null) return results;

        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility f = facilityList.getEntry(i);
            if (f != null && f.getRoomType().equalsIgnoreCase(roomType)) {
                results.add(f);
            }
        }

        return results;
    }

    public String resolvePrefixFor(String facilityName) {
        if (facilityName == null) return PREFIX_LIBRARY;
        String lower = facilityName.toLowerCase();
        if (lower.contains("cyber"))     return PREFIX_CYBER;
        if (lower.contains("sport"))     return PREFIX_SPORTS;
        if (lower.equals("other"))       return PREFIX_OTHER;
        return PREFIX_LIBRARY;
    }

    public String generateFacilityId(String facilityName) {
        String prefix = resolvePrefixFor(facilityName);
        int max = 0;

        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility f = facilityList.getEntry(i);
            if (f != null && f.getFacilityId() != null
                    && f.getFacilityId().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(f.getFacilityId().substring(prefix.length()));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                }
            }
        }

        return prefix + String.format("%0" + ID_PAD_WIDTH + "d", max + 1);
    }

    public boolean isValidFacilityId(String facilityId) {
        if (facilityId == null || facilityId.length() < 2) return false;
        String prefix = facilityId.substring(0, 1);
        if (!prefix.equals(PREFIX_LIBRARY)
                && !prefix.equals(PREFIX_CYBER)
                && !prefix.equals(PREFIX_SPORTS)
                && !prefix.equals(PREFIX_OTHER)) {
            return false;
        }
        try {
            Integer.parseInt(facilityId.substring(1));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValidFacilityName(String facilityName) {
        return facilityName != null && !facilityName.trim().isEmpty();
    }

    public boolean isValidRoomType(String roomType) {
        return roomType != null && !roomType.trim().isEmpty();
    }
    
    public boolean isValidRoomName(String roomName) {
        return roomName != null && !roomName.trim().isEmpty();
    }

    public boolean facilityExists(String facilityName, String roomType, String roomName) {
        Facility probe = new Facility(null, facilityName, roomType, roomName);
        return facilityList.contains(probe);
    }

    public boolean facilityExistsExcluding(String excludeId,
                                           String facilityName,
                                           String roomType,
                                           String roomName) {
        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility f = facilityList.getEntry(i);
            if (f != null && !f.getFacilityId().equalsIgnoreCase(excludeId)) {
                if (f.getFacilityName().equalsIgnoreCase(facilityName)
                        && f.getRoomType().equalsIgnoreCase(roomType)
                        && f.getRoomName().equalsIgnoreCase(roomName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Facility findByFacilityId(String facilityId) {
        if (facilityId == null) return null;
        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility f = facilityList.getEntry(i);
            if (f != null && f.getFacilityId().equalsIgnoreCase(facilityId)) {
                return f;
            }
        }
        return null;
    }

    public SortedArrayList<Facility> searchByFacilityName(String keyword) {
        SortedArrayList<Facility> results = new SortedArrayList<>();
        String lower = keyword.toLowerCase();

        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility f = facilityList.getEntry(i);
            if (f != null && f.getFacilityName().toLowerCase().contains(lower)) {
                results.add(f);
            }
        }
        return results;
    }

    public SortedArrayList<Facility> searchByRoomName(String keyword) {
        SortedArrayList<Facility> results = new SortedArrayList<>();
        String lower = keyword.toLowerCase();

        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility f = facilityList.getEntry(i);
            if (f != null && f.getRoomName().toLowerCase().contains(lower)) {
                results.add(f);
            }
        }
        return results;
    }

    public boolean addFacility(Facility facility) {
        if (facility == null)                                   return false;
        if (!isValidFacilityId(facility.getFacilityId()))      return false;
        if (!isValidFacilityName(facility.getFacilityName()))  return false;
        if (!isValidRoomType(facility.getRoomType()))          return false;
        if (!isValidRoomName(facility.getRoomName()))          return false;

        if (facilityExists(facility.getFacilityName(),
                           facility.getRoomType(),
                           facility.getRoomName())) {
            return false;
        }

        boolean added = facilityList.add(facility);
        if (added) saveToFile();
        return added;
    }

    public boolean updateFacility(String facilityId,
                                  String newFacilityName,
                                  String newRoomType,
                                  String newRoomName) {
        Facility existing = findByFacilityId(facilityId);
        if (existing == null)                       return false;
        if (!isValidFacilityName(newFacilityName)) return false;
        if (!isValidRoomType(newRoomType))          return false;
        if (!isValidRoomName(newRoomName))          return false;

        if (facilityExistsExcluding(facilityId, newFacilityName, newRoomType, newRoomName)) {
            return false;
        }

        boolean removed = facilityList.remove(existing);
        if (!removed) return false;

        Facility updated = new Facility(facilityId, newFacilityName, newRoomType, newRoomName);

        boolean added = facilityList.add(updated);
        if (added) {
            saveToFile();
            return true;
        } else {
            facilityList.add(existing);
            return false;
        }
    }

    public boolean deleteFacility(String facilityId) {
        Facility existing = findByFacilityId(facilityId);
        if (existing == null) return false;

        boolean deleted = facilityList.remove(existing);
        if (deleted) saveToFile();
        return deleted;
    }

    public void saveToFile() {
        facilityDAO.saveToFile(facilityList);
    }

    public void reloadFromFile() {
        facilityList = facilityDAO.retrieveFromFile();
    }

    public String displayAllFacilities() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n==========================================================================================\n");
        sb.append(String.format("%-12s %-20s %-46s %-15s%n",
                "Facility ID", "Facility Name", "Room Type", "Room Name"));
        sb.append("==========================================================================================\n");

        if (facilityList.isEmpty()) {
            sb.append(String.format("%-12s %-20s %-46s %-15s%n",
                    "-", "-", "-", "No facilities found."));
        } else {
            for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
                Facility f = facilityList.getEntry(i);
                if (f != null) {
                    sb.append(String.format("%-12s %-20s %-46s %-15s%n",
                            f.getFacilityId(),
                            f.getFacilityName(),
                            f.getRoomType(),
                            f.getRoomName()));
                }
            }
        }

        sb.append("==========================================================================================\n");
        return sb.toString();
    }

    public String displayFacilityList(SortedArrayList<Facility> list) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n==========================================================================================\n");
        sb.append(String.format("%-12s %-20s %-46s %-15s%n",
                "Facility ID", "Facility Name", "Room Type", "Room Name"));
        sb.append("==========================================================================================\n");

        if (list == null || list.isEmpty()) {
            sb.append(String.format("%-12s %-20s %-46s %-15s%n",
                    "-", "-", "-", "No facilities found."));
        } else {
            for (int i = 1; i <= list.getNumberOfEntries(); i++) {
                Facility f = list.getEntry(i);
                if (f != null) {
                    sb.append(String.format("%-12s %-20s %-46s %-15s%n",
                            f.getFacilityId(),
                            f.getFacilityName(),
                            f.getRoomType(),
                            f.getRoomName()));
                }
            }
        }

        sb.append("==========================================================================================\n");
        return sb.toString();
    }
}