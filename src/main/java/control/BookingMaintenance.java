package control;

import adt.*;
import boundary.BookingMaintenanceUI;
import dao.BookingDAO;
import entity.Booking;
import utility.InputOutputHelper;

public class BookingMaintenance {

    private SortedListInterface<Booking> bookingList = new SortedArrayList<>();
    private BookingDAO bookingDAO = new BookingDAO();
    private BookingMaintenanceUI bookingUI = new BookingMaintenanceUI();

    public BookingMaintenance() {
        bookingList = bookingDAO.retrieveFromFile();
    }

    public void runBooking() {

        int choice;

        do {
            choice = bookingUI.getMenuChoice();

            switch (choice) {

                case 0:
                    InputOutputHelper.displayExitMessage();
                    break;

                case 1:
                    createBooking();
                    bookingUI.displayAllBookings(getAllBookings());
                    break;

                case 2:
                    cancelBooking();
                    bookingUI.displayAllBookings(getAllBookings());
                    break;

                case 3:
                    updateBooking();
                    bookingUI.displayAllBookings(getAllBookings());
                    break;

                case 4:
                    showAvailableSlots();
                    break;

                case 5:
                    bookingUI.displayAllBookings(getAllBookings());
                    break;

                default:
                    InputOutputHelper.displayInvalidChoiceMessage();
            }

        } while (choice != 0);
    }

    // CREATE
    public void createBooking() {

        Booking newBooking = bookingUI.inputBookingDetails();

        if (!isAvailable(newBooking)) {
            bookingUI.displayMessage("Booking FAILED (Time slot not available)");
            return;
        }

        bookingList.add(newBooking);
        bookingDAO.saveToFile(bookingList);

        bookingUI.displayMessage("Booking SUCCESS");
    }

    // DELETE
    public void cancelBooking() {

        int bookingID = bookingUI.inputBookingID();

        Booking found = findBookingByID(bookingID);

        if (found != null) {
            bookingList.remove(found);
            bookingDAO.saveToFile(bookingList);
            bookingUI.displayMessage("Booking cancelled");
        } else {
            bookingUI.displayMessage("Booking not found");
        }
    }

    // UPDATE
    public void updateBooking() {

        int bookingID = bookingUI.inputBookingID();

        Booking oldBooking = findBookingByID(bookingID);

        if (oldBooking == null) {
            bookingUI.displayMessage("Booking not found");
            return;
        }

        Booking updatedBooking = bookingUI.inputBookingDetails();

        // remove old first
        bookingList.remove(oldBooking);

        if (!isAvailable(updatedBooking)) {
            bookingList.add(oldBooking); // rollback
            bookingUI.displayMessage("Update FAILED (Time slot not available)");
            return;
        }

        bookingList.add(updatedBooking);
        bookingDAO.saveToFile(bookingList);

        bookingUI.displayMessage("Update SUCCESS");
    }

    // CHECK AVAILABILITY
    public boolean isAvailable(Booking newBooking) {

        for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {

            Booking b = bookingList.getEntry(i);

            if (b.getFacilityID() == newBooking.getFacilityID() &&
                b.getDate().equals(newBooking.getDate()) &&
                b.getTimeSlot().equals(newBooking.getTimeSlot())) {

                return false;
            }
        }

        return true;
    }

    // SHOW AVAILABLE TIME SLOTS
    public void showAvailableSlots() {

        int facilityID = bookingUI.inputFacilityID();
        String date = bookingUI.inputDate();

        String[] allSlots = {
            "09:00-10:00",
            "10:00-11:00",
            "11:00-12:00",
            "12:00-13:00",
            "13:00-14:00",
            "14:00-15:00",
            "15:00-16:00",
            "16:00-17:00",
            "17:00-18:00"
        };

        String output = "Available Slots:\n";

        for (String slot : allSlots) {

            boolean occupied = false;

            for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {

                Booking b = bookingList.getEntry(i);

                if (b.getFacilityID() == facilityID &&
                    b.getDate().equals(date) &&
                    b.getTimeSlot().equals(slot)) {

                    occupied = true;
                    break;
                }
            }

            if (!occupied) {
                output += slot + "\n";
            }
        }

        bookingUI.displayMessage(output);
    }

    // GET ALL BOOKINGS
    public String getAllBookings() {

        String output = "";

        for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {
            output += bookingList.getEntry(i) + "\n";
        }

        return output;
    }

    // FIND BOOKING (IMPORTANT)
    private Booking findBookingByID(int bookingID) {

        for (int i = 1; i <= bookingList.getNumberOfEntries(); i++) {

            Booking b = bookingList.getEntry(i);

            if (b.getBookingID() == bookingID) {
                return b;
            }
        }

        return null;
    }
  
}