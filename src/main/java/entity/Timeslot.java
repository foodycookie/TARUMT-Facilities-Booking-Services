package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Ong Hao Howard
 */

public class Timeslot implements Comparable<Timeslot>, Serializable {
    public static final LocalTime DAY_START = LocalTime.of(9, 0);
    public static final LocalTime DAY_END = LocalTime.of(19, 30);
    public static final int MINUTES_PER_BLOCK = 30;
    
    public enum Status {
        AVAILABLE,
        BOOKED,
        BLOCKED
    }

    private String timeslotId;
    private String facilityId;
    private LocalDate date;
    private LocalTime startTime;
    // startTime + 30 minutes
    private LocalTime endTime;
    private Status status;
    // userId if BOOKED, staffId if BLOCKED, null if AVAILABLE
    private String bookedBy;
    // null if AVAILABLE
    private String bookingId;

    public Timeslot(String facilityId, LocalDate date, LocalTime startTime) {
        this.timeslotId = generateTimeslotId(facilityId, date, startTime);
        this.facilityId = facilityId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(MINUTES_PER_BLOCK);
        this.status = Status.AVAILABLE;
        this.bookedBy = null;
        this.bookingId = null;
    }

    public String getTimeslotId() {
        return timeslotId;
    }

    public String getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(String facilityId) { 
        this.facilityId = facilityId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public LocalTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(MINUTES_PER_BLOCK);
        this.timeslotId = generateTimeslotId(this.facilityId, this.date, startTime);
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Status getStatus() {
        return status;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public String getBookingId() {
        return bookingId;
    }
    
    public static String generateTimeslotId(String facilityId, LocalDate date, LocalTime startTime) {
        String datePart = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String timePart = startTime.format(DateTimeFormatter.ofPattern("HHmm"));
        
        return "TS-" + facilityId + "-" + datePart + "-" + timePart;
    }
    
    public boolean book(String userId, String bookingId) {
        if (this.status != Status.AVAILABLE) {
            return false;
        }
        
        this.status = Status.BOOKED;
        this.bookedBy = userId;
        this.bookingId = bookingId;
        
        return true;
    }
    public boolean cancel() {
        if (this.status != Status.BOOKED) {
            return false;
        }
        
        this.status = Status.AVAILABLE;
        this.bookedBy = null;
        this.bookingId = null;
        
        return true;
    }

    public boolean block(String staffId) {
        if (this.status == Status.BLOCKED) {
            return false;
        }
        
        this.status = Status.BLOCKED;
        this.bookedBy = staffId;
        this.bookingId = null;
        
        return true;
    }

    public boolean unblock() {
        if (this.status != Status.BLOCKED) {
            return false;
        }
        
        this.status = Status.AVAILABLE;
        this.bookedBy = null;

        return true;
    }
    
    public boolean isAvailable() {
        return this.status == Status.AVAILABLE;
    }

    public boolean isBooked() {
        return this.status == Status.BOOKED;
    }

    public boolean isBlocked() {
        return this.status == Status.BLOCKED;
    }
    
    public boolean isWithinBookingWindow(LocalDate today) {
        return date.equals(today) || date.equals(today.plusDays(1));
    }
    
    @Override
    public int compareTo(Timeslot other) {
        int result = this.date.compareTo(other.date);
        if (result != 0) {
            return result;
        }

        result = this.startTime.compareTo(other.startTime);
        if (result != 0) {
            return result;
        }

        return this.facilityId.compareTo(other.facilityId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        
        if (!(obj instanceof Timeslot)) {
            return false;
        }
        
        Timeslot other = (Timeslot) obj;
        
        return this.timeslotId.equals(other.timeslotId);
    }
    
    // For debug, table UI in boundary
    @Override
    public String toString() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        return String.format("Timeslot[%s | %s | %s | %s–%s | %s | BookedBy: %s]",
            timeslotId,
            facilityId,
            date.format(dateFormate),
            startTime.format(timeFormat),
            endTime.format(timeFormat),
            status,
            bookedBy != null ? bookedBy : "-"
        );
    }    
}