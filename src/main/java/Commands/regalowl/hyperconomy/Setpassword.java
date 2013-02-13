package regalowl.hyperconomy;



import org.bukkit.entity.Player;

public class Setpassword {
	Setpassword(String args[], Player player) {		
		HyperConomy hc = HyperConomy.hc;
		Calculation calc = hc.getCalculation();
		LanguageFile L = hc.getLanguageFile();
		DataHandler dh = hc.getDataFunctions();
		
		if (args.length == 1 && player != null) {
			HyperPlayer hp = dh.getHyperPlayer(player);
			String salt = calc.generateSecureSalt();
			hp.setSalt(salt);
			String hash = calc.sha256Digest(args[0] + salt);
			hp.setHash(hash);
			player.sendMessage(L.get("SETPASSWORD_SUCCESS"));
		} else {
			player.sendMessage(L.get("SETPASSWORD_INVALID"));
		}

	}
}
