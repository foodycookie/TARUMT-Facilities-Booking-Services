package dao;

import adt.SortedArrayList;
import adt.SortedListInterface;
import entity.Booking;
import java.io.*;

/*
 * Lai Yu Hui
*/

public class BookingDAO {
    private String fileName = "src/main/resources/bookings.dat";

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

    public SortedListInterface<Booking> retrieveFromFile() {

        File file = new File(fileName);

        SortedListInterface<Booking> bookingList = new SortedArrayList<>();

        if (!file.exists()) {
            return bookingList;
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