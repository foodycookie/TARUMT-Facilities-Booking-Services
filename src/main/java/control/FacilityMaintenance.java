package control;

import adt.SortedArrayList;
import dao.FacilityDAO;
import entity.Facility;

/**
 * Business-logic layer for the Facility module.
 *
 * ID format:
 *   L001, L002, … — Library Discussion Room / Individual Study Room
 *   C001, C002, … — Cyber Centre Discussion Room
 *   S001, S002, … — Sports Facilities
 *
 * Each prefix group maintains its own independent counter so that adding a
 * Cyber room never bumps the Library sequence, and vice-versa.
 *
 * Binary-search usage:
 *   SortedArrayList.add() / remove() / contains() all call binarySearch()
 *   internally via Facility.compareTo() (O(log n)).
 *   The sort key is facilityName → roomType → roomName — facilityId is NOT
 *   part of compareTo, so probe objects with a null ID work correctly.
 *
 *   ID-based lookups (findByFacilityId) require a linear scan because the
 *   list is sorted by name, not by ID — same pattern as UserMaintenance.
 *
 * @author (facility module)
 */
public class FacilityMaintenance {

    // ------------------------------------------------------------------ //
    //  Constants                                                            //
    // ------------------------------------------------------------------ //

    /** Relative path to the binary data file for facilities. */
    private static final String FACILITY_FILE = "data/facilities.dat";

    // Prefix letters, one per facility category group
    /** Prefix for Library Discussion Room and Individual Study Room. */
    public static final String PREFIX_LIBRARY = "L";

    /** Prefix for Cyber Centre Discussion Room. */
    public static final String PREFIX_CYBER   = "C";

    /** Prefix for Sports Facilities. */
    public static final String PREFIX_SPORTS  = "S";

    /** Zero-pad width for the numeric portion of an ID, e.g. 001. */
    private static final int ID_PAD_WIDTH = 3;

    // ------------------------------------------------------------------ //
    //  Fields                                                               //
    // ------------------------------------------------------------------ //

    /**
     * In-memory sorted list of facilities.
     * Backed by SortedArrayList — add/remove/contains use binary search O(log n).
     */
    private SortedArrayList<Facility> facilityList;

    /** DAO responsible for reading/writing the binary data file. */
    private final FacilityDAO facilityDAO;

    // ------------------------------------------------------------------ //
    //  Constructor                                                          //
    // ------------------------------------------------------------------ //

    /**
     * Initialises the control layer and loads persisted facilities from disk.
     */
    public FacilityMaintenance() {
        facilityDAO  = new FacilityDAO(FACILITY_FILE);
        facilityList = facilityDAO.retrieveFromFile();
    }

    // ------------------------------------------------------------------ //
    //  Query helpers                                                        //
    // ------------------------------------------------------------------ //

    /** @return true if there are no facilities in the list */
    public boolean isEmpty() {
        return facilityList.isEmpty();
    }

    /** @return total number of facilities currently stored */
    public int getNumberOfFacilities() {
        return facilityList.getNumberOfEntries();
    }

    // ------------------------------------------------------------------ //
    //  ID generation                                                        //
    // ------------------------------------------------------------------ //

    /**
     * Determines which prefix letter to use for a given facility name.
     *
     *   facilityName contains "Cyber"   → "C"
     *   facilityName contains "Sports"  → "S"
     *   anything else (Library / Study) → "L"
     *
     * @param facilityName the facility category name entered by the user
     * @return the one-letter prefix string
     */
    public String resolvePrefixFor(String facilityName) {
        if (facilityName == null) return PREFIX_LIBRARY;
        String lower = facilityName.toLowerCase();
        if (lower.contains("cyber"))  return PREFIX_CYBER;
        if (lower.contains("sport"))  return PREFIX_SPORTS;
        return PREFIX_LIBRARY;   // Library Discussion Room & Individual Study Room both use L
    }

    /**
     * Auto-generates the next facility ID for the given prefix group.
     *
     * Scans the full list, keeps only entries whose ID starts with the
     * resolved prefix, finds the highest numeric suffix among them, and
     * returns prefix + (max + 1) zero-padded to ID_PAD_WIDTH digits.
     *
     * Examples:
     *   existing: C001, C002  →  next Cyber ID = C003
     *   existing: L001        →  next Library ID = L002
     *   existing: (none)      →  first Sports ID = S001
     *
     * @param facilityName the facility category name (used to resolve prefix)
     * @return the next unique ID string, e.g. "L002", "C001", "S003"
     */
    public String generateFacilityId(String facilityName) {
        String prefix = resolvePrefixFor(facilityName);
        int max = 0;

        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility f = facilityList.getEntry(i);
            if (f != null && f.getFacilityId() != null
                    && f.getFacilityId().startsWith(prefix)) {
                try {
                    int num = Integer.parseInt(f.getFacilityId().substring(prefix.length()));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ignored) {
                    // Skip malformed entries
                }
            }
        }

        return prefix + String.format("%0" + ID_PAD_WIDTH + "d", max + 1);
    }

    /**
     * Validates that a facility ID string matches the expected format:
     * one letter prefix (L / C / S) followed by digits.
     *
     * @param facilityId the ID to validate
     * @return true if the format is correct
     */
    public boolean isValidFacilityId(String facilityId) {
        if (facilityId == null || facilityId.length() < 2) return false;
        String prefix = facilityId.substring(0, 1);
        if (!prefix.equals(PREFIX_LIBRARY)
                && !prefix.equals(PREFIX_CYBER)
                && !prefix.equals(PREFIX_SPORTS)) {
            return false;
        }
        try {
            Integer.parseInt(facilityId.substring(1));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ------------------------------------------------------------------ //
    //  Validation                                                           //
    // ------------------------------------------------------------------ //

    /**
     * A facility name is valid when it is non-null and non-blank.
     *
     * @param facilityName the candidate name
     * @return true if valid
     */
    public boolean isValidFacilityName(String facilityName) {
        return facilityName != null && !facilityName.trim().isEmpty();
    }

    /**
     * A room type is valid when it is non-null and non-blank.
     *
     * @param roomType the candidate type string
     * @return true if valid
     */
    public boolean isValidRoomType(String roomType) {
        return roomType != null && !roomType.trim().isEmpty();
    }

    /**
     * A room name is valid when it is non-null and non-blank.
     *
     * @param roomName the candidate room name / code
     * @return true if valid
     */
    public boolean isValidRoomName(String roomName) {
        return roomName != null && !roomName.trim().isEmpty();
    }

    // ------------------------------------------------------------------ //
    //  Duplicate detection (uses SortedArrayList binary search internally) //
    // ------------------------------------------------------------------ //

    /**
     * Checks whether a facility with exactly the same facilityName, roomType,
     * and roomName already exists.
     *
     * Uses SortedArrayList.contains() → binarySearch() → Facility.compareTo().
     * Since compareTo() ignores facilityId, the probe object can use null.
     * O(log n).
     *
     * @param facilityName the facility category name
     * @param roomType     the room equipment type
     * @param roomName     the specific room identifier
     * @return true if a duplicate exists
     */
    public boolean facilityExists(String facilityName, String roomType, String roomName) {
        // null ID is safe — compareTo does not use facilityId
        Facility probe = new Facility(null, facilityName, roomType, roomName);
        return facilityList.contains(probe);
    }

    /**
     * Checks for a duplicate excluding a specific facility (used during update).
     *
     * @param excludeId    the facilityId of the record being edited
     * @param facilityName new facility name
     * @param roomType     new room type
     * @param roomName     new room name
     * @return true if another record with that combination already exists
     */
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

    // ------------------------------------------------------------------ //
    //  Search operations                                                    //
    // ------------------------------------------------------------------ //

    /**
     * Finds a facility by its exact string ID (e.g. "L001") using a linear
     * scan. Linear scan is necessary because the list is sorted by name, not ID.
     *
     * @param facilityId the string ID to find
     * @return the matching Facility, or null if not found
     */
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

    /**
     * Returns all facilities whose facilityName contains the given keyword
     * (case-insensitive, partial match). Requires a linear scan.
     *
     * @param keyword the search term
     * @return a SortedArrayList of matching facilities (may be empty)
     */
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

    /**
     * Returns all facilities whose roomName contains the given keyword
     * (case-insensitive, partial match). Requires a linear scan.
     *
     * @param keyword the search term (e.g. "B002", "Pickleball")
     * @return a SortedArrayList of matching facilities (may be empty)
     */
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

    // ------------------------------------------------------------------ //
    //  CRUD operations                                                      //
    // ------------------------------------------------------------------ //

    /**
     * Validates then inserts a new facility into the sorted list and persists
     * it to disk.
     *
     * SortedArrayList.add() finds the insertion point via binary search O(log n).
     *
     * @param facility the Facility to add (facilityId must already be set)
     * @return true if added successfully, false on validation or duplicate failure
     */
    public boolean addFacility(Facility facility) {
        if (facility == null)                                   return false;
        if (!isValidFacilityId(facility.getFacilityId()))      return false;
        if (!isValidFacilityName(facility.getFacilityName()))  return false;
        if (!isValidRoomType(facility.getRoomType()))          return false;
        if (!isValidRoomName(facility.getRoomName()))          return false;

        // Reject exact duplicate (binary search via SortedArrayList.contains)
        if (facilityExists(facility.getFacilityName(),
                           facility.getRoomType(),
                           facility.getRoomName())) {
            return false;
        }

        boolean added = facilityList.add(facility);
        if (added) saveToFile();
        return added;
    }

    /**
     * Updates an existing facility identified by its string ID.
     *
     * Strategy (mirrors UserMaintenance.updateUser):
     *   1. Locate the old record by ID (linear scan).
     *   2. Validate new fields.
     *   3. Check no other record has the same name combination.
     *   4. Remove old record (binary search by sort key).
     *   5. Re-insert updated record (binary search for new position).
     *   6. Persist; roll back if re-insert fails.
     *
     * Note: the facilityId is preserved unchanged during an update — only
     * the name fields can change.
     *
     * @param facilityId      the ID of the facility to update, e.g. "L001"
     * @param newFacilityName the new facility category name
     * @param newRoomType     the new room type
     * @param newRoomName     the new room name / identifier
     * @return true if updated successfully, false otherwise
     */
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

        // Remove old — binary search locates it via sort key
        boolean removed = facilityList.remove(existing);
        if (!removed) return false;

        Facility updated = new Facility(facilityId, newFacilityName, newRoomType, newRoomName);

        // Binary-search re-insertion at the new sorted position
        boolean added = facilityList.add(updated);
        if (added) {
            saveToFile();
            return true;
        } else {
            facilityList.add(existing); // roll back
            return false;
        }
    }

    /**
     * Removes the facility with the given string ID and persists the change.
     *
     * @param facilityId the ID of the facility to delete, e.g. "C002"
     * @return true if deleted, false if not found
     */
    public boolean deleteFacility(String facilityId) {
        Facility existing = findByFacilityId(facilityId);
        if (existing == null) return false;

        boolean deleted = facilityList.remove(existing); // binary search inside
        if (deleted) saveToFile();
        return deleted;
    }

    // ------------------------------------------------------------------ //
    //  Persistence pass-through                                             //
    // ------------------------------------------------------------------ //

    /**
     * Writes the current in-memory list to the data file.
     * Called automatically after every mutating operation.
     */
    public void saveToFile() {
        facilityDAO.saveToFile(facilityList);
    }

    /**
     * Discards the in-memory list and reloads it from the data file.
     */
    public void reloadFromFile() {
        facilityList = facilityDAO.retrieveFromFile();
    }

    // ------------------------------------------------------------------ //
    //  Display helpers                                                      //
    // ------------------------------------------------------------------ //

    /**
     * Builds a formatted table of all facilities sorted by
     * facilityName → roomType → roomName.
     *
     * @return a multi-line string ready for console output
     */
    public String displayAllFacilities() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n==========================================================================\n");
        sb.append(String.format("%-12s %-35s %-30s %-15s%n",
                "Facility ID", "Facility Name", "Room Type", "Room Name"));
        sb.append("==========================================================================\n");

        if (facilityList.isEmpty()) {
            sb.append(String.format("%-12s %-35s %-30s %-15s%n",
                    "-", "-", "-", "No facilities found."));
        } else {
            for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
                Facility f = facilityList.getEntry(i);
                if (f != null) {
                    sb.append(String.format("%-12s %-35s %-30s %-15s%n",
                            f.getFacilityId(),
                            f.getFacilityName(),
                            f.getRoomType(),
                            f.getRoomName()));
                }
            }
        }

        sb.append("==========================================================================\n");
        return sb.toString();
    }

    /**
     * Builds a formatted table for a subset of facilities (e.g. search results).
     *
     * @param list the SortedArrayList to display
     * @return a multi-line string ready for console output
     */
    public String displayFacilityList(SortedArrayList<Facility> list) {
        StringBuilder sb = new StringBuilder();

        sb.append("\n==========================================================================\n");
        sb.append(String.format("%-12s %-35s %-30s %-15s%n",
                "Facility ID", "Facility Name", "Room Type", "Room Name"));
        sb.append("==========================================================================\n");

        if (list == null || list.isEmpty()) {
            sb.append(String.format("%-12s %-35s %-30s %-15s%n",
                    "-", "-", "-", "No facilities found."));
        } else {
            for (int i = 1; i <= list.getNumberOfEntries(); i++) {
                Facility f = list.getEntry(i);
                if (f != null) {
                    sb.append(String.format("%-12s %-35s %-30s %-15s%n",
                            f.getFacilityId(),
                            f.getFacilityName(),
                            f.getRoomType(),
                            f.getRoomName()));
                }
            }
        }

        sb.append("==========================================================================\n");
        return sb.toString();
    }
}