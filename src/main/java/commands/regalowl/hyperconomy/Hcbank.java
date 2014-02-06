package regalowl.hyperconomy;



import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;





public class Hcbank implements CommandExecutor {
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		HyperConomy hc = HyperConomy.hc;
		LanguageFile L = hc.getLanguageFile();
		if (hc.getHyperLock().isLocked(sender)) {
			hc.getHyperLock().sendLockMessage(sender);
			return true;
		}
		EconomyManager em = hc.getEconomyManager();
		Player player = null;
		if (sender instanceof Player) {
			player = (Player)sender;
		}
		if (player == null) {return true;}
		HyperPlayer hp = em.getHyperPlayer(player.getName());
		if (args.length == 0) {
			player.sendMessage("hcbank help");
			return true;
		}
		if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c")) {
			if (args.length != 2) {
				player.sendMessage("hcbank create help");
				return true;
			}
			if (em.hasBank(args[1])) {
				player.sendMessage("bank already exists");
				return true;
			}
			HyperBank hb = new HyperBank(args[1], hp);
			em.addHyperBank(hb);
			player.sendMessage("bank added");
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (args.length != 2) {
				player.sendMessage("hcbank remove help");
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage("bank doesn't exist");
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			em.removeHyperBank(hb);
			player.sendMessage("bank removed");
		} else if (args[0].equalsIgnoreCase("addmember") || args[0].equalsIgnoreCase("am")) {
			if (args.length != 3) {
				player.sendMessage("Use /hcbank addmember [bank] [member]");
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage("bank doesn't exist");
				return true;
			}
			if (!em.hasAccount(args[2])) {
				player.sendMessage("account doesn't exist");
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			HyperPlayer account = em.getHyperPlayer(args[2]);
			hb.addMember(account);
			player.sendMessage("member added");
		} else if (args[0].equalsIgnoreCase("removemember") || args[0].equalsIgnoreCase("rm")) {
			if (args.length != 3) {
				player.sendMessage("Use /hcbank removemember [bank] [member]");
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage("bank doesn't exist");
				return true;
			}
			if (!em.hasAccount(args[2])) {
				player.sendMessage("account doesn't exist");
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			HyperPlayer account = em.getHyperPlayer(args[2]);
			hb.removeMember(account);
			player.sendMessage("member removed");
		} else  if (args[0].equalsIgnoreCase("addowner") || args[0].equalsIgnoreCase("ao")) {
			if (args.length != 3) {
				player.sendMessage("Use /hcbank addowner [bank] [owner]");
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage("bank doesn't exist");
				return true;
			}
			if (!em.hasAccount(args[2])) {
				player.sendMessage("account doesn't exist");
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			HyperPlayer account = em.getHyperPlayer(args[2]);
			hb.addOwner(account);
			player.sendMessage("owner added");
		} else if (args[0].equalsIgnoreCase("removeowner") || args[0].equalsIgnoreCase("ro")) {
			if (args.length != 3) {
				player.sendMessage("Use /hcbank remove [bank] [owner]");
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage("bank doesn't exist");
				return true;
			}
			if (!em.hasAccount(args[2])) {
				player.sendMessage("account doesn't exist");
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			HyperPlayer account = em.getHyperPlayer(args[2]);
			hb.removeOwner(account);
			player.sendMessage("owner removed");
		} else if (args[0].equalsIgnoreCase("deposit") || args[0].equalsIgnoreCase("d")) {
			if (args.length != 3) {
				player.sendMessage("Use /hcbank deposit [bank] [amount]");
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage("bank doesn't exist");
				return true;
			}
			double amount = 0.0;
			try {
				amount = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage("Use /hcbank deposit [bank] [amount]");;
				return true;
			}
			if (!hp.hasBalance(amount)) {
				player.sendMessage("You don't have enough money.");;
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			hp.withdraw(amount);
			hb.deposit(amount);
			player.sendMessage("money deposited");
		} else if (args[0].equalsIgnoreCase("withdraw") || args[0].equalsIgnoreCase("w")) {
			if (args.length != 3) {
				player.sendMessage("Use /hcbank withdraw [bank] [amount]");
				return true;
			}
			if (!em.hasBank(args[1])) {
				player.sendMessage("bank doesn't exist");
				return true;
			}
			double amount = 0.0;
			try {
				amount = Double.parseDouble(args[2]);
			} catch (Exception e) {
				player.sendMessage("Use /hcbank withdraw [bank] [amount]");;
				return true;
			}
			HyperBank hb = em.getHyperBank(args[1]);
			if (!hb.hasBalance(amount)) {
				player.sendMessage("The bank doesn't have enough money.");;
				return true;
			}
			hb.withdraw(amount);
			hp.deposit(amount);
			player.sendMessage("money withdrawn");
		} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) {
			if (args.length == 1) {
				player.sendMessage("Banks: " + hc.gCF().implode(em.getHyperBankNames(), ","));
			}
		} else {
			player.sendMessage("hcbank help");
			return true;
		}

		
		
		return true;
	}
	
	

}
