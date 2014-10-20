package regalowl.hyperconomy.command;



import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;


public class Setpassword extends BaseCommand implements HyperCommand {

	
	
	public Setpassword() {
		super(true);
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


	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		if (args.length == 1 ) {
			String salt = generateSecureSalt();
			hp.setSalt(salt);
			String hash = sha256Digest(args[0] + salt);
			hp.setHash(hash);
			data.addResponse(L.get("SETPASSWORD_SUCCESS"));
		} else {
			data.addResponse(L.get("SETPASSWORD_INVALID"));
		}
		return data;
	}
}
