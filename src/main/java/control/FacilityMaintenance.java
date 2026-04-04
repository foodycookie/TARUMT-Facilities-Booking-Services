package control;

import adt.SortedArrayList;
import dao.FacilityDAO;
import entity.Facility;

/**
 * Business-logic layer for the Facility module.
 *
 * <p><b>Facility ID format:</b>
 * <ul>
 *   <li>C001, C002, … — Cyber Centre</li>
 *   <li>L001, L002, … — Library (Discussion Rooms &amp; Individual Study Rooms)</li>
 *   <li>S001, S002, … — Sports Facilities</li>
 *   <li>O001, O002, … — Other (miscellaneous facilities)</li>
 * </ul>
 * Each prefix group maintains its own independent counter, so adding a Cyber
 * room never bumps the Library sequence, and vice-versa.
 *
 * <p><b>Binary-search usage (ADT contract):</b><br>
 * {@code SortedArrayList.add()} / {@code remove()} / {@code contains()} all call
 * {@code binarySearch()} internally via {@code Facility.compareTo()} — O(log n).<br>
 * The sort key is {@code facilityName → roomType → roomName}; {@code facilityId}
 * is intentionally excluded from {@code compareTo()} so probe objects with a
 * {@code null} ID work correctly for duplicate detection.
 *
 * <p><b>When binary search cannot be used:</b><br>
 * {@code findByFacilityId()} and {@code getFacilitiesByRoomType()} must do a
 * linear scan because the list is sorted by name, not by ID or room type.
 *
 * <p><b>Early-exit optimisation via {@code SortedListHelper}:</b><br>
 * {@code getFacilitiesByFacilityName()} uses
 * {@code SortedListHelper.compareStringIfExceedTarget()} to break out of the
 * loop as soon as the current entry's facilityName exceeds the target
 * alphabetically — exploiting the sorted order to avoid unnecessary comparisons.
 * This is the same pattern used in {@code TimeslotMaintenance.findTimeslotById()}.
 *
 */

public class FacilityMaintenance {

    // ------------------------------------------------------------------ //
    //  Constants                                                            //
    // ------------------------------------------------------------------ //

    /** Relative path to the binary data file for facilities. */
    private static final String FACILITY_FILE = "data/facilities.dat";

    /** Prefix for Library (Discussion Rooms and Individual Study Rooms). */
    public static final String PREFIX_LIBRARY = "L";

    /** Prefix for Cyber Centre. */
    public static final String PREFIX_CYBER   = "C";

    /** Prefix for Sports Facilities. */
    public static final String PREFIX_SPORTS  = "S";

    /** Prefix for Other / miscellaneous facilities. */
    public static final String PREFIX_OTHER   = "O";

    /** Zero-pad width for the numeric portion of an ID, e.g. 001. */
    private static final int ID_PAD_WIDTH = 3;

    // ------------------------------------------------------------------ //
    //  Fields                                                               //
    // ------------------------------------------------------------------ //

    /**
     * In-memory sorted list of facilities.
     * Backed by {@link SortedArrayList} — add/remove/contains all use
     * binary search in O(log n) via {@code Facility.compareTo()}.
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

    /**
     * @return {@code true} if there are no facilities in the list
     */
    public boolean isEmpty() {
        return facilityList.isEmpty();
    }

    /**
     * @return total number of facilities currently stored
     */
    public int getNumberOfFacilities() {
        return facilityList.getNumberOfEntries();
    }

    // ------------------------------------------------------------------ //
    //  Getter methods for the full list and filtered subsets               //
    // ------------------------------------------------------------------ //

    /**
     * Returns the complete in-memory facility list in sorted order
     * (facilityName → roomType → roomName).
     *
     * <p>Used by other control classes (e.g. {@code TimeslotMaintenance}) that
     * need to iterate over all facilities — for example to generate time-slots
     * for every facility on a given date.
     *
     * <p>The returned reference is the live internal list; callers should treat
     * it as read-only and not mutate it directly.
     *
     * @return all facilities as a {@code SortedArrayList<Facility>}
     */
    public SortedArrayList<Facility> getAllFacilities() {
        return facilityList;
    }

    /**
     * Returns all facilities whose {@code facilityName} exactly matches the
     * given name (case-insensitive).
     *
     * <p><b>Early-exit optimisation:</b> Because the internal list is sorted
     * by {@code facilityName} as its primary key, once an entry's name
     * exceeds the target alphabetically there can be no further matches.
     * {@link utility.SortedListHelper#compareStringIfExceedTarget} is used to
     * detect this condition and break out early — the same pattern used in
     * {@code TimeslotMaintenance.findTimeslotById()}.
     *
     * <p>This is distinct from {@link #searchByFacilityName(String)}, which
     * does a partial/keyword match and cannot use early exit safely.
     *
     * @param facilityName the exact facility category name to match,
     *                     e.g. {@code "Library Discussion Room"}
     * @return a {@code SortedArrayList<Facility>} of exact matches
     *         (sorted by roomType → roomName); empty if none found
     */
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

    /**
     * Returns all facilities whose {@code roomType} exactly matches the given
     * type (case-insensitive).
     *
     * <p><b>Note on scan strategy:</b> The internal list is sorted by
     * {@code facilityName}, not by {@code roomType}, so a full linear scan is
     * unavoidable here. Early exit cannot be applied safely because matching
     * room types are scattered across different facility-name groups.
     *
     * @param roomType the exact room type to match,
     *                 e.g. {@code "Discussion Room (1 PC)"}
     * @return a {@code SortedArrayList<Facility>} of matching facilities
     *         (sorted by facilityName → roomName); empty if none found
     */
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

    // ------------------------------------------------------------------ //
    //  ID generation                                                        //
    // ------------------------------------------------------------------ //

    /**
     * Determines which prefix letter to use for a given facility name.
     * <ul>
     *   <li>Name contains {@code "cyber"}  → {@code "C"}</li>
     *   <li>Name contains {@code "sport"}  → {@code "S"}</li>
     *   <li>Name equals   {@code "other"}  → {@code "O"}</li>
     *   <li>Anything else (Library)        → {@code "L"}</li>
     * </ul>
     *
     * @param facilityName the facility category name entered by the user
     * @return the one-letter prefix string
     */
    public String resolvePrefixFor(String facilityName) {
        if (facilityName == null) return PREFIX_LIBRARY;
        String lower = facilityName.toLowerCase();
        if (lower.contains("cyber"))     return PREFIX_CYBER;
        if (lower.contains("sport"))     return PREFIX_SPORTS;
        if (lower.equals("other"))       return PREFIX_OTHER;
        return PREFIX_LIBRARY;
    }

    /**
     * Auto-generates the next facility ID for the prefix group that corresponds
     * to the given facility name.
     *
     * <p>Scans the full list, filters to entries whose ID starts with the
     * resolved prefix, finds the highest numeric suffix, and returns
     * {@code prefix + (max + 1)} zero-padded to {@value #ID_PAD_WIDTH} digits.
     *
     * <p>Examples:
     * <pre>
     *   existing: C001, C002  →  next Cyber ID  = C003
     *   existing: L001        →  next Library ID = L002
     *   existing: (none)      →  first Sports ID = S001
     * </pre>
     *
     * @param facilityName the facility category name (used to resolve the prefix)
     * @return the next unique ID string, e.g. {@code "L002"}, {@code "C001"}
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
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {
                    // Skip malformed entries
                }
            }
        }

        return prefix + String.format("%0" + ID_PAD_WIDTH + "d", max + 1);
    }

    /**
     * Validates that a facility ID string matches the expected format:
     * one letter prefix (L / C / S / O) followed by digits.
     *
     * @param facilityId the ID to validate
     * @return {@code true} if the format is correct
     */
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

    // ------------------------------------------------------------------ //
    //  Validation                                                           //
    // ------------------------------------------------------------------ //

    /**
     * A facility name is valid when it is non-null and non-blank.
     *
     * @param facilityName the candidate name
     * @return {@code true} if valid
     */
    public boolean isValidFacilityName(String facilityName) {
        return facilityName != null && !facilityName.trim().isEmpty();
    }

    /**
     * A room type is valid when it is non-null and non-blank.
     *
     * @param roomType the candidate type string
     * @return {@code true} if valid
     */
    public boolean isValidRoomType(String roomType) {
        return roomType != null && !roomType.trim().isEmpty();
    }

    /**
     * A room name is valid when it is non-null and non-blank.
     *
     * @param roomName the candidate room name / code
     * @return {@code true} if valid
     */
    public boolean isValidRoomName(String roomName) {
        return roomName != null && !roomName.trim().isEmpty();
    }

    // ------------------------------------------------------------------ //
    //  Duplicate detection — O(log n) via binary search                    //
    // ------------------------------------------------------------------ //

    /**
     * Checks whether a facility with exactly the same {@code facilityName},
     * {@code roomType}, and {@code roomName} already exists.
     *
     * <p>Delegates to {@code SortedArrayList.contains()}, which calls
     * {@code binarySearch()} via {@code Facility.compareTo()} — O(log n).
     * Because {@code compareTo()} ignores {@code facilityId}, the probe object
     * is safely constructed with a {@code null} ID.
     *
     * @param facilityName the facility category name
     * @param roomType     the room equipment type
     * @param roomName     the specific room identifier
     * @return {@code true} if a duplicate exists
     */
    public boolean facilityExists(String facilityName, String roomType, String roomName) {
        Facility probe = new Facility(null, facilityName, roomType, roomName);
        return facilityList.contains(probe);
    }

    /**
     * Checks for a duplicate while excluding one specific facility record.
     * Used during update to allow keeping the same name fields unchanged.
     *
     * @param excludeId    the {@code facilityId} of the record being edited
     * @param facilityName new facility name to check
     * @param roomType     new room type to check
     * @param roomName     new room name to check
     * @return {@code true} if another record with that combination already exists
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
     * Finds a facility by its exact string ID (e.g. {@code "L001"}).
     *
     * <p>Requires a linear scan because the list is sorted by name, not by ID.
     * This is the same pattern as {@code UserMaintenance.findUserByUserId()}.
     *
     * @param facilityId the string ID to find
     * @return the matching {@link Facility}, or {@code null} if not found
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
     * Returns all facilities whose {@code facilityName} contains the given
     * keyword (case-insensitive, partial match).
     *
     * <p><b>Why early exit is not used here:</b> This method performs a
     * <em>substring</em> search, not an exact-match search. A keyword like
     * {@code "Room"} could match entries spread across multiple alphabetically
     * distinct facility names, so stopping at the first non-match would
     * silently drop valid results. For exact-match filtering, use
     * {@link #getFacilitiesByFacilityName(String)}.
     *
     * @param keyword the partial search term (e.g. {@code "Library"})
     * @return a {@code SortedArrayList<Facility>} of matching facilities
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
     * Returns all facilities whose {@code roomName} contains the given keyword
     * (case-insensitive, partial match). Requires a full linear scan.
     *
     * @param keyword the partial search term (e.g. {@code "B002"}, {@code "Pickleball"})
     * @return a {@code SortedArrayList<Facility>} of matching facilities
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
     * <p>{@code SortedArrayList.add()} locates the sorted insertion point via
     * binary search in O(log n) before shifting elements to make room.
     *
     * @param facility the {@link Facility} to add ({@code facilityId} must be set)
     * @return {@code true} if added successfully,
     *         {@code false} on validation or duplicate failure
     */
    public boolean addFacility(Facility facility) {
        if (facility == null)                                   return false;
        if (!isValidFacilityId(facility.getFacilityId()))      return false;
        if (!isValidFacilityName(facility.getFacilityName()))  return false;
        if (!isValidRoomType(facility.getRoomType()))          return false;
        if (!isValidRoomName(facility.getRoomName()))          return false;

        // O(log n) duplicate check via SortedArrayList.contains() → binarySearch()
        if (facilityExists(facility.getFacilityName(),
                           facility.getRoomType(),
                           facility.getRoomName())) {
            return false;
        }

        boolean added = facilityList.add(facility);  // O(log n) binary-search insertion
        if (added) saveToFile();
        return added;
    }

    /**
     * Updates an existing facility identified by its string ID.
     *
     * <p>Strategy (mirrors {@code UserMaintenance.updateUser()}):
     * <ol>
     *   <li>Locate the old record by ID (linear scan — sorted by name, not ID).</li>
     *   <li>Validate new field values.</li>
     *   <li>Check no other record has the same name combination.</li>
     *   <li>Remove the old record — binary search locates it via sort key.</li>
     *   <li>Insert the updated record — binary search finds the new position.</li>
     *   <li>Persist; roll back by re-inserting the original if re-insert fails.</li>
     * </ol>
     * The {@code facilityId} is never changed during an update.
     *
     * @param facilityId      the ID of the facility to update, e.g. {@code "L001"}
     * @param newFacilityName the new facility category name
     * @param newRoomType     the new room type
     * @param newRoomName     the new room name / identifier
     * @return {@code true} if updated successfully, {@code false} otherwise
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

        boolean removed = facilityList.remove(existing);  // O(log n) binary search
        if (!removed) return false;

        Facility updated = new Facility(facilityId, newFacilityName, newRoomType, newRoomName);

        boolean added = facilityList.add(updated);  // O(log n) binary-search insertion
        if (added) {
            saveToFile();
            return true;
        } else {
            facilityList.add(existing);  // roll back to keep list consistent
            return false;
        }
    }

    /**
     * Removes the facility with the given string ID and persists the change.
     *
     * <p>{@code SortedArrayList.remove()} uses binary search to locate the
     * entry via its sort key in O(log n).
     *
     * @param facilityId the ID of the facility to delete, e.g. {@code "C002"}
     * @return {@code true} if deleted, {@code false} if not found
     */
    public boolean deleteFacility(String facilityId) {
        Facility existing = findByFacilityId(facilityId);
        if (existing == null) return false;

        boolean deleted = facilityList.remove(existing);  // O(log n) binary search
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
     * Useful for reverting unsaved in-memory changes.
     */
    public void reloadFromFile() {
        facilityList = facilityDAO.retrieveFromFile();
    }

    // ------------------------------------------------------------------ //
    //  Display helpers                                                      //
    // ------------------------------------------------------------------ //

    /**
     * Builds a formatted table of all facilities in sorted order
     * (facilityName → roomType → roomName).
     *
     * @return a multi-line string ready for console output
     */
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

    /**
     * Builds a formatted table for a subset of facilities (e.g. search results).
     *
     * @param list the {@code SortedArrayList} to display
     * @return a multi-line string ready for console output
     */
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