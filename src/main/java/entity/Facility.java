
package entity;

import java.io.Serializable;

/*
 * Tiw Hong Xuan
*/

public class Facility implements Serializable, Comparable<Facility> {
    private static final long serialVersionUID = 1L;

    private String facilityId;
    private String facilityName;
    private String roomType;
    private String roomName;

    public Facility() {}

    public Facility(String facilityId, String facilityName, String roomType, String roomName) {
        this.facilityId   = facilityId;
        this.facilityName = facilityName;
        this.roomType     = roomType;
        this.roomName     = roomName;
    }

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

    @Override
    public String toString() {
        return facilityName + " - " + roomType + " (" + roomName + ")";
    }

    @Override
    public int compareTo(Facility o) {
        int result = this.facilityName.compareToIgnoreCase(o.facilityName);
        if (result != 0) return result;

        result = this.roomType.compareToIgnoreCase(o.roomType);
        if (result != 0) return result;

        return this.roomName.compareToIgnoreCase(o.roomName);
    }
}