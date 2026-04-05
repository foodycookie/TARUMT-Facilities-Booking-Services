package entity;

import java.io.Serializable;

/**
 * Lai Yu Hui
 */

public class Booking implements Serializable, Comparable<Booking> {

    private String bookingID;
    private String userID;
    private String facilityID;
    private String facilityName;
    private String roomType;
    private String roomName;
    private String date;
    private String timeSlotId;

    public Booking() {}

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

    public String getTimeSlotId() { return timeSlotId; }
    public void setTimeSlotId(String timeSlotId) { this.timeSlotId = timeSlotId; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Booking other = (Booking) obj;
        return this.bookingID.equals(other.bookingID);
    }

    @Override
    public int hashCode() {
        return bookingID.hashCode();
    }

    @Override
    public int compareTo(Booking other) {
        if (other == null) return 1;

        int dateCompare = this.date.compareTo(other.date);
        if (dateCompare != 0) return dateCompare;

        int timeCompare = this.timeSlotId.compareTo(other.timeSlotId);
        if (timeCompare != 0) return timeCompare;

        return this.bookingID.compareTo(other.bookingID);
    }

    @Override
    public String toString() {
        return String.format("B%s | User:%s | %s | %s | %s | %s",
                bookingID, userID, roomName, date, timeSlotId, facilityName);
    }

    public String toTableRow() {
        return String.format("%-10s %-12s %-25s %-12s %-15s",
                bookingID,
                userID,
                roomName != null ? roomName : "N/A",
                date,
                timeSlotId);
    }

    public String toSummary() {
        return "Booking " + bookingID + " - " + roomName + " on " + date + " (" + timeSlotId + ")";
    }
}