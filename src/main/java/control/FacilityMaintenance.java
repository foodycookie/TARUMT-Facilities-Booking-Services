package control;

import adt.*;
import boundary.FacilityMaintenanceUI;
import dao.FacilityDAO;
import entity.*;
import utility.MessageUIExample;

/**
 *
 * @author Ong Hao Howard
 */
public class FacilityMaintenance {

//  private SortedListInterface<Facility> facilityList = new ArrayList<>();
//  private FacilityDAO faciltiyDAO = new FacilityDAO();
//  private FacilityMaintenanceUI facilityUI = new FacilityMaintenanceUI();
//
//  public FacilityMaintenance() {
//    productList = facilityDAO.retrieveFromFile();
//  }
//  
//   public void runProductMaintenance() {
//    int choice = 0;
//    do {
//      choice = productUI.getMenuChoice();
//      switch(choice) {
//        case 0:
//          MessageUIExample.displayExitMessage();
//          break;
//        case 1:
//          addNewProduct();
//          productUI.listAllProducts(getAllProducts());
//          break;
//        case 2:
//          productUI.listAllProducts(getAllProducts());
//          break;
//        default:
//          MessageUIExample.displayInvalidChoiceMessage();
//      } 
//    } while (choice != 0);
//  }
//
//  public void addNewProduct() {
//    ProductExample newProduct = productUI.inputProductDetails();
//    productList.add(newProduct);
//    productDAO.saveToFile(productList);
//  }
//
//  public String getAllProducts() {
//    String outputStr = "";
//    for (int i = 1; i <= productList.getNumberOfEntries(); i++) {
//      outputStr += productList.getEntry(i) + "\n";
//    }
//    return outputStr;
//  }
//  
//  public void displayProducts() {
//    productUI.listAllProducts(getAllProducts());
//  }
//  
//  public static void main(String[] args) {
//    ProductMaintenanceExample productMaintenance = new ProductMaintenanceExample();
//    productMaintenance.runProductMaintenance();
//  }
}
