package control;

import adt.SortedArrayList;
import entity.Booking;
import entity.Facility;
import entity.User;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author TAY TIAN YOU
 */
public class BookingMaintenance {

    private static final String BOOKING_FILE = "src/main/resources/booking.dat";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private SortedArrayList<Booking> bookingList;

    private final UserMaintenance userControl;
    private final FacilityMaintenance facilityControl;

    public BookingMaintenance() {
        userControl = new UserMaintenance();
        facilityControl = new FacilityMaintenance();
        bookingList = retrieveFromFile();
    }

    public boolean isEmpty() {
        return bookingList.isEmpty();
    }

    public int getNumberOfBookings() {
        return bookingList.getNumberOfEntries();
    }

    public String generateBookingId() {
        int max = 0;

        for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {
            Booking booking = bookingList.getEntry(i);

            if (booking != null && booking.getBookingID() != null && booking.getBookingID().startsWith("B")) {
                try {
                    int num = Integer.parseInt(booking.getBookingID().substring(1));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException e) {
                    // ignore old invalid data
                }
            }
        }

        return "B" + String.format("%05d", max + 1);
    }

    public boolean isExistingUser(String userId) {
        return userControl.findUserByUserId(userId) != null;
    }

    public User findUser(String userId) {
        return userControl.findUserByUserId(userId);
    }

    public Facility findFacility(String facilityId) {
        return facilityControl.findByFacilityId(facilityId);
    }

    public Booking findBookingById(String bookingId) {
        for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {
            Booking booking = bookingList.getEntry(i);

            if (booking != null && booking.getBookingID().equalsIgnoreCase(bookingId)) {
                return booking;
            }
        }

        return null;
    }

    public SortedArrayList<Booking> getBookingsByUserId(String userId) {
        SortedArrayList<Booking> result = new SortedArrayList<>();

        for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {
            Booking booking = bookingList.getEntry(i);

            if (booking != null && booking.getUserID().equalsIgnoreCase(userId)) {
                result.add(booking);
            }
        }

        return result;
    }

    public boolean addBooking(String userId, String facilityId, String date, String timeSlot) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }

        if (facilityId == null || facilityId.trim().isEmpty()) {
            return false;
        }

        if (date == null || date.trim().isEmpty()) {
            return false;
        }

        if (timeSlot == null || timeSlot.trim().isEmpty()) {
            return false;
        }

        User user = userControl.findUserByUserId(userId);
        if (user == null) {
            return false;
        }

        Facility facility = facilityControl.findByFacilityId(facilityId);
        if (facility == null) {
            return false;
        }

        if (!isValidDate(date)) {
            return false;
        }

        if (!isValidTimeSlot(timeSlot)) {
            return false;
        }

        if (hasBookingConflict(facilityId, date, timeSlot)) {
            return false;
        }

        String bookingId = generateBookingId();

        Booking newBooking = new Booking(
                bookingId,
                user.getUserId(),
                facility,
                date,
                timeSlot
        );

        boolean added = bookingList.add(newBooking);

        if (added) {
            saveToFile();
        }

        return added;
    }

    public boolean cancelBooking(String bookingId) {
        Booking booking = findBookingById(bookingId);

        if (booking == null) {
            return false;
        }

        boolean removed = bookingList.remove(booking);

        if (removed) {
            saveToFile();
        }

        return removed;
    }

    public boolean cancelBookingByUser(String bookingId, String userId) {
        Booking booking = findBookingById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!booking.getUserID().equalsIgnoreCase(userId)) {
            return false;
        }

        boolean removed = bookingList.remove(booking);

        if (removed) {
            saveToFile();
        }

        return removed;
    }

    public String displayAllBookings() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n====================================================================================================\n");
        sb.append(String.format("%-10s %-12s %-25s %-12s %-15s%n",
                "Booking ID", "User ID", "Room Name", "Date", "Time Slot"));
        sb.append("====================================================================================================\n");

        if (bookingList.isEmpty()) {
            sb.append(String.format("%-10s %-12s %-25s %-12s %-15s%n",
                    "-", "-", "No booking records found", "-", "-"));
        } else {
            for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {
                Booking booking = bookingList.getEntry(i);

                if (booking != null) {
                    sb.append(booking.toTableRow()).append("\n");
                }
            }
        }

        sb.append("====================================================================================================\n");
        return sb.toString();
    }

    public String displayBookingsByUser(String userId) {
        SortedArrayList<Booking> userBookings = getBookingsByUserId(userId);
        StringBuilder sb = new StringBuilder();

        sb.append("\n====================================================================================================\n");
        sb.append(String.format("%-10s %-12s %-25s %-12s %-15s%n",
                "Booking ID", "User ID", "Room Name", "Date", "Time Slot"));
        sb.append("====================================================================================================\n");

        if (userBookings.isEmpty()) {
            sb.append(String.format("%-10s %-12s %-25s %-12s %-15s%n",
                    "-", "-", "No current booking found", "-", "-"));
        } else {
            for (int i = 1; i <= userBookings.getNumberOfEntries(); i++) {
                Booking booking = userBookings.getEntry(i);

                if (booking != null) {
                    sb.append(booking.toTableRow()).append("\n");
                }
            }
        }

        sb.append("====================================================================================================\n");
        return sb.toString();
    }

    public boolean hasBookingConflict(String facilityId, String date, String newTimeSlot) {
        LocalTime newStart = extractStartTime(newTimeSlot);
        LocalTime newEnd = extractEndTime(newTimeSlot);

        if (newStart == null || newEnd == null) {
            return true;
        }

        for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {
            Booking existing = bookingList.getEntry(i);

            if (existing == null) {
                continue;
            }

            if (!existing.getFacilityID().equalsIgnoreCase(facilityId)) {
                continue;
            }

            if (!existing.getDate().equals(date)) {
                continue;
            }

            LocalTime existingStart = extractStartTime(existing.getTimeSlot());
            LocalTime existingEnd = extractEndTime(existing.getTimeSlot());

            if (existingStart == null || existingEnd == null) {
                continue;
            }

            boolean overlap = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);

            if (overlap) {
                return true;
            }
        }

        return false;
    }

    public String buildTimeSlot(LocalTime startTime, int durationHours) {
        LocalTime endTime = startTime.plusHours(durationHours);
        return startTime.format(TIME_FORMAT) + "-" + endTime.format(TIME_FORMAT);
    }

    public boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isValidTimeSlot(String timeSlot) {
        LocalTime start = extractStartTime(timeSlot);
        LocalTime end = extractEndTime(timeSlot);

        if (start == null || end == null) {
            return false;
        }

        if (!start.isBefore(end)) {
            return false;
        }

        if (start.isBefore(LocalTime.of(9, 0))) {
            return false;
        }

        if (end.isAfter(LocalTime.of(19, 0))) {
            return false;
        }

        return true;
    }

    private LocalTime extractStartTime(String timeSlot) {
        try {
            String[] parts = timeSlot.split("-");
            return LocalTime.parse(parts[0].trim(), TIME_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalTime extractEndTime(String timeSlot) {
        try {
            String[] parts = timeSlot.split("-");
            return LocalTime.parse(parts[1].trim(), TIME_FORMAT);
        } catch (Exception e) {
            return null;
        }
    }

    public void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(BOOKING_FILE))) {
            oos.writeObject(bookingList);
        } catch (Exception e) {
            System.out.println("Cannot save booking file.");
        }
    }

    @SuppressWarnings("unchecked")
    private SortedArrayList<Booking> retrieveFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(BOOKING_FILE))) {
            return (SortedArrayList<Booking>) ois.readObject();
        } catch (Exception e) {
            return new SortedArrayList<>();
        }
    }

    public void reloadFromFile() {
        bookingList = retrieveFromFile();
    }
}