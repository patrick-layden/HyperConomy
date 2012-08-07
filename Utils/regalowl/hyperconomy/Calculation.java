package regalowl.hyperconomy;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

/**
 * 
 * 
 * This class handles various calculations, such as how much a purchase or sale is worth.
 * 
 */
public class Calculation {
	

	private HyperConomy hc;
	
	/**
	 * 
	 * 
	 * Calculation Constructor.
	 * 
	 */
	Calculation() {
		hc = HyperConomy.hc;
	}
	
	/**
	 * 
	 * 
	 * This function determines the sale value for one or more of the item.
	 * 
	 */
	public double getValue(String name, int amount, Player p){
		SQLFunctions sf = hc.getSQLFunctions();
		String playerecon = sf.getPlayerEconomy(p.getName());
		
		try {
		
		//Stores the total value for the item(s).
		double totalvalue = 0;

		//Gets the items config file.
		
		int itemid = 0;
		//Gets the item id and initializes the damage variable;
		
		itemid = sf.getId(name, playerecon);
		double damage = 0;

		boolean isstatic = false;
		isstatic = Boolean.parseBoolean(sf.getStatic(name, playerecon));

		
		//Checks to make sure the price is not static.
		if (!isstatic) {
			
			//Gets the total damage of all items with the given id and divides it by the number of items.
			damage = getDamage(itemid, amount, p);
			//p.sendMessage("damage: " + damage);

			//Gets the item's data from the items yaml file.
			double shopstock = 0;
			double value = 0;
			double median = 0;
			double icost = 0;
			

			shopstock = sf.getStock(name, playerecon);
			value = sf.getValue(name, playerecon);
			median = sf.getMedian(name, playerecon);
			icost = sf.getStartPrice(name, playerecon);

			
			//Deactivates the initial pricing period if the initial price is greater than or equal to the hyperbolic price and if the shop has more than 0 items.
			if (icost >= ((median * value)/shopstock) && shopstock > 0) {
				sf.setInitiation(name, playerecon, "false");
			}
			
			//Sums the value of all desired items.
			int counter = 0;
			while (counter < amount) {

				//Calculates the price for each individual item and sums them up in totalvalue.
				double price = ((median * value)/shopstock);
				shopstock = shopstock + 1;
				totalvalue = totalvalue + price;
				counter++;
			}
			
			//Factors in the total damage of all the chosen items.
			totalvalue = totalvalue * damage;

			//Checks to see if initiation is active.  If it is it converts the totalvalue to the initial value.
			Boolean initial = false;

			initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));

			if (initial == true){
				totalvalue = icost * damage * amount;	
			}
		
			//Checks if the price is infinite, and if it is sets the cost to a specific value that can later be identified.
			if (totalvalue < Math.pow(10, 10)) {
				//Rounds to two decimal places if not infinite
				totalvalue = twoDecimals(totalvalue);
			} else {
				totalvalue = 3235624645000.7;
			}
			
		//If the item uses a static price, this calculates the correct value for it.
		} else {
			
			//Factors in the total damage.
			damage = getDamage(itemid, amount, p);
			
			//Gets the static price and multiplies it by the number of items.
			double statprice = 0;
			statprice = sf.getStaticPrice(name, playerecon);

			totalvalue = (statprice * amount) * damage;
		}
		
		//Returns the resulting value calculation.
		return totalvalue;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #16");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #16", "hyperconomy.error");
			double totalvalue = 0;
			return totalvalue;
		}
	}
	
	

	
	/**
	 * 
	 * 
	 * This function determines the purchase price for one or more of the item.
	 * 
	 */
	
	public double getCost(String name, int amount, String playerecon){
		SQLFunctions sf = hc.getSQLFunctions();
		try {
			sf = hc.getSQLFunctions();
			//Stores the total cost for the item(s).
			double cost = 0;

			
			//Checks to see if the price is static.
			boolean isstatic = Boolean.parseBoolean(sf.getStatic(name, playerecon));

			if (isstatic == false) {
				double shopstock = 0;
				double oshopstock = 0;
				double value = 0;
				double median = 0;
				shopstock = sf.getStock(name, playerecon);
				oshopstock = shopstock;
				value = sf.getValue(name, playerecon);
				median = sf.getMedian(name, playerecon);

				//Offsets the shop stock by one so that buying and selling will result in no money gained or lost.  (The purchase price is set to what the price would have been after the purchase.)
				shopstock = shopstock - 1;
			
				//Sums up the cost of all items.
				int counter = 0;
				while (counter < amount) {
					double price = ((median * value)/shopstock);			
					shopstock = shopstock - 1;
					cost = cost + price;
					counter++;
				}	
				
				//Checks to see if initiation is active.
				boolean initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));

				if (initial == true){
					
					//If initiation is active, gets the correct cost.
					double icost = 0;
					icost = sf.getStartPrice(name, playerecon);
		
					//If the shop has more than 0 items (which results in infinite values), it makes sure the real cost is less than the initial cost.  If not, it turns off initiation.
					if (cost < (icost * amount) && oshopstock > 0){
						sf.setInitiation(name, playerecon, "false");
					//If the initial cost is indeed more than the normal cost, the cost is recalculated to the correct initial cost. 
					} else {	
						//Gets the initial tax multiplier and calculates the cost.
						cost = icost * amount;
					} 
				}
		
				//Checks if the cost is infinite, and if it is sets the cost to a specific value that can later be identified.  If isn't infinite, it adds on the tax.
				if (cost < Math.pow(10, 10)) {
					//Rounds to two decimal places.
					cost = twoDecimals(cost);
				} else {
					cost = 3235624645000.7;
				}
			
			//If the item is using a static price, this calculates the correct cost, factoring in the static tax.
			} else {
				double staticcost = sf.getStaticPrice(name, playerecon);
				cost = staticcost * amount;
				cost = twoDecimals(cost);
			}
			
			//Returns the resulting cost calculation.
			return cost;
		
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #15");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #15", "hyperconomy.error");
			double cost = 99999999;
			return cost;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function calculates the value for the given enchantment.
	 * 
	 */
	public double getEnchantValue(String name, String mater, String playerecon){
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		ETransaction etran = hc.getETransaction();
		try {
			double cost = 0;
		    double classvalue = etran.getclassValue(mater);
		    boolean stax;
		    stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
			if (!stax) {
				double shopstock;
				double value;
				double median;
				double icost;
				shopstock = sf.getStock(name, playerecon);
				value = sf.getValue(name, playerecon);
				median = sf.getMedian(name, playerecon);
				icost = sf.getStartPrice(name, playerecon);
				if (icost >= ((median * value)/shopstock) && shopstock > 0) {
					sf.setInitiation(name, playerecon, "false");
				}
				double price = (median * value)/shopstock;
				shopstock = shopstock + 1;
				cost = cost + price;
				cost = cost * classvalue;
				Boolean initial;
				initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
				if (initial == true){	
						cost = icost * classvalue;		
				}
				if (cost < Math.pow(10, 10)) {
					cost = calc.twoDecimals(cost);
				} else {
					cost = 3235624645000.7;
				}
			} else {
				double statprice;
				statprice = sf.getStaticPrice(name, playerecon);
				cost = statprice * classvalue;
			}	
			return cost;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger l = Logger.getLogger("Minecraft");
	    	l.info("HyperConomy ERROR #19");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #19", "hyperconomy.error");
			double value = 0;
			return value;
		}
	}



	/**
	 * 
	 * 
	 * This function calculates the cost for the given enchantment.
	 * 
	 */
	public double getEnchantCost(String name, String mater, String playerecon) {
		SQLFunctions sf = hc.getSQLFunctions();
		Calculation calc = hc.getCalculation();
		ETransaction etran = hc.getETransaction();
		try {
			double cost = 0;
	        double classvalue = etran.getclassValue(mater);
			if (classvalue != 123456789) {
			    boolean stax;
			    stax = Boolean.parseBoolean(sf.getStatic(name, playerecon));
				if (!stax) {
					double shopstock;
					double value;
					double median;
					shopstock = sf.getStock(name, playerecon);
					value = sf.getValue(name, playerecon);
					median = sf.getMedian(name, playerecon);
					double oshopstock = shopstock;
					shopstock = shopstock - 1;
					double price = ((median * value)/shopstock);			
					cost = price * classvalue;
					boolean initial;
					initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));
					if (initial == true) {
						double icost;
						icost = sf.getStartPrice(name, playerecon);
						if (price < icost && oshopstock > 0){
							sf.setInitiation(name, playerecon, "false");
						} else {
							cost = icost * classvalue;
						} 
					}
					if (cost < Math.pow(10, 10)) {
						cost = calc.twoDecimals(cost);
					} else {
						cost = 3235624645000.7;
					}
				} else {
					double staticcost;
					staticcost = sf.getStaticPrice(name, playerecon);
					cost =  staticcost * classvalue;
				}		
			} else {
				cost = 123456789;
			}	
			return cost;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger l = Logger.getLogger("Minecraft");
	    	l.info("HyperConomy ERROR #20");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #20", "hyperconomy.error");
			double cost = 99999999;
			return cost;
		}
	}
	
	
	
	
	
	
	
	
	
	
	


	
	
	/**
	 * 
	 * 
	 * This function calculates the total damage multiplier for a cost/value calculation.
	 * 
	 */
	private double getDamage(int itemid, int amount, Player p){
		ETransaction ench = hc.getETransaction();
		try {
			//Holds the total damage for the specified item.
			double damage = 0;
			
			//Makes sure that the item can be damaged.
			if (testId(itemid)) {
				
				//Puts all of the specified item in the player's inventory into a HashMap.
				Inventory pinv = p.getInventory();
				HashMap <Integer,?extends ItemStack> stacks = pinv.all(itemid);
				
				//Gets the slot number of the inventory item being held by the player.
				int heldslot = p.getInventory().getHeldItemSlot();
	
				//Checks to see if the item in the player's hand is enchanted.
				boolean hasenchants2 = ench.hasenchants(stacks.get(heldslot));
				
				//Stores how many items have been processed.  (An item stack can only have a quantity of 1 if it has durability.)
				int totalitems = 0;
				
				//Checks if the item a player is holding is the item being sold or bought and makes sure it is not enchanted.
				if (p.getItemInHand().getTypeId() == itemid && hasenchants2 == false) {
					
					//If the player is holding an eligible item for the transaction, it calculates the damage percent for the item and adds one item to the total number processed.
					//This item is processed first because transactions favor the item in a player's hand over other items.
					damage = getdurabilityPercent(stacks.get(heldslot));
					totalitems++;
				}
				
				//Goes through each slot in a player's inventory and adds up the damage percentages until the total number of items processed equals the amount of items in the transaction.
				int slot = 0;
				while (slot < 36) {
	
		    			//Checks to see if each item is enchanted.
		    			boolean hasenchants = ench.hasenchants(stacks.get(slot));
		    			
		    			//Makes sure the slot has an item, the item is not enchanted, the slot is not the slot the player is holding (as it was already processed), and that the total number of
		    			//items in the transaction has not yet been reached.
						if (stacks.get(slot) != null && totalitems < amount && slot != heldslot && hasenchants == false) {
							
							//Calculates the damage percent for each item and sums them all up in damage.
							damage = getdurabilityPercent(stacks.get(slot)) + damage;
							totalitems++;
						}
					slot++;
				}
					
				//Divides the damage percentage by the number of items in the tranaction. This allows the damage multiplier to treat the entire transaction as one item with a certain
				//durability.
				damage = damage/amount;	
	
			//If an item cannot be damaged, it returns 1.  (1 will have no effect on the cost/value calculation.)
			} else {
				damage = 1;
			}
			
			//Returns the calculated damage multiplier.
			return damage;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #14");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #14", "hyperconomy.error");
			double damage = 0;
			return damage;
		}
	}
	
	
	
	

	/**
	 * 
	 * 
	 * This function returns the percentage of remaining durability in an itemstack. (0 = 0% durability, 1 = 100%)  It returns as 1 if the item cannot be damaged.
	 * 
	 */
	public double getdurabilityPercent(ItemStack i){
		try {

			double durabilitypercent = 1;
			
			//Attempts to get the durability.  If an error is thrown this means that the item is not durable or something went wrong, and as a result of this it returns a durability of 1.
			try {	
				double cdurability = i.getDurability();
				double maxdurability = i.getData().getItemType().getMaxDurability();
				durabilitypercent = (1 - (cdurability/maxdurability));
			} catch (Exception e) {
				durabilitypercent = 1;
			}
			
			//Makes sure that the durability percent is not negative.
			if (durabilitypercent < 0) {
				durabilitypercent = 1;
			}
			return durabilitypercent;	
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #13");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #13", "hyperconomy.error");
			double durabilitypercent = 1;
			return durabilitypercent;
		}
	}
	
	
	
	/**
	 * 
	 * 
	 * This function uses the testId function to determine if the item can be damaged.  If it can be damaged it sets the damage value to 0 (to represent an undamaged item).  If the item
	 * cannot be damaged it returns the original damage value.
	 */
	public int newData(int id, int data) {
		try {
			int newData;
			if (testId(id)) {
				newData = 0;
			} else {
				newData = data;
			}	
			return newData;
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #12");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #12", "hyperconomy.error");
			int newData = data;
			return newData;
		}
	}
	

	
	
	/**
	 * 
	 * 
	 * This function compares the given id with its list to determine if the item can be damaged.  If it can be damaged it returns true, false if not.
	 * 
	 */
	public boolean testId(int id) {
		
		try {		
			boolean datatest = false;
			int durableIds[] = new int[49];
			durableIds[0] = 268;
			durableIds[1] = 272;
			durableIds[2] = 267;
			durableIds[3] = 283;
			durableIds[4] = 276;		
			durableIds[5] = 290;
			durableIds[6] = 291;
			durableIds[7] = 292;
			durableIds[8] = 294;
			durableIds[9] = 293;		
			durableIds[10] = 270;
			durableIds[11] = 274;
			durableIds[12] = 257;
			durableIds[13] = 285;
			durableIds[14] = 278;		
			durableIds[15] = 269;
			durableIds[16] = 273;
			durableIds[17] = 256;
			durableIds[18] = 284;
			durableIds[19] = 277;		
			durableIds[20] = 271;
			durableIds[21] = 275;
			durableIds[22] = 258;
			durableIds[23] = 286;
			durableIds[24] = 279;
			durableIds[25] = 298;
			durableIds[26] = 299;
			durableIds[27] = 300;
			durableIds[28] = 301;
			durableIds[29] = 302;
			durableIds[30] = 303;
			durableIds[31] = 304;
			durableIds[32] = 305;
			durableIds[33] = 306;
			durableIds[34] = 307;
			durableIds[35] = 308;
			durableIds[36] = 309;
			durableIds[37] = 310;
			durableIds[38] = 311;
			durableIds[39] = 312;
			durableIds[40] = 313;
			durableIds[41] = 314;
			durableIds[42] = 315;
			durableIds[43] = 316;
			durableIds[44] = 317;		
			durableIds[45] = 261;
			durableIds[46] = 346;
			durableIds[47] = 359;
			durableIds[48] = 259;		
			int l = durableIds.length;
			int count = 0;	
			while (l > count) {
				if (id == durableIds[count]) {
					datatest = true;
				}
				count++;
			}		
			return datatest;
		
		} catch (Exception e) {
			e.printStackTrace();
	    	Logger log = Logger.getLogger("Minecraft");
	    	log.info("HyperConomy ERROR #11");
			Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #11", "hyperconomy.error");
			boolean datatest = false;
			return datatest;
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * 
	 * 
	 * This function calculates the theoretical value for items, ignoring durability.
	 * 
	 */
		public double getTvalue(String name, int amount, String playerecon){
			SQLFunctions sf = hc.getSQLFunctions();
			try {
				
				double cost = 0;
				int counter = 0;


	
				//Determines whether initial prices will be used.
				Boolean initial = false;
				initial = Boolean.parseBoolean(sf.getInitiation(name, playerecon));

				
				
				//Checks if the price is set to static.
				boolean isstatic = false;

				isstatic = Boolean.parseBoolean(sf.getStatic(name, playerecon));

				if (!isstatic) {
					
					//Gets item data from the items yml file.
					double shopstock = 0;
					double value = 0;
					double median = 0;
					double icost = 0;

					shopstock = sf.getStock(name, playerecon);
					value = sf.getValue(name, playerecon);
					median = sf.getMedian(name, playerecon);
					icost = sf.getStartPrice(name, playerecon);


					
					//Sums the cost in a loop.
					while (counter < amount) {
						
						//Calculates the price for each individual item and sums them up in the cost variable.
						double price = ((median * value)/shopstock);
						shopstock = shopstock + 1;
						cost = cost + price;
						counter++;
					}
	
	
					if (initial == true){
							cost = icost * amount;	
					}
				
					//Checks if the price is infinite, and if it is sets the cost to a specific value that can later be used.
					if (cost < Math.pow(10, 10)) {
						//Rounds to two decimal places if not infinite
						cost = twoDecimals(cost);
					} else {
						cost = 3235624645000.7;
					}
	
				} else {
					double statprice = 0;

					statprice = sf.getStaticPrice(name, playerecon);

					
					cost = statprice * amount;
				}
			
				return cost;
			
			//If there is some error this makes it easier to identify.
			} catch (Exception e) {				
				e.printStackTrace();
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info("HyperConomy ERROR #10");
				Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #10", "hyperconomy.error");
				double cost = 99999999;
				return cost;			
			}
		}
	
	

	
		/**
		 * 
		 * 
		 * This function returns the correct damage value for potions.
		 * 
		 */
		public int getpotionDV(ItemStack item) {
			try {
				int da;
				if (item != null) {
				if (item.getTypeId() == 373) {
					try {
						Potion p = Potion.fromItemStack(item);
						da = p.toDamageValue();		
					} catch (Exception IllegalArgumentException) {
						da = item.getData().getData();
					}
				} else {
					da = item.getData().getData();
				}
				} else {
					da = 0;
				}
				return da;
			} catch (Exception e) {
				e.printStackTrace();
		    	Logger log = Logger.getLogger("Minecraft");
		    	log.info("HyperConomy ERROR #9");
		    	Bukkit.broadcast(ChatColor.DARK_RED + "HyperConomy ERROR #9", "hyperconomy.error");
				int da = 0;
				return da;
			}
		}
		
		//Gets how many xp points are currently in the players xp bar.
		public int getbarxpPoints(Player player) {
			int lvl = player.getLevel();
			int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) * player.getExp() + .5);
			return exppoints;
		}
		
		//Gets how much xp is required to go from the current lvl with an empty xp bar to the next lvl.
		public int getxpfornextLvl(int lvl) {
			//int lvl = player.getLevel();
			int exppoints = (int) Math.floor(((3.5 * lvl) + 6.7) + .5);
			return exppoints;
		}
		
		//Gets the xp points at the minimum of the specified lvl.
		public int getlvlxpPoints(int lvl) {
			//int lvl = player.getLevel();
			int exppoints = (int) Math.floor((1.75 * Math.pow(lvl, 2)) + (5 * lvl) + .5);
			return exppoints;
		}
		
		//Gets how many xp points a player has total.
		public int gettotalxpPoints(Player player) {
			int lvl = player.getLevel();
			int lvlxp = getlvlxpPoints(lvl);
			int barxp = getbarxpPoints(player);
			int totalxp = lvlxp + barxp;
			return totalxp;
		}
		
		//Gets a players lvl from their total exp.
		public int getlvlfromXP(int exp) {
			double lvlraw = (Math.sqrt((exp * 7.0) + 25.0) - 5.0) * (2.0/7.0);
			int lvl = (int) Math.floor(lvlraw);
			//Incase math.floor fails.
			if ((double)lvl > lvlraw) {
				lvl = lvl - 1;
			}
			return lvl;
		}


		//Rounds to two decimal places.
		public double twoDecimals(double input) {
			int nodecimals = (int) Math.ceil((input * 100) - .5);
			double twodecimals = (double)nodecimals/100.0;
			return twodecimals;
		}
		
		//Rounds to tree decimal places.
		public double threeDecimals(double input) {
			int nodecimals = (int) Math.ceil((input * 1000) - .5);
			double threedecimals = (double)nodecimals/1000.0;
			return threedecimals;
		}
		
		//Gets an item's true damage value, restoring damaged item's damage value to its original value and getting the correct damage value for potions.
		
		public int getdamageValue(ItemStack item) {
			int itd = item.getTypeId();
			int da = getpotionDV(item);
			int newdat = newData(itd, da);
			return newdat;
		}
		 
		
		public double getSalesTax(Player p, Double fprice) {
			Account acc = hc.getAccount();
			double salestax = 0;
			if (hc.getYaml().getConfig().getBoolean("config.dynamic-tax.use-dynamic-tax")) {
				if (p == null) {
					return 0.0;
				}
				double moneyfloor = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-floor");
				double moneycap = hc.getYaml().getConfig().getDouble("config.dynamic-tax.money-cap");
				double cbal = acc.getBalance(p.getName());
				if (cbal >= moneycap) {
					salestax = fprice * (hc.getYaml().getConfig().getDouble("config.dynamic-tax.max-tax-percent")/100);
				} else if (cbal <= moneyfloor) {
					salestax = 0;
				} else {
					salestax = fprice * ((cbal - moneyfloor)/(moneycap - moneyfloor));
				}
			} else {
				double salestaxpercent = hc.getYaml().getConfig().getDouble("config.sales-tax-percent");
				salestax = (salestaxpercent/100) * fprice;
			}
			return salestax;
		}
		
		
		public double getPurchaseTax(String name, String economy, double cost) {
			SQLFunctions sf = hc.getSQLFunctions();
			boolean isinitial = Boolean.parseBoolean(sf.getInitiation(name, economy));
			boolean isstatic = Boolean.parseBoolean(sf.getStatic(name, economy));
			double tax = 0.0;
			if (isstatic) {
				tax = hc.getYaml().getConfig().getDouble("config.statictaxpercent")/100.0;
			} else if (isinitial) {
				tax = hc.getYaml().getConfig().getDouble("config.initialpurchasetaxpercent")/100.0;
			} else {
				tax = hc.getYaml().getConfig().getDouble("config.purchasetaxpercent")/100.0;
			}
			return twoDecimals(cost * tax);
		}
		
		public double getEnchantTax(String name, String economy, double cost) {
			SQLFunctions sf = hc.getSQLFunctions();
			boolean isstatic = Boolean.parseBoolean(sf.getStatic(name, economy));
			double tax = 0.0;
			if (isstatic) {
				tax = hc.getYaml().getConfig().getDouble("config.statictaxpercent")/100.0;
			} else {
				tax = hc.getYaml().getConfig().getDouble("config.enchanttaxpercent")/100.0;
			}
			return twoDecimals(cost * tax);
		}
	
}
