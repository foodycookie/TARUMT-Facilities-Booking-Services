package dao;

import adt.SortedArrayList;
import entity.Facility;
import java.io.*;

/**
 * Data Access Object for Facility persistence.
 *
 * Serialises and deserialises a SortedArrayList<Facility> to/from a binary
 * file using Java's ObjectOutputStream / ObjectInputStream, consistent with
 * UserDAO.
 *
 * The change of Facility.facilityId from int to String is transparent here
 * because Java serialisation handles the object graph automatically — no
 * logic changes were required in this class.
 *
 * Constructor requires the file path at construction time (same pattern as
 * UserDAO). The teammate's version had no constructor and would have thrown
 * a NullPointerException on every call; that bug is fixed here.
 *
 * @author (facility module)
 */
public class FacilityDAO {

    /** Path to the binary file that stores all facility records. */
    private final String fileName;

    // ------------------------------------------------------------------ //
    //  Constructor                                                          //
    // ------------------------------------------------------------------ //

    /**
     * Creates a FacilityDAO that reads from and writes to the given file.
     *
     * @param fileName relative or absolute path to the data file
     *                 (e.g. "data/facilities.dat")
     */
    public FacilityDAO(String fileName) {
        this.fileName = fileName;
    }

    // ------------------------------------------------------------------ //
    //  Persistence operations                                               //
    // ------------------------------------------------------------------ //

    /**
     * Serialises the entire facility list to disk.
     * Overwrites the file if it already exists.
     * Creates parent directories automatically on first run.
     *
     * @param facilityList the SortedArrayList to persist
     */
    public void saveToFile(SortedArrayList<Facility> facilityList) {
        File file = new File(fileName);

        // Ensure parent directories exist so the file can be created on first run
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

    /**
     * Deserialises the facility list from disk.
     * Returns an empty SortedArrayList if the file does not exist yet
     * (first-run scenario) or cannot be read.
     *
     * @return the persisted SortedArrayList<Facility>, or an empty list
     */
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