package control;

import adt.SortedArrayList;
import dao.TimeslotDAO;
import entity.Facility;
import entity.Timeslot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import static utility.SortedListHelper.compareStringIfExceedTarget;

public class TimeslotMaintenance {
    public static final int MAX_CONSECUTIVE_BLOCKS = 4;
    private SortedArrayList<Timeslot> timeslotList;
    private TimeslotDAO timeslotDAO;
    
    public TimeslotMaintenance() {
        timeslotDAO = new TimeslotDAO("timeslot.dat");
        timeslotList = timeslotDAO.retrieveFromFile();
    }
    
    // -----------------------------------------
    // CREATE
    // -----------------------------------------

    public int generateDaySlotsForOneFacility(String facilityId, LocalDate date) {
        int addedSlot = 0;
        LocalTime cursor = Timeslot.DAY_START;

        while (cursor.isBefore(Timeslot.DAY_END)) {
            Timeslot timeslot = new Timeslot(facilityId, date, cursor);

            // Only add if this exact slot doesn't already exist
            if (!timeslotList.contains(timeslot)) {
                timeslotList.add(timeslot);
                addedSlot++;
            }

            cursor = cursor.plusMinutes(Timeslot.MINUTES_PER_BLOCK);
        }

        timeslotDAO.saveToFile(timeslotList);
        
        return addedSlot;
    }

    public int generateDaySlotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        int addedSlot = 0;
        Iterator<Facility> iterator = facilityList.getIterator();

        while (iterator.hasNext()) {
            Facility facility = iterator.next();
                        
            addedSlot += generateDaySlotsForOneFacility(facility.getFacilityId(), date);
        }

        return addedSlot;
    }

    // -----------------------------------------
    // READ
    // -----------------------------------------

    public SortedArrayList<Timeslot> getTimeslots(SortedArrayList<Facility> facilityList, LocalDate date) {
        SortedArrayList<String> matchingFacilityIdList = new SortedArrayList<>();
        Iterator<Facility> facilityIterator = facilityList.getIterator();

        while (facilityIterator.hasNext()) {
            Facility facility = facilityIterator.next();
            
            matchingFacilityIdList.add(facility.getFacilityId());
        }

        SortedArrayList<Timeslot> result = new SortedArrayList<>();
        Iterator<Timeslot> timeslotIterator = timeslotList.getIterator();
        
        // This is a sorted list
        // If the list stop adding something after adding something, means nothing will match after it, can exit early
        boolean trigger = false;

        while (timeslotIterator.hasNext()) {
            Timeslot timeslot = timeslotIterator.next();

            if (matchingFacilityIdList.contains(timeslot.getFacilityId()) && timeslot.getDate().isEqual(date)) {
                result.add(timeslot);
                trigger = true;
            }
            
            else {
                if (trigger) {
                    break;
                }
            }
        }

        return result;
    }
    
    public Timeslot findTimeslotById(String timeslotId) {
        Iterator<Timeslot> iterator = timeslotList.getIterator();

        while (iterator.hasNext()) {
            Timeslot timeslot = iterator.next();
            
            if (timeslot.getTimeslotId().equals(timeslotId)) {
                return timeslot;
            }
            
            if (compareStringIfExceedTarget(timeslot.getTimeslotId(), timeslotId)) {
                return null;
            }
        }

        return null;
    }

    public int getTotalSlotCount() {
        return timeslotList.getNumberOfEntries();
    }

    // -----------------------------------------
    // UPDATE
    // -----------------------------------------

    public boolean blockOneTimeslot(String timeslotId, String userId) {
        Timeslot timeslot = findTimeslotById(timeslotId);
        
        if (timeslot == null) {
            return false;
        }
        
        boolean success = timeslot.block(userId);
        
        if (success) {
            timeslotDAO.saveToFile(timeslotList);
        }
        
        return success;
    }

    public boolean unblockOneTimeslot(String timeslotId) {
        Timeslot timeslot = findTimeslotById(timeslotId);
        
        if (timeslot == null) {
            return false;
        }

        boolean success = timeslot.unblock();
        
        if (success) {
            timeslotDAO.saveToFile(timeslotList);
        }
        
        return success;
    }
    
    public int blockMultipleTimeslots(SortedArrayList<Facility> facilityList, LocalDate date, String userId) {
        int count = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslots(facilityList, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().block(userId)) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotList);
        }
        
        return count;
    }
    
    public int unblockMultipleTimeslots(SortedArrayList<Facility> facilityList, LocalDate date) {
        int count = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslots(facilityList, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().unblock()) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotList);
        }
        
        return count;
    }
    
    // -----------------------------------------
    // DELETE
    // -----------------------------------------

    public int deleteAvailableTimeslotsForOneFacility(String facilityId, LocalDate date) {
        SortedArrayList<Timeslot> timeslotListToRemove = new SortedArrayList<>();
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        boolean trigger = false;

        while (iterator.hasNext()) {
            Timeslot timeslot = iterator.next();
            
            if (timeslot.getFacilityId().equals(facilityId) && timeslot.getDate().equals(date) && timeslot.isAvailable()) {
                timeslotListToRemove.add(timeslot);
                trigger = true;
            }
            
            else {
                if (trigger) {
                    break;
                }
            }
        }
        
        return removeTimeslotList(timeslotListToRemove);
    }
    
    public int deleteAvailableTimeslotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        SortedArrayList<String> matchingFacilityIdList = new SortedArrayList<>();
        Iterator<Facility> facilityIterator = facilityList.getIterator();
        
        while (facilityIterator.hasNext()) {
            Facility facility = facilityIterator.next();
            
            matchingFacilityIdList.add(facility.getFacilityId());
        }
        
        SortedArrayList<Timeslot> timeslotListToRemove = new SortedArrayList<>();
        Iterator<Timeslot> timeslotIterator = timeslotList.getIterator();
        
        boolean trigger = false;

        while (timeslotIterator.hasNext()) {
            Timeslot timeslot = timeslotIterator.next();

            if (matchingFacilityIdList.contains(timeslot.getFacilityId()) && timeslot.getDate().isEqual(date)) {
                timeslotListToRemove.add(timeslot);
                trigger = true;
            }
            
            else {
                if (trigger) {
                    break;
                }
            }
        }

        return removeTimeslotList(timeslotListToRemove);
    }
    
    private int removeTimeslotList(SortedArrayList<Timeslot> timeslotListToRemove) {
        int count = 0;
        Iterator<Timeslot> iterator = timeslotListToRemove.getIterator();
        
        while (iterator.hasNext()) {
            if (timeslotList.remove(iterator.next())) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotList);
        }
        
        return count;
    }
 
    // I didnt do this part
    // =========================================================================
    // BOOKING VALIDATION — used by BookingMaintenance (control-to-control)
    // =========================================================================

    /**
     * Checks whether a consecutive range of blocks is fully available for booking.
     *
     * @param facilityId  Target facility.
     * @param date        Target date.
     * @param startTime   Start of the first block.
     * @param blockCount  Number of consecutive 30-min blocks (1–4).
     * @return true if all requested blocks are AVAILABLE.
     */
    public boolean areConsecutiveSlotsAvailable(int facilityId, LocalDate date,
                                                 LocalTime startTime, int blockCount) {
        if (blockCount < 1 || blockCount > MAX_CONSECUTIVE_BLOCKS) return false;

        LocalTime cursor = startTime;
        for (int i = 0; i < blockCount; i++) {
            String slotId = Timeslot.generateTimeslotId(String.valueOf(facilityId), date, cursor);
            Timeslot slot = findTimeslotById(slotId);

            if (slot == null || !slot.isAvailable()) return false;

            cursor = cursor.plusMinutes(Timeslot.MINUTES_PER_BLOCK);
        }

        return true;
    }

    /**
     * Books a consecutive range of slots atomically.
     * Only proceeds if ALL slots in the range are available (collision check).
     *
     * @return true if all slots were successfully booked.
     */
    public boolean bookConsecutiveSlots(int facilityId, LocalDate date,
                                         LocalTime startTime, int blockCount,
                                         String userId, String bookingId) {
        if (!areConsecutiveSlotsAvailable(facilityId, date, startTime, blockCount)) {
            return false;
        }

        LocalTime cursor = startTime;
        for (int i = 0; i < blockCount; i++) {
            String slotId = Timeslot.generateTimeslotId(String.valueOf(facilityId), date, cursor);
            Timeslot slot = findTimeslotById(slotId);
            slot.book(userId, bookingId);
            cursor = cursor.plusMinutes(Timeslot.MINUTES_PER_BLOCK);
        }

        saveToFile();
        return true;
    }

    /**
     * Releases all slots associated with a bookingId (for cancellation).
     *
     * @return Number of slots released.
     */
    public int releaseSlotsByBookingId(String bookingId) {
        int count = 0;
        Iterator<Timeslot> it = timeslotList.getIterator();

        while (it.hasNext()) {
            Timeslot slot = it.next();
            if (bookingId.equals(slot.getBookingId()) && slot.isBooked()) {
                slot.cancel();
                count++;
            }
        }

        if (count > 0) saveToFile();
        return count;
    }
}