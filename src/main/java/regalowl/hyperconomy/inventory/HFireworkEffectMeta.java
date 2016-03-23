package regalowl.hyperconomy.inventory;

import java.util.ArrayList;
import java.util.HashMap;

import regalowl.simpledatalib.CommonFunctions;

 

public class HFireworkEffectMeta extends HItemMeta {

	private HFireworkEffect effect;

	
	public HFireworkEffectMeta(String displayName, ArrayList<String> lore, ArrayList<HEnchantment> enchantments, ArrayList<HItemFlag> itemFlags, HFireworkEffect effect) {
		super(displayName, lore, enchantments, itemFlags);
		this.effect = effect;
	}
	

	public HFireworkEffectMeta(String serialized) {
		super(serialized);
		HashMap<String,String> data = CommonFunctions.explodeMap(serialized);
		String serializedFireworkEffect = data.get("effect");
		if (serializedFireworkEffect != null) effect = new HFireworkEffect(serializedFireworkEffect);
    }

	@Override
	public String serialize() {
		HashMap<String,String> data = super.getMap();
		if (effect != null) data.put("effect", effect.serialize());
		return CommonFunctions.implodeMap(data);
	}

	@Override
	public HItemMetaType getType() {
		return HItemMetaType.FIREWORK_EFFECT;
	}
	
	public HFireworkEffect getEffect() {
		return effect;
	}
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((effect == null) ? 0 : effect.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HFireworkEffectMeta other = (HFireworkEffectMeta) obj;
		if (effect == null) {
			if (other.effect != null)
				return false;
		} else if (!effect.equals(other.effect))
			return false;
		return true;
	}
}