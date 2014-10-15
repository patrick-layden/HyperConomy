package regalowl.hyperconomy.command;



import java.util.ArrayList;


import regalowl.hyperconomy.HyperBankManager;
import regalowl.hyperconomy.HyperEconomy;
import regalowl.hyperconomy.account.HyperBank;
import regalowl.hyperconomy.account.HyperPlayer;
import regalowl.hyperconomy.shop.Shop;



public class Hcbank extends BaseCommand implements HyperCommand {
	
	public Hcbank(boolean requirePlayer) {
		super(true);
	}

	@Override
	public CommandData onCommand(CommandData data) {
		if (!validate(data)) return data;
		HyperBankManager hbm = dm.getHyperBankManager();
		if (args.length == 0) {
			data.addResponse(L.get("HCBANK_HELP"));
			return data;
		}
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
			if (args.length != 2) {
				data.addResponse(L.get("HCBANK_CREATE_HELP"));
				return data;
			}
			if (hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_ALREADY_EXISTS"));
				return data;
			}
			if (dm.hyperPlayerExists(args[1])) {
				data.addResponse(L.get("ACCOUNT_ALREADY_EXISTS"));
				return data;
			}
			ArrayList<HyperBank> allBanks = hbm.getHyperBanks();
			int bankOwnerships = 0;
			for (HyperBank hb:allBanks) {
				if (hb.isOwner(hp)) {
					bankOwnerships++;
				}
			}
			if (bankOwnerships > hc.getConf().getInt("bank.max-ownerships-per-player") && !hp.hasPermission("hyperconomy.admin")) {
				data.addResponse(L.get("CANNOT_OWN_MORE_BANKS"));
				return data;
			}
			HyperBank hb = new HyperBank(args[1], hp);
			hbm.addHyperBank(hb);
			data.addResponse(L.get("BANK_CREATED"));
		} else if (args[0].equalsIgnoreCase("delete")) {
			if (args.length != 2) {
				data.addResponse(L.get("HCBANK_DELETE_HELP"));
				return data;
			}
			if (!hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_NOT_EXIST"));
				return data;
			}
			HyperBank hb = hbm.getHyperBank(args[1]);
			if (!hb.isOwner(hp)) {
				data.addResponse(L.get("DONT_OWN_THIS_BANK"));
				return data;
			}
			for (HyperEconomy he:hc.getDataManager().getEconomies()) {
				if (he.getDefaultAccount() == hb) {
					data.addResponse(L.get("BANK_IN_USE_BY_ECONOMY"));
					return data;
				}
			}
			for (Shop s:hc.getHyperShopManager().getShops()) {
				if (s.getOwner() == hb) {
					data.addResponse(L.get("BANK_IN_USE_BY_SHOP"));
					return data;
				}
			}
			if (hb.getBalance() > 0) {
				hb.delete();
				data.addResponse(L.get("BANK_DELETED_DISTRIBUTED"));
			} else {
				hb.delete();
				data.addResponse(L.get("BANK_DELETED"));
			}
		} else if (args[0].equalsIgnoreCase("rename")) {
			if (args.length != 3) {
				data.addResponse(L.get("HCBANK_RENAME_HELP"));
				return data;
			}
			if (!hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_NOT_EXIST"));
				return data;
			}
			HyperBank hb = hbm.getHyperBank(args[1]);
			if (!hb.isOwner(hp)) {
				data.addResponse(L.get("DONT_OWN_THIS_BANK"));
				return data;
			}
			hb.setName(args[2]);
			data.addResponse(L.get("BANK_RENAMED"));
		} else if (args[0].equalsIgnoreCase("addmember") || args[0].equalsIgnoreCase("am")) {
			if (args.length != 3) {
				data.addResponse(L.get("HCBANK_ADDMEMBER_HELP"));
				return data;
			}
			if (!hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_NOT_EXIST"));
				return data;
			}
			if (!dm.hyperPlayerExists(args[2])) {
				data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
				return data;
			}
			HyperBank hb = hbm.getHyperBank(args[1]);
			HyperPlayer account = dm.getHyperPlayer(args[2]);
			if (!hb.isOwner(hp)) {
				data.addResponse(L.get("DONT_OWN_THIS_BANK"));
				return data;
			}
			if (hb.isMember(account)) {
				data.addResponse(L.get("BANK_ALREADY_MEMBER"));
				return data;
			}
			hb.addMember(account);
			data.addResponse(L.get("MEMBER_ADDED_BANK"));
		} else if (args[0].equalsIgnoreCase("removemember") || args[0].equalsIgnoreCase("rm")) {
			if (args.length != 3) {
				data.addResponse(L.get("HCBANK_REMOVEMEMBER_HELP"));
				return data;
			}
			if (!hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_NOT_EXIST"));
				return data;
			}
			if (!dm.hyperPlayerExists(args[2])) {
				data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
				return data;
			}
			HyperBank hb = hbm.getHyperBank(args[1]);
			HyperPlayer account = dm.getHyperPlayer(args[2]);
			if (!hb.isOwner(hp)) {
				data.addResponse(L.get("DONT_OWN_THIS_BANK"));
				return data;
			}
			if (!hb.isMember(account)) {
				data.addResponse(L.get("BANK_NOT_MEMBER"));
				return data;
			}
			hb.removeMember(account);
			data.addResponse(L.get("MEMBER_REMOVED_BANK"));
		} else  if (args[0].equalsIgnoreCase("addowner") || args[0].equalsIgnoreCase("ao")) {
			if (args.length != 3) {
				data.addResponse(L.get("HCBANK_ADDOWNER_HELP"));
				return data;
			}
			if (!hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_NOT_EXIST"));
				return data;
			}
			if (!dm.hyperPlayerExists(args[2])) {
				data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
				return data;
			}
			HyperBank hb = hbm.getHyperBank(args[1]);
			HyperPlayer account = dm.getHyperPlayer(args[2]);
			if (!hb.isOwner(hp)) {
				data.addResponse(L.get("DONT_OWN_THIS_BANK"));
				return data;
			}
			if (hb.isOwner(account)) {
				data.addResponse(L.get("BANK_ALREADY_OWNER"));
				return data;
			}
			hb.addOwner(account);
			data.addResponse(L.get("OWNER_ADDED_BANK"));
		} else if (args[0].equalsIgnoreCase("removeowner") || args[0].equalsIgnoreCase("ro")) {
			if (args.length != 3) {
				data.addResponse(L.get("HCBANK_REMOVEOWNER_HELP"));
				return data;
			}
			if (!hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_NOT_EXIST"));
				return data;
			}
			if (!dm.hyperPlayerExists(args[2])) {
				data.addResponse(L.get("ACCOUNT_NOT_EXIST"));
				return data;
			}
			HyperBank hb = hbm.getHyperBank(args[1]);
			HyperPlayer account = dm.getHyperPlayer(args[2]);
			if (!hb.isOwner(hp)) {
				data.addResponse(L.get("DONT_OWN_THIS_BANK"));
				return data;
			}
			if (!hb.isOwner(account)) {
				data.addResponse(L.get("BANK_NOT_OWNER"));
				return data;
			}
			if (hb.getOwners().size() == 1) {
				hb.delete();
				data.addResponse(L.get("BANK_DELETED_DISTRIBUTED"));
			} else {
				hb.removeOwner(account);
				data.addResponse(L.get("OWNER_REMOVED_BANK"));
			}
		} else if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("d")) {
			if (args.length != 3) {
				data.addResponse(L.get("HCBANK_DEPOSIT_HELP"));
				return data;
			}
			if (!hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_NOT_EXIST"));
				return data;
			}
			double amount = 0.0;
			try {
				amount = Double.parseDouble(args[2]);
			} catch (Exception e) {
				data.addResponse(L.get("HCBANK_DEPOSIT_HELP"));
				return data;
			}
			if (amount <= 0.0) {
				data.addResponse(L.get("TRANSFER_GREATER_THAN_ZERO"));
				return data;
			}
			if (!hp.hasBalance(amount)) {
				data.addResponse(L.get("INSUFFICIENT_FUNDS"));
				return data;
			}
			HyperBank hb = hbm.getHyperBank(args[1]);
			hp.withdraw(amount);
			hb.deposit(amount);
			data.addResponse(L.get("DEPOSIT_SUCCESSFUL"));
		} else if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("w")) {
			if (args.length != 3) {
				data.addResponse(L.get("HCBANK_WITHDRAW_HELP"));
				return data;
			}
			if (!hbm.hasBank(args[1])) {
				data.addResponse(L.get("BANK_NOT_EXIST"));
				return data;
			}
			double amount = 0.0;
			try {
				amount = Double.parseDouble(args[2]);
			} catch (Exception e) {
				data.addResponse(L.get("HCBANK_WITHDRAW_HELP"));
				return data;
			}
			if (amount <= 0.0) {
				data.addResponse(L.get("TRANSFER_GREATER_THAN_ZERO"));
				return data;
			}
			HyperBank hb = hbm.getHyperBank(args[1]);
			if (!hb.isOwner(hp) && !hb.isMember(hp)) {
				data.addResponse(L.get("NOT_MEMBER_OF_BANK"));
				return data;
			}
			if (!hb.hasBalance(amount)) {
				data.addResponse(L.get("BANK_INSUFFICIENT_FUNDS"));
				return data;
			}
			hb.withdraw(amount);
			hp.deposit(amount);
			data.addResponse(L.get("WITHDRAWAL_SUCCESSFUL"));
		} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
			if (args.length == 1) {
				ArrayList<HyperBank> allBanks = hbm.getHyperBanks();
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
				data.addResponse(L.get("LINE_BREAK"));
				data.addResponse(L.f(L.get("BANK_OWNER_OF"), ownerBanks));
				data.addResponse(L.f(L.get("BANK_MEMBER_OF"), memberBanks));
				data.addResponse(L.get("LINE_BREAK"));
			}
			if (args.length == 2) {
				if (!hbm.hasBank(args[1])) {
					data.addResponse(L.get("BANK_NOT_EXIST"));
					return data;
				}
				HyperBank hb = hbm.getHyperBank(args[1]);
				if (hb.isOwner(hp) || hp.getPlayer().hasPermission("hyperconomy.viewbanks")) {
					data.addResponse(L.get("LINE_BREAK"));
					data.addResponse(L.applyColor("&b&o" + hb.getName()));
					data.addResponse(L.f(L.get("BANK_BALANCE"), hb.getBalance()));
					data.addResponse(L.f(L.get("BANK_OWNERS"), hb.getOwnersList()));
					data.addResponse(L.f(L.get("BANK_MEMBERS"), hb.getMembersList()));
					data.addResponse(L.get("LINE_BREAK"));
				} else if (hb.isMember(hp)) {
					data.addResponse(L.f(L.get("BANK_BALANCE"), hb.getBalance()));
				} else {
					data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
				}
			}
		} else if (args[0].equalsIgnoreCase("top")) {
			if (!hp.getPlayer().hasPermission("hyperconomy.viewbanks")) {
				data.addResponse(L.get("YOU_DONT_HAVE_PERMISSION"));
				return data;
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
			for (HyperBank hb:hbm.getHyperBanks()) {
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
			data.addResponse(L.get("TOP_BALANCE"));
			data.addResponse(L.f(L.get("TOP_BALANCE_PAGE"), pe, (int)Math.ceil(sbalances.size()/10.0)));
			data.addResponse(L.f(L.get("TOP_BALANCE_TOTAL"), L.formatMoney(serverTotal)));
			int ps = pe - 1;
			ps *= 10;
			pe *= 10;
			for (int i = ps; i < pe; i++) {
				if (i > (sbalances.size() - 1)) {
					data.addResponse(L.get("REACHED_END"));
					return data;
				}
				data.addResponse(L.f(L.get("TOP_BALANCE_BALANCE"), sbanks.get(i), L.formatMoney(sbalances.get(i)), (i + 1)));
			}
		} else {
			data.addResponse(L.get("HCBANK_HELP"));
			return data;
		}

		
		
		return data;
	}
	
	

}
