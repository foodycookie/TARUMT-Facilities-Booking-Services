package control;

import adt.SortedArrayList;
import entity.Booking;
import entity.Timeslot;
import entity.User;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author TAY TIAN YOU
 */
public class BookingMaintenance {

    private static final String BOOKING_FILE = "src/main/resources/booking.dat";

    private SortedArrayList<Booking> bookingList;

    private TimeslotMaintenance timeslotControl;

    public BookingMaintenance() {
        timeslotControl = new TimeslotMaintenance();
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
                    // ignore invalid old data
                }
            }
        }

        return "B" + String.format("%05d", max + 1);
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

    public SortedArrayList<Booking> getCurrentUserBookings() {
        User currentUser = UserMaintenance.currentUser;

        if (currentUser == null) {
            return new SortedArrayList<>();
        }

        return getBookingsByUserId(currentUser.getUserId());
    }

    public boolean addBooking(Timeslot chosenTimeslot) {
        User currentUser = UserMaintenance.currentUser;

        if (currentUser == null || chosenTimeslot == null) {
            return false;
        }

        Timeslot actualSlot = timeslotControl.findTimeslotById(chosenTimeslot.getTimeslotId());

        if (actualSlot == null) {
            return false;
        }

        if (!actualSlot.isAvailable()) {
            return false;
        }

        String bookingId = generateBookingId();

        boolean slotBooked = timeslotControl.bookOneTimeslot(
                actualSlot.getTimeslotId(),
                bookingId,
                currentUser.getUserId(),
                currentUser.getUserName()
        );

        if (!slotBooked) {
            return false;
        }
        
        timeslotControl = new TimeslotMaintenance();

        Booking newBooking = new Booking(
                bookingId,
                currentUser.getUserId(),
                actualSlot.getFacility(),
                actualSlot.getDate().toString(),
                actualSlot
        );

        boolean added = bookingList.add(newBooking);

        if (added) {
            saveToFile();
            return true;
        } else {
            timeslotControl.releaseSlotsByBookingId(bookingId);
            return false;
        }
    }

    public boolean cancelCurrentUserBooking(String bookingId) {
        User currentUser = UserMaintenance.currentUser;

        if (currentUser == null) {
            return false;
        }

        Booking booking = findBookingById(bookingId);

        if (booking == null) {
            return false;
        }

        if (!booking.getUserID().equalsIgnoreCase(currentUser.getUserId())) {
            return false;
        }

        boolean removed = bookingList.remove(booking);

        if (removed) {
            saveToFile();

            timeslotControl.reloadFromFile();
            timeslotControl.releaseSlotsByBookingId(bookingId);

            return true;
        }

        return false;
    }

    public String displayCurrentUserBookings() {
        User currentUser = UserMaintenance.currentUser;
        StringBuilder sb = new StringBuilder();

        sb.append("\n====================================================================================================\n");
        sb.append(String.format("%-10s %-12s %-25s %-12s %-15s%n",
                "Booking ID", "User ID", "Room Name", "Date", "Time Slot"));
        sb.append("====================================================================================================\n");

        if (currentUser == null) {
            sb.append(String.format("%-10s %-12s %-25s %-12s %-15s%n",
                    "-", "-", "No current user selected", "-", "-"));
            sb.append("====================================================================================================\n");
            return sb.toString();
        }

        SortedArrayList<Booking> currentUserBookings = getCurrentUserBookings();

        if (currentUserBookings.isEmpty()) {
            sb.append(String.format("%-10s %-12s %-25s %-12s %-15s%n",
                    "-", "-", "No current booking found", "-", "-"));
        } else {
            for (int i = 1; i <= currentUserBookings.getNumberOfEntries(); i++) {
                Booking booking = currentUserBookings.getEntry(i);

                if (booking != null) {
                    sb.append(booking.toTableRow()).append("\n");
                }
            }
        }

        sb.append("====================================================================================================\n");
        return sb.toString();
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