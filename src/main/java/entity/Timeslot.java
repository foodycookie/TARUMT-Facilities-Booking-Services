package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Ong Hao Howard
 */

public class Timeslot implements Comparable<Timeslot>, Serializable {
    public static final LocalTime DAY_START = LocalTime.of(8, 0);
    public static final LocalTime DAY_END = LocalTime.of(20, 0);
    public static final int MINUTES_PER_BLOCK = 120;
    
    public enum Status {
        AVAILABLE,
        BOOKED,
        BLOCKED
    }

    private String timeslotId;
    private Facility facility;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Status status;
    private String bookingId;
    private String bookedById;
    private String bookedByName;
    private String blockedById;
    private String blockedByName;

    public Timeslot(Facility facility, LocalDate date, LocalTime startTime) {
        this.timeslotId = generateTimeslotId(facility.getFacilityId(), date, startTime);
        this.facility = facility;
        this.date = date;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(MINUTES_PER_BLOCK);
        this.status = Status.AVAILABLE;
        this.bookingId = null;
        this.bookedById = null;
        this.bookedByName = null;
        this.blockedById = null;
        this.blockedByName = null;
    }

    public String getTimeslotId() {
        return timeslotId;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacilityId(Facility facility) { 
        this.facility = facility;
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
        this.timeslotId = generateTimeslotId(this.facility.getFacilityId(), this.date, startTime);
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public Status getStatus() {
        return status;
    }
    
    public String getBookingId() {
        return bookingId;
    }

    public String getBookedById() {
        return bookedById;
    }
    
     public String getBookedByName() {
        return bookedByName;
    }
     
    public String getBlockedById() {
        return blockedById;
    }
    
    public String getBlockedByName() {
        return blockedByName;
    }
    
    public static String generateTimeslotId(String facilityId, LocalDate date, LocalTime startTime) {
        String datePart = date.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        String timePart = startTime.format(DateTimeFormatter.ofPattern("HHmm"));
        
        return "TS-" + facilityId + "-" + datePart + "-" + timePart;
    }
    
    public boolean book(String bookingId, String userId, String userName) {
        if (this.status != Status.AVAILABLE) {
            return false;
        }
        
        this.status = Status.BOOKED;
        this.bookingId = bookingId;
        this.bookedById = userId;
        this.bookedByName = userName;
        
        
        return true;
    }
    public boolean cancel() {
        if (this.status != Status.BOOKED) {
            return false;
        }
        
        this.status = Status.AVAILABLE;
        this.bookingId = null;
        this.bookedById = null;
        this.bookedByName = null;

        
        return true;
    }

    public boolean block(String adminId, String adminName) {
        if (this.status == Status.BLOCKED) {
            return false;
        }
        
        this.status = Status.BLOCKED;
        this.blockedById = adminId;
        this.blockedByName = adminName;
        
        return true;
    }

    public boolean unblock() {
        if (this.status != Status.BLOCKED) {
            return false;
        }
        
        this.status = Status.AVAILABLE;
        this.blockedById = null;
        this.blockedByName = null;

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
        
        result = this.facility.getFacilityName().compareTo(other.facility.getFacilityName());
        if (result != 0) {
            return result;
        }
        
        result = this.facility.getRoomType().compareTo(other.facility.getRoomType());
        if (result != 0) {
            return result;
        }

        result = this.facility.getRoomName().compareTo(other.facility.getRoomName());
        if (result != 0) {
            return result;
        }

        return this.startTime.compareTo(other.startTime);
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
    
    // Not for display
    @Override
    public String toString() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dateFormate = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        return String.format("Timeslot[%s | %s | %s | %s–%s | %s | BookedBy: %s]",
            timeslotId,
            facility.getFacilityId(),
            date.format(dateFormate),
            startTime.format(timeFormat),
            endTime.format(timeFormat),
            status,
            bookedById != null ? bookedById : "-",
            bookedByName != null ? bookedByName : "-",
            blockedById != null ? blockedById : "-",
            blockedByName != null ? blockedByName : "-"
        );
    }    
}