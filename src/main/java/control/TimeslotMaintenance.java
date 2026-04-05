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
    private SortedArrayList<Timeslot> timeslotListDB;
    private TimeslotDAO timeslotDAO;
    
    public TimeslotMaintenance() {
        timeslotDAO = new TimeslotDAO("src/main/resources/timeslot.dat");
        timeslotListDB = timeslotDAO.retrieveFromFile();
    }

    public SortedArrayList<Timeslot> getTimeslotListDB() {
        return timeslotListDB;
    }
    
    // -----------------------------------------
    // CREATE
    // -----------------------------------------

    public int generateDaySlotsForOneFacility(Facility facility, LocalDate date) {
        if (facility == null) {
            return 0;
        }
        
        int addedSlot = generateDaySlotsForOneFacilityNoSave(facility, date);
        
        if (addedSlot > 0) timeslotDAO.saveToFile(timeslotListDB);
        
        return addedSlot;
    }

    public int generateDaySlotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        if (facilityList == null || facilityList.isEmpty()) {
            return 0;
        }
        
        int addedSlot = 0;
        Iterator<Facility> iterator = facilityList.getIterator();

        while (iterator.hasNext()) {
            addedSlot += generateDaySlotsForOneFacilityNoSave(iterator.next(), date);
        }

        if (addedSlot > 0) timeslotDAO.saveToFile(timeslotListDB);
        
        return addedSlot;
    }
    
    private int generateDaySlotsForOneFacilityNoSave(Facility facility, LocalDate date) {
        if (facility == null) {
            return 0;
        }
        
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

        return addedSlot;
    }

    // -----------------------------------------
    // READ
    // -----------------------------------------
    
    public SortedArrayList<Timeslot> getTimeslotsForOneFacility(Facility facility, LocalDate date) {
        if (facility == null) {
            return new SortedArrayList<>();
        }
                
        SortedArrayList<Timeslot> result = new SortedArrayList<>();
        
        Timeslot probe = new Timeslot(facility, date, Timeslot.DAY_START);
        int startPosition = SortedListHelper.findStartPosition(timeslotListDB, probe);

        for (int i = startPosition; i <= timeslotListDB.getNumberOfEntries(); i++) {
            Timeslot timeslot = timeslotListDB.getEntry(i);
            
            if (SortedListHelper.isPastTargetDateAndRoomName(timeslot, facility, date)) break;
            
            if (timeslot.getDate().isEqual(date)
                    && timeslot.getFacility().getFacilityId().equals(facility.getFacilityId())) {
                result.add(timeslot);
            }
        }
        
        return result;
    }
    
    public SortedArrayList<Timeslot> getTimeslotsForMultipleFacilities (SortedArrayList<Facility> facilityList, LocalDate date) {
        if (facilityList == null || facilityList.isEmpty()) {
            return new SortedArrayList<>();
        }

        SortedArrayList<Timeslot> result = new SortedArrayList<>();

        for (int i = 1; i <= facilityList.getNumberOfEntries(); i++) {
            Facility facility = facilityList.getEntry(i);

            Timeslot probe = new Timeslot(facility, date, Timeslot.DAY_START);
            int startPosition = SortedListHelper.findStartPosition(timeslotListDB, probe);

            for (int j = startPosition; j <= timeslotListDB.getNumberOfEntries(); j++) {
                Timeslot timeslot = timeslotListDB.getEntry(j);

                if (SortedListHelper.isPastTargetDateAndRoomName(timeslot, facility, date)) break;

                if (timeslot.getDate().isEqual(date) && timeslot.getFacility().getFacilityId().equals(facility.getFacilityId())) {
                    result.add(timeslot);
                }
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

    public boolean blockOneTimeslot(String timeslotId, String adminId, String adminName) {
        Timeslot timeslot = findTimeslotById(timeslotId);
        
        if (timeslot == null) {
            return false;
        }
        
        boolean success = timeslot.block(adminId, adminName);
        
        if (success) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return success;
    }
    
    public int blockMultipleTimeslotsForOneFacility(Facility facility, LocalDate date, String adminId, String adminName) {
        if (facility == null) {
            return 0;
        }
        
        int blockedSlot = 0;
        
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForOneFacility(facility, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().block(adminId, adminName)) {
                blockedSlot++;
            }
        }
        
        if (blockedSlot > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return blockedSlot;
    }
    
    public int blockMultipleTimeslotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date, String adminId, String adminName) {
        if (facilityList == null || facilityList.isEmpty()) {
            return 0;
        }
        
        int blockedSlot = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForMultipleFacilities(facilityList, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().block(adminId, adminName)) {
                blockedSlot++;
            }
        }
        
        if (blockedSlot > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return blockedSlot;
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
        if (facility == null) {
            return 0;
        }
        
        int unblockedSlot = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForOneFacility(facility, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().unblock()) {
                unblockedSlot++;
            }
        }
        
        if (unblockedSlot > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return unblockedSlot;
    }
    
    public int unblockMultipleTimeslotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        if (facilityList == null || facilityList.isEmpty()) {
            return 0;
        }
        
        int unblockedSlot = 0;
        SortedArrayList<Timeslot> timeslotList = getTimeslotsForMultipleFacilities(facilityList, date);
        Iterator<Timeslot> iterator = timeslotList.getIterator();
        
        while (iterator.hasNext()) {
            if (iterator.next().unblock()) {
                unblockedSlot++;
            }
        }
        
        if (unblockedSlot > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return unblockedSlot;
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
        if (facility == null) {
            return 0;
        }
        
        int deletedSlot = 0;
        SortedArrayList<Timeslot> timeslotListToRemove = getTimeslotsForOneFacility(facility, date);
        Iterator<Timeslot> iterator = timeslotListToRemove.getIterator();
        
        while (iterator.hasNext()) {
            Timeslot timeslot = iterator.next();
            
            if (timeslot.isAvailable() && timeslotListDB.remove(timeslot)) {
                deletedSlot++;
            }
        }
        
        if (deletedSlot > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }
        
        return deletedSlot;
    }
    
    public int deleteAvailableTimeslotsForMultipleFacilities(SortedArrayList<Facility> facilityList, LocalDate date) {
        if (facilityList == null || facilityList.isEmpty()) {
            return 0;
        }
        
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
      
    public boolean bookOneTimeslot(String timeslotId, String bookingId, String userId, String userName) {
        Timeslot timeslot = findTimeslotById(timeslotId);

        if (timeslot == null) {
            return false;
        }

        boolean success = timeslot.book(bookingId, userId, userName);

        if (success) {
            timeslotDAO.saveToFile(timeslotListDB);
        }

        return success;
    }

    public int releaseSlotsByBookingId(String bookingId) {
        int count = 0;
        Iterator<Timeslot> it = timeslotListDB.getIterator();

        while (it.hasNext()) {
            Timeslot slot = it.next();
            if (bookingId.equals(slot.getBookingId()) && slot.isBooked()) {
                slot.cancel();
                count++;
            }
        }

        if (count > 0) {
            timeslotDAO.saveToFile(timeslotListDB);
        }

        return count;
    }
    
    public void reloadFromFile() {
        timeslotListDB = timeslotDAO.retrieveFromFile();
    }
}