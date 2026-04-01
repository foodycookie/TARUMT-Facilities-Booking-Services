package dao;

import adt.*;
import entity.User;
import java.io.*;
/**
 *
 * @author TAY TIAN YOU
 */
    

public class UserDAO {
    private  String fileName;

    public UserDAO(String filename) {
        this.fileName = filename;
    }

    public void saveToFile(SortedArrayList<User> userList) {
        File file = new File(fileName);

        try (ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file))) {
            ooStream.writeObject(userList);
        } catch (FileNotFoundException ex) {
            System.out.println("\nFile not found.");
        } catch (IOException ex) {
            System.out.println("\nCannot save to file.");
        }
    }

    @SuppressWarnings("unchecked")
    public SortedArrayList<User> retrieveFromFile() {
        File file = new File(fileName);
        SortedArrayList<User> userList = new SortedArrayList<>();

        try (ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file))) {
            userList = (SortedArrayList<User>) oiStream.readObject();
        } catch (FileNotFoundException ex) {
            System.out.println("\nNo existing user file found. A new file will be created when data is saved.");
        } catch (IOException ex) {
            System.out.println("\nCannot read from file.");
        } catch (ClassNotFoundException ex) {
            System.out.println("\nClass not found.");
        } finally{
            return userList;
        } 
    }
}

