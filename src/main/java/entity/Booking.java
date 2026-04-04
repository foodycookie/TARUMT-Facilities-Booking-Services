package entity;

import java.io.Serializable;

/**
 * Booking Entity (POJO)
 * Clean, assignment-compliant, and ready for SortedArrayList.
 */
public class Booking implements Serializable, Comparable<Booking> {

    private String bookingID;     // e.g., "B000001"
    private String userID;        // e.g., "22ABC12345" or "P1234"
    private String facilityID;    // e.g., "101"
    private String facilityName;
    private String roomType;
    private String roomName;
    private String date;          // Format: YYYY-MM-DD
    private String timeSlotId;      // Format: HH:MM-HH:MM

    // Default constructor (required for file deserialization)
    public Booking() {}

    // Full constructor
    public Booking(String bookingID, String userID,
               Facility facility,
               String date,
               Timeslot timeSlot) {

    this.bookingID = bookingID;
    this.userID = userID;
    this.facilityID = facility.getFacilityId();
    this.facilityName = facility.getFacilityName();
    this.roomType = facility.getRoomType();
    this.roomName = facility.getRoomName();
    this.date = date;
    this.timeSlotId = timeSlot.getTimeslotId();
}

    // ====================== Getters & Setters ======================
    public String getBookingID() { return bookingID; }
    public void setBookingID(String bookingID) { this.bookingID = bookingID; }

    public String getUserID() { return userID; }
    public void setUserID(String userID) { this.userID = userID; }

    public String getFacilityID() { return facilityID; }
    public void setFacilityID(String facilityID) { this.facilityID = facilityID; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }

    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTimeSlot() { return timeSlotId; }
    public void setTimeSlot(String timeSlotId) { this.timeSlotId = timeSlotId; }

    // ====================== Critical Methods ======================

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking other = (Booking) obj;
        return this.bookingID.equals(other.bookingID);   // Compare by bookingID
    }

    @Override
    public int hashCode() {
        return bookingID.hashCode();
    }

    /**
     * compareTo() for SortedArrayList - Sorting order: Date → TimeSlot → BookingID
     */
    @Override
    public int compareTo(Booking other) {
        if (other == null) return 1;

        // 1. Primary: Date
        int dateCompare = this.date.compareTo(other.date);
        if (dateCompare != 0) return dateCompare;

        // 2. Secondary: TimeSlot
        int timeCompare = this.timeSlotId.compareTo(other.timeSlotId);
        if (timeCompare != 0) return timeCompare;

        // 3. Tertiary: BookingID
        return this.bookingID.compareTo(other.bookingID);
    }

    // ====================== Display Methods ======================

    @Override
    public String toString() {
        return String.format("B%s | User:%s | %s | %s | %s | %s",
                bookingID, userID, roomName, date, timeSlotId, facilityName);
    }

    /**
     * Nice formatted row for displayAllBookings()
     */
    public String toTableRow() {
        return String.format("%-10s %-12s %-25s %-12s %-15s",
                bookingID,
                userID,
                roomName != null ? roomName : "N/A",
                date,
                timeSlotId);
    }

    /**
     * Short summary for reports
     */
    public String toSummary() {
        return "Booking " + bookingID + " - " + roomName + " on " + date + " (" + timeSlotId + ")";
    }
}