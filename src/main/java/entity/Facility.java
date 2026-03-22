package entity;

import java.io.Serializable;

/**
 * @author Ong Hao Howard
 */

public class Facility implements Serializable, Comparable<Facility> {
    private int facilityId;
    // facilityName = Cyber Centre Discussion Room / Library Discussion Room, Individual Study Room / Sports Facilities
    private String facilityName;
    // roomType = Discussion Room (1 PC) / Discussion Room with Projector (2 PCs) / Pickleball Court / etc
    private String roomType;
    // roomName = B002 / TA255 / Pickleball Court 1 / etc
    private String roomName;
    
    public Facility() {}

    public Facility(int facilityId, String facilityName, String roomType, String roomName) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.roomType = roomType;
        this.roomName = roomName;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
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

    @Override
    public String toString() {
        return facilityName + " - " + roomType + " (" + roomName + ")";
    }
    
    @Override
    public int compareTo(Facility o) {
        int result = this.facilityName.compareToIgnoreCase(o.facilityName);
        if (result != 0) {
            return result;
        }
        
        result = this.roomType.compareToIgnoreCase(o.roomType);
        if (result != 0) {
            return result;
        }
        
        return this.roomName.compareToIgnoreCase(o.roomName);
    }
}