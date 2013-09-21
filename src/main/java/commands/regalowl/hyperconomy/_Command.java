package regalowl.hyperconomy;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class _Command {
	private Player player;
	private HyperConomy hc;
	private EconomyManager em;
	private String playerecon;
	private String nonPlayerEconomy;

	_Command() {
		nonPlayerEconomy = "default";
	}

	public boolean handleCommand(CommandSender sender, Command cmd, String label, String[] args) {
		hc = HyperConomy.hc;
		em = hc.getEconomyManager();
		player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			playerecon = nonPlayerEconomy;
		}
		if (player != null) {
			playerecon = em.getHyperPlayer(player.getName()).getEconomy();
		}
		if (cmd.getName().equalsIgnoreCase("buy") && (player != null)) {
			new Buy(args, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("sell") && (player != null)) {
			new Sell(args, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("sellall") && (player != null)) {
			new Sellall(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("value")) {
			new Value(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("heldbuy") && (player != null)) {
			new Hb(args, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("xpinfo") && (player != null)) {
			new Xpinfo(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("buyid") && (player != null)) {
			new Buyid(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("heldsell") && (player != null)) {
			new Hs(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("heldvalue") && (player != null)) {
			new Hv(args, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("settax")) {
			new Settax(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setclassvalue")) {
			new Setclassvalue(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setinterval")) {
			new Setinterval(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("classvalues")) {
			new Classvalues(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setvalue")) {
			new Setvalue(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setstock")) {
			new Setstock(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setstockall")) {
			new Setstockall(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setmedian")) {
			new Setmedian(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setstatic")) {
			new Setstatic(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setinitiation")) {
			new Setinitiation(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setinitiationall")) {
			new Setinitiationall(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setstaticall")) {
			new Setstaticall(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setstaticprice")) {
			new Setstaticprice(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setstartprice")) {
			new Setstartprice(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setceiling")) {
			new Setceiling(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setfloor")) {
			new Setfloor(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("writeitems")) {
			new Writeitems(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("topitems")) {
			new Topitems(args, player, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("topenchants")) {
			new Topenchants(args, player, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("browseshop")) {
			new Browseshop(args, sender, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("iteminfo") && (player != null)) {
			new Iteminfo(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("itemsettings")) {
			new Itemsettings(args, sender, player, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("enchantsettings")) {
			new Enchantsettings(args, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("taxsettings")) {
			new Taxsettings(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("createeconomy")) {
			new Createeconomy(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("seteconomy")) {
			new Seteconomy(this, args, sender, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("economyinfo")) {
			new Economyinfo(this, args, sender, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setshopeconomy")) {
			new Setshopeconomy(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("deleteeconomy")) {
			new Deleteeconomy(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("listeconomies")) {
			new Listeconomies(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("importnewitems")) {
			new Importnewitems(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("exporttoyml")) {
			new Exporttoyml(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("copydatabase")) {
			new Copydatabase(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hyperlog")) {
			new Hyperlog(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("listcategories")) {
			new Listcategories(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("ymladditem") && (player != null)) {
			new Ymladditem(player, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcbackup")) {
			new Hcbackup(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("ebuy") && player != null) {
			new Ebuy(player, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("esell") && player != null) {
			new Esell(player, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("evalue")) {
			new Evalue(args, player, sender, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("removeshop")) {
			new Removeshop(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("listshops")) {
			new Listshops(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("intervals")) {
			new Intervals(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setbalance")) {
			new Setbalance(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("additem")) {
			new Additem(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("addcategory")) {
			new Addcategory(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("removeitem")) {
			new Removeitem(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("removecategory")) {
			new Removecategory(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("notify")) {
			new Notify(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setstockmedianall")) {
			new Setstockmedianall(sender, args, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("scalebypercent")) {
			new Scalebypercent(sender, args, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("resetshop")) {
			new Resetshop(sender, args, playerecon);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("renameeconomyaccount")) {
			new Renameeconomyaccount(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("renameshop")) {
			new Renameshop(sender, args);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setmessage")) {
			new Setmessage(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setshop") && player != null) {
			new Setshop(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("makedisplay") && player != null) {
			new Makedisplay(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("removedisplay") && player != null) {
			new Removedisplay(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcchunk") && player != null) {
			new Hcchunk(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setlanguage")) {
			new Setlanguage(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("importprices")) {
			new Importprices(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("repairsigns") && player != null) {
			new Repairsigns(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcweb")) {
			new Hcweb(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcclearhistory")) {
			new Hcclearhistory(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcerror")) {
			new Hcerror(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcbalance")) {
			new Hcbalance(args, sender, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hctop")) {
			new Hctop(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("hcpay") && player != null) {
			new Hcpay(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("toggleeconomy")) {
			new Toggleeconomy(sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("makeaccount")) {
			new Makeaccount(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("importbalance")) {
			new Importbalance(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("audit")) {
			new Audit(args, sender);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setchestowner")) {
			new Setchestowner(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("setpassword")) {
			new Setpassword(args, player);
			return true;
		} else if (cmd.getName().equalsIgnoreCase("importfromyml")) {
			new Importfromyml(sender, args);
			return true;
		}
		
		return false;
	}

	public void setNonPlayerEconomy(String economy) {
		nonPlayerEconomy = economy;
	}
	public String getNonPlayerEconomy() {
		return nonPlayerEconomy;
	}
}
