package dao;

import adt.SortedArrayList;
import entity.Admin;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 *
 * @author TAY TIAN YOU
 */

public class AdminDAO {
    private  String fileName;

    public AdminDAO(String filename) {
        this.fileName = filename;
    }

    public void saveToFile(SortedArrayList<Admin> adminList) {
        File file = new File(fileName);

        try (ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file))) {
            ooStream.writeObject(adminList);
        } catch (FileNotFoundException ex) {
            System.out.println("\nFile not found.");
        } catch (IOException ex) {
            System.out.println("\nCannot save to file.");
        }
    }

    @SuppressWarnings("unchecked")
    public SortedArrayList<Admin> retrieveFromFile() {
        File file = new File(fileName);
        SortedArrayList<Admin> adminList = new SortedArrayList<>();

        try (ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file))) {
            adminList = (SortedArrayList<Admin>) oiStream.readObject();
        } catch (FileNotFoundException ex) {
            System.out.println("\nNo existing user file found. A new file will be created when data is saved.");
        } catch (IOException ex) {
            System.out.println("\nCannot read from file.");
        } catch (ClassNotFoundException ex) {
            System.out.println("\nClass not found.");
        } finally{
            return adminList;
        } 
    }
}

