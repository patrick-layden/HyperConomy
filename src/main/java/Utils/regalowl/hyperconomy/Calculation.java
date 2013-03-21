package regalowl.hyperconomy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

/**
 * 
 * 
 * This class handles various calculations, such as how much a purchase or sale
 * is worth.
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
	 * This function uses the testId function to determine if the item can be
	 * damaged. If it can be damaged it sets the damage value to 0 (to represent
	 * an undamaged item). If the item cannot be damaged it returns the original
	 * damage value.
	 */
	public int newData(int id, int data) {
		if (Material.getMaterial(id).getMaxDurability() > 0) {
			return 0;
		} else {
			return data;
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
			String info = "Calculation getpotionDV() passed values ItemStack='" + item + "'";
			new HyperError(e, info);
			int da = 0;
			return da;
		}
	}



	public double round(double input, int decimals) {
		double factor = Math.pow(10, decimals);
		int changedecimals = (int) Math.ceil((input * factor) - .5);
		return (double) changedecimals / factor;
	}

	public double twoDecimals(double input) {
		int nodecimals = (int) Math.ceil((input * 100) - .5);
		double twodecimals = (double) nodecimals / 100.0;
		return twodecimals;
	}

	public int getDamageValue(ItemStack item) {
		try {
			if (item == null) {
				return 0;
			}
			int itd = item.getTypeId();
			int da = getpotionDV(item);
			int newdat = newData(itd, da);
			return newdat;
		} catch (Exception e) {
			String info = "Calculation getDamageValue() passed values ItemStack='" + item.getType() + "'";
			new HyperError(e, info);
			int da = 0;
			return da;
		}
	}







	public String formatMoney(double money) {
		LanguageFile L = hc.getLanguageFile();
		BigDecimal bd = new BigDecimal(money);
		BigDecimal rounded = bd.setScale(2, RoundingMode.HALF_DOWN);
		String currency = L.get("CURRENCY");
		if (currency.length() > 1 || currency == null) {
			currency = "";
		}
		return currency + rounded.toPlainString();
	}
	
	public String sha256Digest(String string) {
		try {
			return (new HexBinaryAdapter()).marshal(MessageDigest.getInstance("SHA-256").digest(string.getBytes()));
		} catch (Exception e) {
			return "";
		}
	}
	
	public String generateSecureSalt() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
}
