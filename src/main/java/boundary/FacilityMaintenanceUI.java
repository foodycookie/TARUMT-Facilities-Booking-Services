package boundary;

import entity.ProductExample;
import java.util.Scanner;

/**
 * @author Ong Hao Howard
 */

public class FacilityMaintenanceUI {
    Scanner scanner = new Scanner(System.in);
    
    public int getFacilityMaintenanceMenuChoice() {
        System.out.println("\n----- Facility Maintenance Menu -----");
        System.out.println("1. Add Facility");
        System.out.println("2. View All Facilities");
        System.out.println("3. Search Facility");
        System.out.println("4. Update Facility");
        System.out.println("5. Delete Facility");
        System.out.println("0. Back");
        System.out.print("Enter choice: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine();
        System.out.println();
        
        return choice; 
    }
    
    public void addFacility() {
        
    }
    
    public void viewAllFacilities() {
        
    }
    
    public void searchFacility() {
        
    }
    
    public void updateFacility() {
        
    }
    
    public void deleteFacility() {
        
    }
    
    
  public void listAllProducts(String outputStr) {
    System.out.println("\nList of Products:\n" + outputStr);
  }

  public void printProductDetails(ProductExample product) {
    System.out.println("Product Details");
    System.out.println("Product code:" + product.getNumber());
    System.out.println("Product name: " + product.getName());
    System.out.println("Quantity: " + product.getQuantity());
  }

  public String inputProductCode() {
    System.out.print("Enter product code: ");
    String code = scanner.nextLine();
    return code;
  }

  public String inputProductName() {
    System.out.print("Enter product name: ");
    String name = scanner.nextLine();
    return name;
  }

  public int inputQuantity() {
    System.out.print("Enter quantity: ");
    int quantity = scanner.nextInt();
    scanner.nextLine();
    return quantity;
  }

  public ProductExample inputProductDetails() {
    String productCode = inputProductCode();
    String productName = inputProductName();
    int quantity = inputQuantity();
    System.out.println();
    return new ProductExample(productCode, productName, quantity);
  }
}
