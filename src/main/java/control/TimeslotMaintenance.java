package control;

import adt.SortedArrayList;
import dao.TimeslotDAO;
import entity.Facility;
import entity.Timeslot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Iterator;
import utility.SortedListHelper;
public class TimeslotMaintenance {
    public static final int MAX_CONSECUTIVE_BLOCKS = 2;
    private SortedArrayList<Timeslot> timeslotListDB;
    private TimeslotDAO timeslotDAO;
    
    public TimeslotMaintenance() {
        timeslotDAO = new TimeslotDAO("timeslot.dat");
        timeslotListDB = timeslotDAO.retrieveFromFile();
    }
    
    // -----------------------------------------
    // CREATE
    // -----------------------------------------

    public int generateDaySlotsForOneFacility(Facility facility, LocalDate date) {
        int addedSlot = 0;
        LocalTime cursor = Timeslot.DAY_START;

        while (cursor.isBefore(Timeslot.DAY_END)) {
            Timeslot timeslot = new Timeslot(facility, date, cursor);

            if (!timeslotListDB.contains(timeslot)) {
                timeslotListDB.add(timeslot);
                addedSlot++;
            }

            cursor = cursor.plusMinutes(Timeslot.MINUTES_PER_BLOCK);
        }

        timeslotDAO.saveToFile(timeslotListDB);
        
        return addedSlot;
    }

    public int generateDaySlotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        int addedSlot = 0;
        Iterator<Facility> iterator = facilityList.getIterator();

        while (iterator.hasNext()) {
            Facility facility = iterator.next();
                        
            addedSlot += generateDaySlotsForOneFacility(facility, date);
        }

        return addedSlot;
    }

    // -----------------------------------------
    // READ
    // -----------------------------------------
    
    public SortedArrayList<Timeslot> getTimeslotsForOneFacility(Facility facility, LocalDate date) {
        SortedArrayList<Timeslot> result = new SortedArrayList<>();
        
        Timeslot probe = new Timeslot(facility, date, Timeslot.DAY_START);
        int startPosition = SortedListHelper.findStartPosition(timeslotListDB, probe);

        for (int i = startPosition; i <= timeslotListDB.getNumberOfEntries(); i++) {
            Timeslot timeslot = timeslotListDB.getEntry(i);
            
            if (SortedListHelper.isPastTargetDateAndRoomName(timeslot, facility, date)) break;
            
            if (timeslot.getFacility().getFacilityId().equals(facility.getFacilityId())) {
                result.add(timeslot);
            }
        }
        
        return result;
    }
    
    public SortedArrayList<Timeslot> getTimeslotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        SortedArrayList<String> matchingFacilityIdList = new SortedArrayList<>();
        
        Iterator<Facility> facilityIterator = facilityList.getIterator();

        while (facilityIterator.hasNext()) {
            Facility facility = facilityIterator.next();
            
            matchingFacilityIdList.add(facility.getFacilityId());
        }
        
        SortedArrayList<Timeslot> result = new SortedArrayList<>();

        Timeslot probe = new Timeslot(facilityList.getEntry(1), date, Timeslot.DAY_START);
        int startPosition = SortedListHelper.findStartPosition(timeslotListDB, probe);

        for (int i = startPosition; i <= timeslotListDB.getNumberOfEntries(); i++) {
            Timeslot timeslot = timeslotListDB.getEntry(i);

            if (SortedListHelper.isPastTargetDate(timeslot, date)) break;

            if (timeslot.getDate().isEqual(date) && matchingFacilityIdList.contains(timeslot.getFacility().getFacilityId())) {
                result.add(timeslot);
            }
        }

        return result;
    }
    
    public Timeslot findTimeslotById(String timeslotId) {
        Iterator<Timeslot> iterator = timeslotListDB.getIterator();

        while (iterator.hasNext()) {
            Timeslot timeslot = iterator.next();
            
            if (timeslot.getTimeslotId().equals(timeslotId)) {
                return timeslot;
            }
        }

        return null;
    }

    public int getTotalSlotCount() {
        return timeslotListDB.getNumberOfEntries();
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
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return success;
    }
    
    public int blockMultipleTimeslotsForOneFacility(Facility facility, LocalDate date, String userId) {
        int count = 0;
        
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForOneFacility(facility, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().block(userId)) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return count;
    }
    
    public int blockMultipleTimeslotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date, String userId) {
        int count = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForMultipleFacilities(facilityList, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().block(userId)) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return count;
    }
    
    public boolean unblockOneTimeslot(String timeslotId) {
        Timeslot timeslot = findTimeslotById(timeslotId);
        
        if (timeslot == null) {
            return false;
        }

        boolean success = timeslot.unblock();
        
        if (success) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return success;
    }
    
    public int unblockMultipleTimeslotsForOneFacility(Facility facility, LocalDate date) {
        int count = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForOneFacility(facility, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().unblock()) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return count;
    }
    
    public int unblockMultipleTimeslotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        int count = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForMultipleFacilities(facilityList, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().unblock()) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return count;
    }
    
    // -----------------------------------------
    // DELETE
    // -----------------------------------------
    
    public boolean deleteOneTimeslot(String timeslotId) {
        Timeslot timeslot = findTimeslotById(timeslotId);
        
        if (timeslot == null || !timeslot.isAvailable()) {
            return false;
        }

        boolean success = timeslotListDB.remove(timeslot);
        
        if (success) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return success;
    }

    public int deleteAvailableTimeslotsForOneFacility(Facility facility, LocalDate date) {
        int count = 0;
        SortedArrayList<Timeslot> timeslotListToRemove = getTimeslotsForOneFacility(facility, date);
        Iterator<Timeslot> iterator = timeslotListToRemove.getIterator();
        
        while (iterator.hasNext()) {
            Timeslot timeslot = iterator.next();
            
            if (timeslot.isAvailable() && timeslotListDB.remove(timeslot)) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return count;
    }
    
    public int deleteAvailableTimeslotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        int count = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForMultipleFacilities(facilityList, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            Timeslot timeslot = iterator.next();
            
            if (timeslot.isAvailable() && timeslotListDB.remove(timeslot)) {
                count++;
            }
        }
        
        if (count > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return count;
    }
    
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
    */

    /**
     * Books a consecutive range of slots atomically.
     * Only proceeds if ALL slots in the range are available (collision check).
     *
     * @return true if all slots were successfully booked.
     
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
    */

    /**
     * Releases all slots associated with a bookingId (for cancellation).
     *
     * @return Number of slots released.
     
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
    */
}