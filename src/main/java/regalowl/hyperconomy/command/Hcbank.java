package regalowl.hyperconomy.command;



import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import regalowl.hyperconomy.DataManager;
import regalowl.hyperconomy.HyperConomy;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.util.LanguageFile;





public class Hcbank implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);
			return true;
		}
		DataManager em = hc.getDataManager();
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if (player == null) {return true;}
		HyperPlayer hp = em.getHyperPlayer(player.getName());
		if (args.length == 0) {
			player.sendMessage(L.get("HCBANK_HELP"));
			return true;
		}
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
			if (args.length != 2) {
				player.sendMessage(L.get("HCBANK_CREATE_HELP"));
				return true;
			}
			if (em.hasBank(args[1])) {
				player.sendMessage(L.get("BANK_ALREADY_EXISTS"));
				return true;
			}
			ArrayList<HyperBank> allBanks = em.getHyperBanks();
			int bankOwnerships = 0;
			for (HyperBank hb:allBanks) {
				if (hb.isOwner(hp)) {
					bankOwnerships++;
				}
			}
			if (bankOwnerships > hc.gYH().gFC("config").getInt("config.max-bank-ownerships-per-player") && !player.hasPermission("hyperconomy.admin")) {
				player.sendMessage(L.get("CANNOT_OWN_MORE_BANKS"));
				return true;
			}
			HyperBank hb = new HyperBank(args[1], hp);
			em.addHyperBank(hb);
			player.sendMessage(L.get("BANK_CREATED"));
		} else if (args[0].equalsIgnoreCase("delete")) {
			if (args.length != 2) {
				player.sendMessage(L.get("HCBANK_DELETE_HELP"));
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage(L.get("BANK_NOT_EXIST"));
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			if (!hb.isOwner(hp)) {
				player.sendMessage(L.get("DONT_OWN_THIS_BANK"));
				return true;
			}
			if (hb.getBalance() > 0) {
				hb.delete();
				player.sendMessage(L.get("BANK_DELETED_DISTRIBUTED"));
			} else {
				hb.delete();
				player.sendMessage(L.get("BANK_DELETED"));
			}
		} else if (args[0].equalsIgnoreCase("addmember") || args[0].equalsIgnoreCase("am")) {
			if (args.length != 3) {
				player.sendMessage(L.get("HCBANK_ADDMEMBER_HELP"));
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage(L.get("BANK_NOT_EXIST"));
				return true;
			}
			if (!em.hyperPlayerExists(args[2])) {
				player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			HyperPlayer account = em.getHyperPlayer(args[2]);
			if (!hb.isOwner(hp)) {
				player.sendMessage(L.get("DONT_OWN_THIS_BANK"));
				return true;
			}
			if (hb.isMember(account)) {
				player.sendMessage(L.get("BANK_ALREADY_MEMBER"));
				return true;
			}
			hb.addMember(account);
			player.sendMessage(L.get("MEMBER_ADDED_BANK"));
		} else if (args[0].equalsIgnoreCase("removemember") || args[0].equalsIgnoreCase("rm")) {
			if (args.length != 3) {
				player.sendMessage(L.get("HCBANK_REMOVEMEMBER_HELP"));
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage(L.get("BANK_NOT_EXIST"));
				return true;
			}
			if (!em.hyperPlayerExists(args[2])) {
				player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			HyperPlayer account = em.getHyperPlayer(args[2]);
			if (!hb.isOwner(hp)) {
				player.sendMessage(L.get("DONT_OWN_THIS_BANK"));
				return true;
			}
			if (!hb.isMember(account)) {
				player.sendMessage(L.get("BANK_NOT_MEMBER"));
				return true;
			}
			hb.removeMember(account);
			player.sendMessage(L.get("MEMBER_REMOVED_BANK"));
		} else  if (args[0].equalsIgnoreCase("addowner") || args[0].equalsIgnoreCase("ao")) {
			if (args.length != 3) {
				player.sendMessage(L.get("HCBANK_ADDOWNER_HELP"));
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage(L.get("BANK_NOT_EXIST"));
				return true;
			}
			if (!em.hyperPlayerExists(args[2])) {
				player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			HyperPlayer account = em.getHyperPlayer(args[2]);
			if (!hb.isOwner(hp)) {
				player.sendMessage(L.get("DONT_OWN_THIS_BANK"));
				return true;
			}
			if (hb.isOwner(account)) {
				player.sendMessage(L.get("BANK_ALREADY_OWNER"));
				return true;
			}
			hb.addOwner(account);
			player.sendMessage(L.get("OWNER_ADDED_BANK"));
		} else if (args[0].equalsIgnoreCase("removeowner") || args[0].equalsIgnoreCase("ro")) {
			if (args.length != 3) {
				player.sendMessage(L.get("HCBANK_REMOVEOWNER_HELP"));
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage(L.get("BANK_NOT_EXIST"));
				return true;
			}
			if (!em.hyperPlayerExists(args[2])) {
				player.sendMessage(L.get("ACCOUNT_NOT_EXIST"));
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			HyperPlayer account = em.getHyperPlayer(args[2]);
			if (!hb.isOwner(hp)) {
				player.sendMessage(L.get("DONT_OWN_THIS_BANK"));
				return true;
			}
			if (!hb.isOwner(account)) {
				player.sendMessage(L.get("BANK_NOT_OWNER"));
				return true;
			}
			if (hb.getOwners().size() == 1) {
				hb.delete();
				player.sendMessage(L.get("BANK_DELETED_DISTRIBUTED"));
			} else {
				hb.removeOwner(account);
				player.sendMessage(L.get("OWNER_REMOVED_BANK"));
			}
		} else if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("d")) {
			if (args.length != 3) {
				player.sendMessage(L.get("HCBANK_DEPOSIT_HELP"));
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage(L.get("BANK_NOT_EXIST"));
				return true;
			}
			double amount = 0.0;
			try {
				amount = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage(L.get("HCBANK_DEPOSIT_HELP"));
				return true;
			}
			if (amount <= 0.0) {
				player.sendMessage(L.get("TRANSFER_GREATER_THAN_ZERO"));
				return true;
			}
			if (!hp.hasBalance(amount)) {
				player.sendMessage(L.get("INSUFFICIENT_FUNDS"));
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			hp.withdraw(amount);
			hb.deposit(amount);
			player.sendMessage(L.get("DEPOSIT_SUCCESSFUL"));
		} else if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("w")) {
			if (args.length != 3) {
				player.sendMessage(L.get("HCBANK_WITHDRAW_HELP"));
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage(L.get("BANK_NOT_EXIST"));
				return true;
			}
			double amount = 0.0;
			try {
				amount = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage(L.get("HCBANK_WITHDRAW_HELP"));
				return true;
			}
			if (amount <= 0.0) {
				player.sendMessage(L.get("TRANSFER_GREATER_THAN_ZERO"));
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			if (!hb.isOwner(hp) && !hb.isMember(hp)) {
				player.sendMessage(L.get("NOT_MEMBER_OF_BANK"));
				return true;
			}
			if (!hb.hasBalance(amount)) {
				player.sendMessage(L.get("BANK_INSUFFICIENT_FUNDS"));
				return true;
			}
			hb.withdraw(amount);
			hp.deposit(amount);
			player.sendMessage(L.get("WITHDRAWAL_SUCCESSFUL"));
		} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
			if (args.length == 1) {
				ArrayList<HyperBank> allBanks = em.getHyperBanks();
				String ownerBanks = "";
				String memberBanks = "";
				for (HyperBank hb:allBanks) {
					if (hb.isOwner(hp)) {
						ownerBanks += hb.getName() + ",";
					}
					if (hb.isMember(hp)) {
						memberBanks += hb.getName() + ",";
					}
				}
				if (ownerBanks.length() > 0) {
					ownerBanks = ownerBanks.substring(0, ownerBanks.length() - 1);
				}
				if (memberBanks.length() > 0) {
					memberBanks = memberBanks.substring(0, memberBanks.length() - 1);
				}
				player.sendMessage(L.get("LINE_BREAK"));
				player.sendMessage(L.f(L.get("BANK_OWNER_OF"), ownerBanks));
				player.sendMessage(L.f(L.get("BANK_MEMBER_OF"), memberBanks));
				player.sendMessage(L.get("LINE_BREAK"));
			}
			if (args.length == 2) {
				if (!em.hasBank(args[1])) {
					player.sendMessage(L.get("BANK_NOT_EXIST"));
					return true;
				}
				HyperBank hb = em.getHyperBank(args[1]);
				if (hb.isOwner(hp) || hp.getPlayer().hasPermission("hyperconomy.viewbanks")) {
					player.sendMessage(L.get("LINE_BREAK"));
					player.sendMessage(L.applyColor("&f" + hb.getName()));
					player.sendMessage(L.f(L.get("BANK_BALANCE"), hb.getBalance()));
					player.sendMessage(L.f(L.get("BANK_OWNERS"), hb.getOwnersList()));
					player.sendMessage(L.f(L.get("BANK_MEMBERS"), hb.getMembersList()));
					player.sendMessage(L.get("LINE_BREAK"));
				} else if (hb.isMember(hp)) {
					player.sendMessage(L.f(L.get("BANK_BALANCE"), hb.getBalance()));
				} else {
					player.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
				}
			}
		} else if (args[0].equalsIgnoreCase("top")) {
			if (!hp.getPlayer().hasPermission("hyperconomy.viewbanks")) {
				player.sendMessage(L.get("YOU_DONT_HAVE_PERMISSION"));
				return true;
			}
			int pe = 1;
			if (args.length == 2) {
				try {
					pe = Integer.parseInt(args[1]);
				} catch (Exception e) {
					pe = 1;
				}
			}
			ArrayList<String> banks = new ArrayList<String>();
			ArrayList<Double> balances = new ArrayList<Double>();
			for (HyperBank hb:em.getHyperBanks()) {
				banks.add(hb.getName());
				balances.add(hb.getBalance());
			}
			ArrayList<String> sbanks = new ArrayList<String>();
			ArrayList<Double> sbalances = new ArrayList<Double>();
			while (balances.size() > 0) {
				int topBalanceIndex = 0;
				double topBalance = 0;
				for (int i = 0; i < balances.size(); i++) {
					double curBal = balances.get(i);
					if (curBal > topBalance) {
						topBalance = curBal;
						topBalanceIndex = i;
					}
				}
				sbalances.add(topBalance);
				sbanks.add(banks.get(topBalanceIndex));
				balances.remove(topBalanceIndex);
				banks.remove(topBalanceIndex);
			}
			double serverTotal = 0.0;
			for (int i = 0; i < sbalances.size(); i++) {
				serverTotal += sbalances.get(i);
			}
			sender.sendMessage(L.get("TOP_BALANCE"));
			sender.sendMessage(L.f(L.get("TOP_BALANCE_PAGE"), pe, (int)Math.ceil(sbalances.size()/10.0)));
			sender.sendMessage(L.f(L.get("TOP_BALANCE_TOTAL"), L.formatMoney(serverTotal)));
			int ps = pe - 1;
			ps *= 10;
			pe *= 10;
			for (int i = ps; i < pe; i++) {
				if (i > (sbalances.size() - 1)) {
					sender.sendMessage(L.get("REACHED_END"));
					return true;
				}
				sender.sendMessage(L.f(L.get("TOP_BALANCE_BALANCE"), sbanks.get(i), L.formatMoney(sbalances.get(i)), (i + 1)));
			}
		} else {
			player.sendMessage(L.get("HCBANK_HELP"));
			return true;
		}

		
		
		return true;
	}
	
	

}
