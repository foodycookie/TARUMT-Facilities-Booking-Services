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
            
    public void saveToFile(SortedArrayList<Facility> facilityList) {
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
    
    public SortedArrayList<Facility> retrieveFromFile() {
        File file = new File(fileName);
        
        SortedArrayList<Facility> facilityList = new SortedArrayList<>();
        
        try (ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file))) {
            facilityList = (SortedArrayList<Facility>) (oiStream.readObject());
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