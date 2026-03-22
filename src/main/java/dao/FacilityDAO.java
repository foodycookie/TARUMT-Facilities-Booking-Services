package dao;

import adt.*;
import entity.Facility;
import java.io.*;

/**
 * @author Kat Tan
 * @author Ong Hao Howard (modified)
 */

public class FacilityDAO {
    private String fileName;
            
    public void saveToFile(SortedLinkedList<Facility> facilityList) {
        File file = new File(fileName);
        
        try (ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file))) {
            ooStream.writeObject(facilityList);
            ooStream.close();
        } catch (FileNotFoundException ex) {
            System.out.println("\nFile not found");
        } catch (IOException ex) {
            System.out.println("\nCannot save to file");
        }
    }
    
    public SortedLinkedList<Facility> retrieveFromFile() {
        File file = new File(fileName);
        
        SortedLinkedList<Facility> facilityList = new SortedLinkedList<>();
        
        try (ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file))) {
            facilityList = (SortedLinkedList<Facility>) (oiStream.readObject());
            oiStream.close();
        } catch (FileNotFoundException ex) {
            System.out.println("\nNo such file");
        } catch (IOException ex) {
            System.out.println("\nCannot read from file");
        } catch (ClassNotFoundException ex) {
            System.out.println("\nClass not found");
        } finally {
            return facilityList;
        }
    }
}