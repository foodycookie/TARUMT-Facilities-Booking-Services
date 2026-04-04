package utility;

import adt.SortedArrayList;
import entity.Facility;
import entity.Timeslot;
import java.time.LocalDate;

/**
 * @author Ong Hao Howard
 */

public class SortedListHelper {
   public static <T extends Comparable<T>> int findStartPosition(SortedArrayList<T> list, T probe) {
       int position = list.getPosition(probe);

       if (position >= 1) {
           // Found
           return position;
       } 
       
       else {
           // Not found, return insertion point
           return -(position);
       }
   }
   
    // Checks whether iteration should stop early when scanning a timeslot list
    public static boolean isPastTargetDate(Timeslot timeslot, LocalDate date) {
        return timeslot.getDate().isAfter(date);
    }
    
    public static boolean isPastTargetDateAndFacilityName(Timeslot timeslot, Facility facility, LocalDate date) {
        if (timeslot.getDate().isAfter(date)) return true;
        if (timeslot.getDate().isBefore(date)) return false;

        int facilityNameComparison = timeslot.getFacility().getFacilityName().compareToIgnoreCase(facility.getFacilityName());        
        return facilityNameComparison > 0;
    }
    
    public static boolean isPastTargetDateAndRoomType(Timeslot timeslot, Facility facility, LocalDate date) {
        if (timeslot.getDate().isAfter(date)) return true;
        if (timeslot.getDate().isBefore(date)) return false;

        int facilityNameComparison = timeslot.getFacility().getFacilityName().compareToIgnoreCase(facility.getFacilityName());
        if (facilityNameComparison > 0) return true;
        if (facilityNameComparison < 0) return false;

        int roomTypeComparison = timeslot.getFacility().getRoomType().compareToIgnoreCase(facility.getRoomType());
        return roomTypeComparison > 0;        
    }
    
    public static boolean isPastTargetDateAndRoomName(Timeslot timeslot, Facility facility, LocalDate date) {
        if (timeslot.getDate().isAfter(date)) return true;
        if (timeslot.getDate().isBefore(date)) return false;

        int facilityNameComparison = timeslot.getFacility().getFacilityName().compareToIgnoreCase(facility.getFacilityName());
        if (facilityNameComparison > 0) return true;
        if (facilityNameComparison < 0) return false;

        int roomTypeComparison = timeslot.getFacility().getRoomType().compareToIgnoreCase(facility.getRoomType());
        if (roomTypeComparison > 0) return true;
        if (roomTypeComparison < 0) return false;

        int roomNameComparison = timeslot.getFacility().getRoomName().compareToIgnoreCase(facility.getRoomName());
        return roomNameComparison > 0;
    }
}