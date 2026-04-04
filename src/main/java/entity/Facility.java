
package entity;

import java.io.Serializable;

/**
 * Represents a bookable facility at TARUMT.
 *
 * facilityId format:
 *   L001, L002, … — Library Discussion Room / Individual Study Room
 *   C001, C002, … — CyberCentre Discussion Room
 *   S001, S002, … — Sports Facilities
 *
 * The field type is String (not int) so that the alphabetic prefix is
 * preserved in storage and display. The prefix is determined by facilityName
 * at creation time in FacilityMaintenance.generateFacilityId().
 *
 * compareTo() sorts by facilityName → roomType → roomName (case-insensitive),
 * matching the original design. facilityId is intentionally excluded from
 * compareTo so that SortedArrayList.contains() / binary-search probes work
 * correctly using only the three name fields.
 *
 */

public class Facility implements Serializable, Comparable<Facility> {

    private static final long serialVersionUID = 1L;

    /**
     * Unique facility ID.
     * Format: one-letter prefix + 3-digit zero-padded number, e.g. L001, C003, S002.
     *   L = Library Discussion Room or Individual Study Room
     *   C = CyberCentre Discussion Room
     *   S = Sports Facilities
     */
    private String facilityId;

    // facilityName = Cyber Centre Discussion Room / Library Discussion Room /
    //                Individual Study Room / Sports Facilities
    private String facilityName;

    // roomType = Discussion Room (1 PC) / Discussion Room with Projector (2 PCs) /
    //            Pickleball Court / etc.
    private String roomType;

    // roomName = B002 / TA255 / Pickleball Court 1 / etc.
    private String roomName;

    // ------------------------------------------------------------------ //
    //  Constructors                                                         //
    // ------------------------------------------------------------------ //

    public Facility() {}

    /**
     * Full constructor.
     *
     * @param facilityId   string ID, e.g. "L001", "C002", "S003"
     * @param facilityName category name, e.g. "Library Discussion Room"
     * @param roomType     room equipment type, e.g. "Discussion Room (1 PC)"
     * @param roomName     specific room code, e.g. "B002"
     */
    public Facility(String facilityId, String facilityName, String roomType, String roomName) {
        this.facilityId   = facilityId;
        this.facilityName = facilityName;
        this.roomType     = roomType;
        this.roomName     = roomName;
    }

    // ------------------------------------------------------------------ //
    //  Getters & Setters                                                    //
    // ------------------------------------------------------------------ //

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    // ------------------------------------------------------------------ //
    //  Object overrides                                                      //
    // ------------------------------------------------------------------ //

    @Override
    public String toString() {
        return facilityName + " - " + roomType + " (" + roomName + ")";
    }

    /**
     * Sorts by facilityName → roomType → roomName (case-insensitive).
     * facilityId is deliberately excluded so that binary-search probe objects
     * (constructed with a null/empty ID) still match correctly.
     */
    @Override
    public int compareTo(Facility o) {
        int result = this.facilityName.compareToIgnoreCase(o.facilityName);
        if (result != 0) return result;

        result = this.roomType.compareToIgnoreCase(o.roomType);
        if (result != 0) return result;

        return this.roomName.compareToIgnoreCase(o.roomName);
    }
}