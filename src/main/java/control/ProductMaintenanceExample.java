package control;

import adt.*;
import boundary.ProductMaintenanceUIExample;
import dao.ProductDAOExample;
import entity.*;
import utility.MessageUIExample;

/**
 *
 * @author Kat Tan
 */
public class ProductMaintenanceExample {
//
//  private SortedListInterface<ProductExample> productList = new ArrayList<>();
//  private ProductDAOExample productDAO = new ProductDAOExample();
//  private ProductMaintenanceUIExample productUI = new ProductMaintenanceUIExample();
//
//  public ProductMaintenanceExample() {
//    productList = productDAO.retrieveFromFile();
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
