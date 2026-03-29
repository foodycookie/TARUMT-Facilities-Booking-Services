package entity;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Lenovo
 */
public class Booking implements Serializable, Comparable<Booking>{
    private int bookingID;
    private int userID;
    private int facilityID;
    private String date;
    private String timeSlot;
    
    public Booking (){}

    public Booking(int bookingID, int userID, int facilityID, String date, String timeSlot) {
        this.bookingID = bookingID;
        this.userID = userID;
        this.facilityID = facilityID;
        this.date = date;
        this.timeSlot = timeSlot;
    }

    public int getBookingID() {
        return bookingID;
    }

    public void setBookingID(int bookingID) {
        this.bookingID = bookingID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getFacilityID() {
        return facilityID;
    }

    public void setFacilityID(int facilityID) {
        this.facilityID = facilityID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(bookingID);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Booking other = (Booking) obj;
        if (this.userID != other.userID) {
            return false;
        }
        if (this.facilityID != other.facilityID) {
            return false;
        }
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return Objects.equals(this.timeSlot, other.timeSlot);
    }

    @Override
    public String toString() {
        return "Booking{" + "bookingID=" + bookingID + ", userID=" + userID + ", facilityID=" + facilityID + ", date=" + date + ", timeSlot=" + timeSlot + '}';
    }
    
//    @Override
//    public int compareTo(Booking o) {
//        int result = Integer.compare(this.bookingID, o.bookingID);
//        if (result != 0) {
//            return result;
//        }
//
//        return result;
//    }
    
}
