package dao;

import adt.SortedArrayList;
import entity.Timeslot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author Ong Hao Howard
 */

public class TimeslotDAO {
    private String fileName;

    public TimeslotDAO(String filename) {
        this.fileName = filename;
    }

    public void saveToFile(SortedArrayList<Timeslot> timeslotList) {
        File file = new File(fileName);
        
        try (ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file))) {
            ooStream.writeObject(timeslotList);
        } catch (FileNotFoundException ex) {
            System.out.println("\nFile not found.");
        } catch (IOException ex) {
            System.out.println("\nCannot save to file.");}
    }

    @SuppressWarnings("unchecked")
    public SortedArrayList<Timeslot> retrieveFromFile() {
        File file = new File(fileName);
        SortedArrayList<Timeslot> timeslotList = new SortedArrayList<>();

        try (ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file))) {
            timeslotList = (SortedArrayList<Timeslot>) oiStream.readObject();
        } catch (FileNotFoundException ex) {
            System.out.println("\nNo existing timeslot file found. A new file will be created when data is saved.");
        } catch (IOException ex) {
            System.out.println("\nCannot read from file.");
        } catch (ClassNotFoundException ex) {
            System.out.println("\nClass not found.");
        } finally{
            return timeslotList;
        } 
    }
}