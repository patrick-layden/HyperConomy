package regalowl.hyperconomy.inventory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import regalowl.hyperconomy.HC;

public class HColor extends SerializableObject implements Serializable {
	
	private static final long serialVersionUID = 1194773802989404854L;

	private int red;
	private int green;
	private int blue;
 
	public HColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public HColor(String base64String) {
    	try {
			byte[] data = Base64Coder.decode(base64String);
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
			Object o = ois.readObject();
			ois.close();
			if (!(o instanceof HColor)) {return;}
			HColor sc = (HColor)o;
	        this.red = sc.getRed();
	        this.green = sc.getGreen();
	        this.blue = sc.getBlue();
    	} catch (Exception e) {
    		HC.hc.getDataBukkit().writeError(e);
    	}
    }
	

	public int getRed() {
		return red;
	}
	public int getGreen() {
		return red;
	}
	public int getBlue() {
		return red;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blue;
		result = prime * result + green;
		result = prime * result + red;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HColor other = (HColor) obj;
		if (blue != other.blue)
			return false;
		if (green != other.green)
			return false;
		if (red != other.red)
			return false;
		return true;
	}
}
