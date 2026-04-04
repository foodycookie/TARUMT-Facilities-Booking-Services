package entity;

import java.io.Serializable;

    public class Booking implements Serializable, Comparable<Booking> {

        private int bookingID;
        private int userID;
        private int facilityID;
        private String date;        // Format: YYYY-MM-DD
        private String timeSlot;    // Format: HH:MM-HH:MM

        // Default constructor
        public Booking() {}

        // Full constructor
        public Booking(int bookingID, int userID, int facilityID, String date, String timeSlot) {
            this.bookingID = bookingID;
            this.userID = userID;
            this.facilityID = facilityID;
            this.date = date;
            this.timeSlot = timeSlot;
        }

        // Getters & Setters
        public int getBookingID() { return bookingID; }
        public void setBookingID(int bookingID) { this.bookingID = bookingID; }

        public int getUserID() { return userID; }
        public void setUserID(int userID) { this.userID = userID; }

        public int getFacilityID() { return facilityID; }
        public void setFacilityID(int facilityID) { this.facilityID = facilityID; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getTimeSlot() { return timeSlot; }
        public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

        // hashCode (based on unique ID)
        @Override
        public int hashCode() {
            return Integer.hashCode(bookingID);
        }

        // equals (IMPORTANT: based on bookingID ONLY)
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            Booking other = (Booking) obj;
            return this.bookingID == other.bookingID;
        }

        // compareTo (CRITICAL for SortedArrayList)
        @Override
        public int compareTo(Booking other) {

            // 1. Sort by date
            int dateCompare = this.date.compareTo(other.date);
            if (dateCompare != 0) {
                return dateCompare;
            }

            // 2. Then by time slot
            int timeCompare = this.timeSlot.compareTo(other.timeSlot);
            if (timeCompare != 0) {
                return timeCompare;
            }

            // 3. Finally by bookingID (to avoid duplicates issue)
            return Integer.compare(this.bookingID, other.bookingID);
        }

        // toString (clean for report)
        @Override
        public String toString() {
            return bookingID + "\t" +
                   userID + "\t" +
                   facilityID + "\t" +
                   date + "\t" +
                   timeSlot;
        }
}