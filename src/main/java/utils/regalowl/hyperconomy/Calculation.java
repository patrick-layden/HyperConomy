package regalowl.hyperconomy;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;


public class Calculation {
	private HyperConomy hc;

	Calculation() {
		hc = HyperConomy.hc;

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
