package regalowl.hyperconomy;


import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;


public class Copydatabase {
	
	private HyperConomy hc;
	private SQLWrite tempWrite;
	private BukkitTask waitTask;
	private CommandSender sender;
	private String mysqlMessage;
	private String sqliteMessage;
	

	Copydatabase(CommandSender sender, String args[]) {
		hc = HyperConomy.hc;
		this.sender = sender;
		LanguageFile L = hc.getLanguageFile();
		
		try {
			mysqlMessage = L.get("COPYDATABASE_MYSQL");
			sqliteMessage = L.get("COPYDATABASE_SQLITE");
			if (args.length == 0) {
				if (hc.useMySQL()) {
					sender.sendMessage(L.get("COPYDATABASE_MYSQL_WARNING"));
				} else {
					sender.sendMessage(L.get("COPYDATABASE_SQLITE_WARNING"));
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
				SQLEconomy se = hc.getSQLEconomy();
				DataHandler dh = hc.getDataFunctions();
				if (hc.useMySQL()) {
					boolean databaseOk = se.checkSQLLite();
					if (databaseOk) {
						hc.lockHyperConomy(true);

						hc.setUseMySQL(false);
						tempWrite = new SQLWrite();
						for (HyperObject ho:dh.getHyperObjects()) {
							tempWrite.executeSQL("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + ho.getName() + "','" + ho.getEconomy() + "','" + ho.getType() + "','" + ho.getCategory() + "','" + ho.getMaterial() + "','" + ho.getId() + "','" + ho.getData() + "','"
									+ ho.getDurability() + "','" + ho.getValue() + "','" + ho.getIsstatic() + "','" + ho.getStaticprice() + "','" + ho.getStock() + "','" + ho.getMedian() + "','" + ho.getInitiation() + "','" + ho.getStartprice()
									+ "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
						}
						for (HyperPlayer hp:dh.getHyperPlayers()) {
							tempWrite.executeSQL("Insert Into hyperconomy_objects (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH)" + " Values ('" + hp.getName() + "','" + hp.getEconomy() + "','" + hp.getBalance() + "','" + hp.getX() + "','" + hp.getY() + "','" + hp.getZ() + "','" + hp.getWorld() + "','"
									+ hp.getHash() + "')");
						}
						waitForFinish();
						sender.sendMessage(L.get("COPYDATABASE_STARTED"));
					} else {
						sender.sendMessage(L.get("COPYDATABASE_CONNECTION_FAILED_MYSQL"));
						return;
					}
				} else {
					boolean databaseOk = se.checkMySQL();
					if (databaseOk) {
						hc.lockHyperConomy(true);
						hc.setUseMySQL(true);
						tempWrite = new SQLWrite();
						for (HyperObject ho:dh.getHyperObjects()) {
							tempWrite.executeSQL("Insert Into hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " Values ('" + ho.getName() + "','" + ho.getEconomy() + "','" + ho.getType() + "','" + ho.getCategory() + "','" + ho.getMaterial() + "','" + ho.getId() + "','" + ho.getData() + "','"
									+ ho.getDurability() + "','" + ho.getValue() + "','" + ho.getIsstatic() + "','" + ho.getStaticprice() + "','" + ho.getStock() + "','" + ho.getMedian() + "','" + ho.getInitiation() + "','" + ho.getStartprice()
									+ "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
						}
						for (HyperPlayer hp:dh.getHyperPlayers()) {
							tempWrite.executeSQL("Insert Into hyperconomy_objects (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH)" + " Values ('" + hp.getName() + "','" + hp.getEconomy() + "','" + hp.getBalance() + "','" + hp.getX() + "','" + hp.getY() + "','" + hp.getZ() + "','" + hp.getWorld() + "','"
									+ hp.getHash() + "')");
						}
						waitForFinish();
						sender.sendMessage(L.get("COPYDATABASE_STARTED"));
					} else {
						sender.sendMessage(L.get("COPYDATABASE_CONNECTION_FAILED_SQLITE"));
						return;
					}
				}
			} else {
				sender.sendMessage(L.get("COPYDATABASE_INVALID"));
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("COPYDATABASE_INVALID"));
			return;
		}
	}
	
	
	
	
	
	
	
	private void waitForFinish() {
		waitTask = hc.getServer().getScheduler().runTaskTimerAsynchronously(hc, new Runnable() {
    		public void run() {
    			if (tempWrite == null || tempWrite.getBufferSize() == 0) {
    				hc.setUseMySQL(!hc.useMySQL());
    				hc.lockHyperConomy(false);
    				hc.getServer().getScheduler().runTask(hc, new Runnable() {
    		    		public void run() {
    		    			if (hc.useMySQL()) {
    		    				sender.sendMessage(mysqlMessage);
    		    			} else {
    		    				sender.sendMessage(sqliteMessage);
    		    			}
    		    		}
    		    	});
    				endWait();
    			}
    		}
    	}, 5L, 5L);
	}
	
	
	private void endWait() {
		waitTask.cancel();
		tempWrite = null;
	}
	
	
}
