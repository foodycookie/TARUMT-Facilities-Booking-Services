package dao;

import adt.SortedListInterface;
import adt.SortedArrayList;
import entity.Booking;
import java.io.*;

public class BookingDAO {

    private String fileName = "bookings.dat";

    // SAVE TO FILE
    public void saveToFile(SortedListInterface<Booking> bookingList) {

        File file = new File(fileName);

        try (ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file))) {

            ooStream.writeObject(bookingList);

        } catch (FileNotFoundException ex) {
            System.out.println("File not found.");
        } catch (IOException ex) {
            System.out.println("Error writing to file.");
        }
    }

    // LOAD FROM FILE
    public SortedListInterface<Booking> retrieveFromFile() {

        File file = new File(fileName);

        SortedListInterface<Booking> bookingList = new SortedArrayList<>();

        if (!file.exists()) {
            return bookingList; // return empty list if file not exist
        }

        try (ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file))) {

            bookingList = (SortedArrayList<Booking>) oiStream.readObject();

        } catch (IOException ex) {
            System.out.println("Error reading from file.");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not found.");
        }

        return bookingList;
    }
}