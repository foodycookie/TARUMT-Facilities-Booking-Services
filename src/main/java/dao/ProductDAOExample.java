package dao;

import adt.*;
import entity.ProductExample;
import java.io.*;

/**
 *
 * @author Kat Tan
 */
public class ProductDAOExample {
//  private String fileName = "products.dat"; // For security and maintainability, should not have filename hardcoded here.
//  
//  public void saveToFile(SortedListInterface<ProductExample> productList) {
//    File file = new File(fileName);
//    try {
//      ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file));
//      ooStream.writeObject(productList);
//      ooStream.close();
//    } catch (FileNotFoundException ex) {
//      System.out.println("\nFile not found");
//    } catch (IOException ex) {
//      System.out.println("\nCannot save to file");
//    }
//  }
//
//  public SortedListInterface<ProductExample> retrieveFromFile() {
//    File file = new File(fileName);
//    SortedListInterface<ProductExample> productList = new ArrayList<>();
//    try {
//      ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file));
//      productList = (ArrayList<ProductExample>) (oiStream.readObject());
//      oiStream.close();
//    } catch (FileNotFoundException ex) {
//      System.out.println("\nNo such file.");
//    } catch (IOException ex) {
//      System.out.println("\nCannot read from file.");
//    } catch (ClassNotFoundException ex) {
//      System.out.println("\nClass not found.");
//    } finally {
//      return productList;
//    }
//  }
}
