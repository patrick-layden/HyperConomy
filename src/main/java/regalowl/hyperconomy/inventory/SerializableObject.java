package regalowl.hyperconomy.inventory;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;

public class SerializableObject implements Serializable {

	private static final long serialVersionUID = -8134508220996453330L;

	public String serialize() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			oos.close();
			return new String(Base64Coder.encode(baos.toByteArray()));
		} catch (Exception e) {
			HC.hc.getDataBukkit().writeError(e);
			return null;
		}
	}
	
}
