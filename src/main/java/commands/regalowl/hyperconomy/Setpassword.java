package regalowl.hyperconomy;



import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.bukkit.entity.Player;

public class Setpassword {
	Setpassword(String args[], Player player) {		
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();

		if (args.length == 1 && player != null) {
			HyperPlayer hp = hc.getEconomyManager().getHyperPlayer(player.getName());
			String salt = generateSecureSalt();
			hp.setSalt(salt);
			String hash = sha256Digest(args[0] + salt);
			hp.setHash(hash);
			player.sendMessage(L.get("SETPASSWORD_SUCCESS"));
		} else {
			player.sendMessage(L.get("SETPASSWORD_INVALID"));
		}

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
