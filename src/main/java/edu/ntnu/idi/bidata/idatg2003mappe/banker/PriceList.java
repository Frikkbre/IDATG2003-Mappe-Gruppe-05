package edu.ntnu.idi.bidata.idatg2003mappe.banker;

import java.util.HashMap;

/**
 * This class is responsible for holding the prices for different services.
 * For example flipping a marker or using a plane route.
 *
 * @author Simen Gudbrandsen and Frikk Breadsroed
 * @version 0.0.1
 * @since 07.02.2025
 */
public class PriceList {
  HashMap<String, Integer> priceRegister = new HashMap();  //TODO - Is this class overkill? refactor to Banker class?


  public PriceList() {
    priceRegister.put("RedGem", 1000); //TODO - Should this be hardcoded or take inn <obj, int>
    priceRegister.put("GreenGem", 4000); //Originally double the price at certain places.
    priceRegister.put("YellowGem", 2000);
  }


}
