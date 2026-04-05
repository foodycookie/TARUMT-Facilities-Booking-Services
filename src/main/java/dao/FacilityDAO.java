package dao;

import adt.SortedArrayList;
import entity.Facility;
import java.io.*;

/**
 * @author Tiw Hong Xuan
 */

public class FacilityDAO {
    private final String fileName;

    public FacilityDAO(String fileName) {
        this.fileName = fileName;
    }

    public void saveToFile(SortedArrayList<Facility> facilityList) {
        File file = new File(fileName);

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(file))) {

            oos.writeObject(facilityList);

        } catch (FileNotFoundException ex) {
            System.out.println("\nFacility file not found: " + fileName);
        } catch (IOException ex) {
            System.out.println("\nCannot save facility data to file.");
        }
    }

    @SuppressWarnings("unchecked")
    public SortedArrayList<Facility> retrieveFromFile() {
        File file = new File(fileName);
        SortedArrayList<Facility> facilityList = new SortedArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {

            facilityList = (SortedArrayList<Facility>) ois.readObject();

        } catch (FileNotFoundException ex) {
            System.out.println("\nNo existing facility file found. "
                    + "A new file will be created when data is saved.");
        } catch (IOException ex) {
            System.out.println("\nCannot read facility data from file.");
        } catch (ClassNotFoundException ex) {
            System.out.println("\nFacility class not found during deserialisation.");
        }

        return facilityList;
    }
}