package regalowl.hyperconomy;




import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;



public class Hctest implements CommandExecutor {
	/*
	private HyperConomy hc;
	private EconomyManager em;
	private HyperEconomy de;
	private Player player;
	private Inventory inv;
	private FileConfiguration config;
	private HyperPlayer hp;
*/
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return true;
		/*
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy em = hc.getEconomyManager().getEconomy("default");
		FileConfiguration composites = hc.gYH().gFC("composites");
		Iterator<String> it = composites.getKeys(false).iterator();
		while (it.hasNext()) {
			String oldName = it.next();
			HyperItem ho = em.getHyperItem(oldName);
			if (ho != null) {
				composites.set(ho.getName() + ".information.type", composites.getString(oldName + ".information.type"));
				composites.set(ho.getName() + ".information.material", composites.getString(oldName + ".information.material"));
				composites.set(ho.getName() + ".information.data", composites.getInt(oldName + ".information.data"));
				composites.set(ho.getName() + ".name.display", ho.getDisplayName());
				composites.set(ho.getName() + ".name.aliases", ho.getAliasesString());
				composites.set(ho.getName() + ".components", composites.getString(oldName + ".components"));
				composites.set(oldName, null);
			}
		}
		hc.gYH().saveYamls();
		return true;
		*/
		/*
		HyperConomy hc = HyperConomy.hc;
		for (HyperObject ho:hc.getEconomyManager().getHyperObjects()) {
			ho.setCeiling(ho.getCeiling());
			ho.setEconomy(ho.getEconomy());
			ho.setFloor(ho.getFloor());
			ho.setInitiation(ho.getInitiation());
			ho.setIsstatic(ho.getIsstatic());
			ho.setMaxstock(ho.getMaxstock());
			ho.setMedian(ho.getMedian());
			ho.setName(ho.getName());
			ho.setStartprice(ho.getStartprice());
			ho.setStaticprice(ho.getStaticprice());
			ho.setStock(ho.getStock());
			ho.setType(ho.getType().toString());
			ho.setValue(ho.getValue());
		}
		return true;
		
		*/
		
		/*
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		config = hc.gYH().gFC("config");
		if (config == null) {
			sender.sendMessage("error1");
			return true;
		}
		player = null;
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);
			return true;
		}
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if (player == null) {
			sender.sendMessage("error2");
			return true;
		}
		hp = em.getHyperPlayer(player);
		if (hp == null) {
			sender.sendMessage("error3");
			return true;
		}
		de = em.getDefaultEconomy();
		if (de == null) {
			sender.sendMessage("error4");
			return true;
		}
		HyperItem emerald = de.getHyperItem("emerald");
		if (emerald == null) {
			sender.sendMessage("error5");
			return true;
		}
		
		inv = player.getInventory();
		
		inv.clear();
		emerald.add(10, inv);
		if (emerald.count(inv) != 10) {
			sender.sendMessage("error6");
			return true;
		}
		emerald.remove(10, inv);
		if (emerald.count(inv) != 0) {
			sender.sendMessage("error7");
			return true;
		}
		emerald.add(10, inv);
		
		em.addPlayer("testPlayer");
		HyperPlayer testPlayer = em.getHyperPlayer("testPlayer");
		if (testPlayer == null) {
			sender.sendMessage("error8");
			return true;
		}
		testPlayer.setBalance(10000);
		hp.setBalance(10000);
		
		
		

		emerald.setStartprice(512);
		emerald.setMedian(15000);
		emerald.setStaticprice(512);
		emerald.setValue(256);
		emerald.setStock(0);
		emerald.setIsstatic("false");
		emerald.setInitiation("true");
		config.set("config.purchasetaxpercent", 0);
		config.set("config.sales-tax-percent", 0);
		config.set("config.statictaxpercent", 0);
		config.set("config.initialpurchasetaxpercent", 0);
		config.set("config.enchanttaxpercent", 0);
		
		
		
		PlayerTransaction trans = new PlayerTransaction(TransactionType.SELL);
		trans.setHyperObject(emerald);
		trans.setAmount(10);
		trans.setTradePartner(testPlayer);
		TransactionResponse response = hp.processTransaction(trans);
		if (response.getMessages() == null) {
			sender.sendMessage("error9");
			return true;
		}
		if (!response.successful()) {
			sender.sendMessage("error10");
			return true;
		}
		if (hp.getBalance() != (10000 + 5120)) {
			sender.sendMessage("error11");
			return true;
		}
		if (testPlayer.getBalance() != (10000 - 5120)) {
			sender.sendMessage("error12");
			return true;
		}
		if (emerald.count(inv) != 0) {
			sender.sendMessage("error13");
			return true;
		}

		//basic buy/sell
		trans = new PlayerTransaction(TransactionType.BUY);
		trans.setHyperObject(emerald);
		trans.setAmount(10);
		trans.setTradePartner(testPlayer);
		response = null;
		response = hp.processTransaction(trans);
		if (response.getMessages() == null) {
			sender.sendMessage("error14");
			return true;
		}
		if (!response.successful()) {
			sender.sendMessage("error15");
			return true;
		}
		if (hp.getBalance() != (10000)) {
			sender.sendMessage("error16");
			return true;
		}
		if (testPlayer.getBalance() != (10000)) {
			sender.sendMessage("error17");
			return true;
		}
		if (emerald.count(inv) != 10) {
			sender.sendMessage("error18");
			return true;
		}

		
		
		
		//basic xp buy/sell
		HyperXP xp = de.getHyperXP();
		xp.setStartprice(512);
		xp.setMedian(15000);
		xp.setStaticprice(512);
		xp.setValue(256);
		xp.setStock(0);
		xp.setIsstatic("false");
		xp.setInitiation("true");
		
		player.setLevel(0);
		player.setExp(0);
		xp.addXp(player, 1000);
		if (player.getLevel() != 22) {
			sender.sendMessage("error19");
			return true;
		}
		
		trans = new PlayerTransaction(TransactionType.SELL);
		trans.setHyperObject(xp);
		trans.setAmount(10);
		trans.setTradePartner(testPlayer);
		response = hp.processTransaction(trans);
		if (response.getMessages() == null) {
			sender.sendMessage("error20");
			return true;
		}
		if (!response.successful()) {
			sender.sendMessage("error21");
			return true;
		}
		if (hp.getBalance() != (10000 + 5120)) {
			sender.sendMessage("error22");
			return true;
		}
		if (testPlayer.getBalance() != (10000 - 5120)) {
			sender.sendMessage("error23");
			return true;
		}
		if (xp.getTotalXpPoints(player) != 990) {
			sender.sendMessage("error24");
			return true;
		}

		
		trans = new PlayerTransaction(TransactionType.BUY);
		trans.setHyperObject(xp);
		trans.setAmount(10);
		trans.setTradePartner(testPlayer);
		response = null;
		response = hp.processTransaction(trans);
		if (response.getMessages() == null) {
			sender.sendMessage("error25");
			return true;
		}
		if (!response.successful()) {
			sender.sendMessage("error26");
			return true;
		}
		if (hp.getBalance() != (10000)) {
			sender.sendMessage("error27");
			return true;
		}
		if (testPlayer.getBalance() != (10000)) {
			sender.sendMessage("error28");
			return true;
		}
		if (xp.getTotalXpPoints(player) != 1000) {
			sender.sendMessage("error29");
			return true;
		}
		
		HyperItem goldsword = de.getHyperItem("goldsword");
		HyperEnchant smite2 = de.getHyperEnchant("smite2");
		//player.getInventory().setItem(0, );
		//player.setItemOnCursor(arg0);
		
		
		
		
		
		sender.sendMessage("Test successful.");
		return true;
		
		*/
	}
	
	
	
	
	/*
	
	Hctest() {
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy em = hc.getEconomyManager().getEconomy("default");
		FileConfiguration composites = hc.gYH().gFC("composites");
		ArrayList<String> names = em.getItemNames();
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			composites.set(name, null);
			HyperObject ho = em.getHyperObject(name);
			String newtype = HyperObjectType.getString(ho.getType());
			String newcategory = ho.getCategory();
			String newmaterial = ho.getMaterial();
			int newid = ho.getId();
			int newdata = ho.getData();
			composites.set(name + ".information.type", newtype);
			composites.set(name + ".information.category", newcategory);
			composites.set(name + ".information.material", newmaterial);
			composites.set(name + ".information.id", newid);
			composites.set(name + ".information.data", newdata);
			String componentString = "";
			if (name.contains("potion")) {
				boolean modified = false;
				if (name.contains("spotion")) {
					componentString += "gunpowder,1;";
					modified = true;
				}
				if (name.contains("2")) {
					componentString += "glowstonedust,1;";
					modified = true;
				}
				if (name.contains("ext")) {
					componentString += "redstone,1;";
					modified = true;
				}
				if (modified) {
					String basePotion = name.replace("spotion", "potion").replace("2", "").replace("ext", "");
					componentString += basePotion+",1;";
				}
			} else {
				componentString = ";";
			}
			composites.set(name + ".components", componentString);
		}
		hc.gYH().saveYamls();
	}
	
	
	
	
	
	Hctest(String args[], CommandSender sender) {
		/*
		HyperConomy hc = HyperConomy.hc;
		for (HyperObject ho:hc.getEconomyManager().getHyperObjects()) {
			ho.setCategory(ho.getCategory());
			ho.setCeiling(ho.getCeiling());
			ho.setData(ho.getData());
			ho.setDurability(ho.getDurability());
			ho.setEconomy(ho.getEconomy());
			ho.setFloor(ho.getFloor());
			ho.setId(ho.getId());
			ho.setInitiation(ho.getInitiation());
			ho.setIsstatic(ho.getIsstatic());
			ho.setMaterial(ho.getMaterial());
			ho.setMaxstock(ho.getMaxstock());
			ho.setMedian(ho.getMedian());
			ho.setName(ho.getName());
			ho.setStartprice(ho.getStartprice());
			ho.setStaticprice(ho.getStaticprice());
			ho.setStock(ho.getStock());
			ho.setType(ho.getType().toString());
			ho.setValue(ho.getValue());
		}
		*/
		
		
		
		
		
		
		/*
		HyperConomy hc = HyperConomy.hc;
		HyperEconomy em = hc.getEconomyManager().getEconomy("default");
		FileConfiguration composites = hc.gYH().gFC("composites");
		ArrayList<String> names = em.getItemNames();
		Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
		for (int i = 0; i < names.size(); i++) {
			String name = names.get(i);
			composites.set(name, null);
			HyperObject ho = em.getHyperObject(name);
			String newtype = HyperObjectType.getString(ho.getType());
			String newcategory = ho.getCategory();
			String newmaterial = ho.getMaterial();
			int newid = ho.getId();
			int newdata = ho.getData();
			composites.set(name + ".information.type", newtype);
			composites.set(name + ".information.category", newcategory);
			composites.set(name + ".information.material", newmaterial);
			composites.set(name + ".information.id", newid);
			composites.set(name + ".information.data", newdata);
			String componentString = "";
			if (name.contains("potion")) {
				boolean modified = false;
				if (name.contains("spotion")) {
					componentString += "gunpowder,1;";
					modified = true;
				}
				if (name.contains("2")) {
					componentString += "glowstonedust,1;";
					modified = true;
				}
				if (name.contains("ext")) {
					componentString += "redstone,1;";
					modified = true;
				}
				if (modified) {
					String basePotion = name.replace("spotion", "potion").replace("2", "").replace("ext", "");
					componentString += basePotion+",1;";
				}
			} else {
				componentString = ";";
			}
			composites.set(name + ".components", componentString);
		}
		hc.gYH().saveYamls();
		
		
		
		
	}
	*/
}
