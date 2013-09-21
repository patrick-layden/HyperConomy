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
	private LanguageFile L;
	private boolean includeHistory;

	Copydatabase(CommandSender csender, String args[]) {
		hc = HyperConomy.hc;
		sender = csender;
		L = hc.getLanguageFile();
		includeHistory = false;
		
		try {
			mysqlMessage = L.get("COPYDATABASE_MYSQL");
			sqliteMessage = L.get("COPYDATABASE_SQLITE");
			if (args.length == 0) {
				if (hc.s().gB("sql-connection.use-mysql")) {
					sender.sendMessage(L.get("COPYDATABASE_MYSQL_WARNING"));
				} else {
					sender.sendMessage(L.get("COPYDATABASE_SQLITE_WARNING"));
				}
			} else if (args.length >= 1 && args[0].equalsIgnoreCase("confirm")) {
				if (args.length == 2 && args[1].equalsIgnoreCase("history")) {
					includeHistory = true;
				}
				if (hc.getSQLWrite().getBufferSize() != 0) {
					sender.sendMessage(L.get("WAIT_FOR_QUEUE"));
					return;
				}
				hc.getServer().getScheduler().runTaskAsynchronously(hc, new Runnable() {
		    		public void run() {
		    			EconomyManager em = hc.getEconomyManager();
						SQLRead sr = hc.getSQLRead();
						if (hc.s().gB("sql-connection.use-mysql")) {
							boolean databaseOk = em.checkSQLLite();
							if (databaseOk) {
								hc.loadLock(true);

								hc.s().sB("sql-connection.use-mysql", false);
								tempWrite = new SQLWrite();
								tempWrite.executeSQL("DELETE FROM hyperconomy_objects");
								tempWrite.executeSQL("DELETE FROM hyperconomy_players");
								tempWrite.executeSQL("DELETE FROM hyperconomy_audit_log");
								if (includeHistory) {
									tempWrite.executeSQL("DELETE FROM hyperconomy_history");
								}
								tempWrite.executeSQL("DELETE FROM hyperconomy_log");
								tempWrite.executeSQL("DELETE FROM hyperconomy_settings");
								for (HyperObject ho:em.getHyperObjects()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " VALUES ('" + ho.getName() + "','" + ho.getEconomy() + "','" + ho.getType() + "','" + ho.getCategory() + "','" + ho.getMaterial() + "','" + ho.getId() + "','" + ho.getData() + "','"
											+ ho.getDurability() + "','" + ho.getValue() + "','" + ho.getIsstatic() + "','" + ho.getStaticprice() + "','" + ho.getStock() + "','" + ho.getMedian() + "','" + ho.getInitiation() + "','" + ho.getStartprice()
											+ "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
								}
								for (HyperPlayer hp:em.getHyperPlayers()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH)" + " VALUES ('" + hp.getName() + "','" + hp.getEconomy() + "','" + hp.getBalance() + "','" + hp.getX() + "','" + hp.getY() + "','" + hp.getZ() + "','" + hp.getWorld() + "','"
											+ hp.getHash() + "')");
								}
								QueryResult result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_audit_log");
								while (result.next()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_audit_log (TIME, ACCOUNT, ACTION, AMOUNT, ECONOMY) VALUES ('" + result.getString("TIME") + "','" + result.getString("ACCOUNT") + "','" + result.getString("ACTION") + "','" + result.getDouble("AMOUNT") + "','" + result.getString("ECONOMY") + "')");
								}
								result.close();
								result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_log");
								while (result.next()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_log (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE) VALUES ('" + result.getString("TIME") + "','" + result.getString("CUSTOMER") + "','" + result.getString("ACTION") + "','" + result.getString("OBJECT") + "','" + result.getDouble("AMOUNT") + "','" + result.getDouble("MONEY") + "','" + result.getDouble("TAX") + "','" + result.getString("STORE") + "','" + result.getString("TYPE") + "')");
								}
								result.close();
								if (includeHistory) {
									result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_history");
									while (result.next()) {
										tempWrite.executeSQL("INSERT INTO hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE)" + " VALUES ('" + result.getString("OBJECT") + "','" + result.getString("ECONOMY") + "','" + result.getString("TIME") + "','" + result.getDouble("PRICE") + "')");
									}
									result.close();
								}
								result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_settings");
								while (result.next()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME)" + " VALUES ('" + result.getString("SETTING") + "','" + result.getString("VALUE") + "','" + result.getString("TIME") + "')");
								}
								result.close();
								waitForFinish();
								hc.getServer().getScheduler().runTask(hc, new Runnable() {
						    		public void run() {
						    			sender.sendMessage(L.get("COPYDATABASE_STARTED"));
						    		}
						    	});
							} else {
								hc.getServer().getScheduler().runTask(hc, new Runnable() {
						    		public void run() {
						    			sender.sendMessage(L.get("COPYDATABASE_CONNECTION_FAILED_MYSQL"));
						    		}
						    	});
								return;
							}
						} else {
							boolean databaseOk = em.checkMySQL();
							if (databaseOk) {
								hc.loadLock(true);
								hc.s().sB("sql-connection.use-mysql", true);
								tempWrite = new SQLWrite();
								tempWrite.executeSQL("DELETE FROM hyperconomy_objects");
								tempWrite.executeSQL("DELETE FROM hyperconomy_players");
								tempWrite.executeSQL("DELETE FROM hyperconomy_audit_log");
								if (includeHistory) {
									tempWrite.executeSQL("DELETE FROM hyperconomy_history");
								}
								tempWrite.executeSQL("DELETE FROM hyperconomy_log");
								tempWrite.executeSQL("DELETE FROM hyperconomy_settings");
								for (HyperObject ho:em.getHyperObjects()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_objects (NAME, ECONOMY, TYPE, CATEGORY, MATERIAL, ID, DATA, DURABILITY, VALUE, STATIC, STATICPRICE, STOCK, MEDIAN, INITIATION, STARTPRICE, CEILING, FLOOR, MAXSTOCK)" + " VALUES ('" + ho.getName() + "','" + ho.getEconomy() + "','" + ho.getType() + "','" + ho.getCategory() + "','" + ho.getMaterial() + "','" + ho.getId() + "','" + ho.getData() + "','"
											+ ho.getDurability() + "','" + ho.getValue() + "','" + ho.getIsstatic() + "','" + ho.getStaticprice() + "','" + ho.getStock() + "','" + ho.getMedian() + "','" + ho.getInitiation() + "','" + ho.getStartprice()
											+ "','" + ho.getCeiling() + "','" + ho.getFloor() + "','" + ho.getMaxstock() + "')");
								}
								for (HyperPlayer hp:em.getHyperPlayers()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_players (PLAYER, ECONOMY, BALANCE, X, Y, Z, WORLD, HASH)" + " VALUES ('" + hp.getName() + "','" + hp.getEconomy() + "','" + hp.getBalance() + "','" + hp.getX() + "','" + hp.getY() + "','" + hp.getZ() + "','" + hp.getWorld() + "','"
											+ hp.getHash() + "')");
								}
								QueryResult result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_audit_log");
								while (result.next()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_audit_log (TIME, ACCOUNT, ACTION, AMOUNT, ECONOMY) VALUES ('" + result.getString("TIME") + "','" + result.getString("ACCOUNT") + "','" + result.getString("ACTION") + "','" + result.getDouble("AMOUNT") + "','" + result.getString("ECONOMY") + "')");
								}
								result.close();
								result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_log");
								while (result.next()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_log (TIME, CUSTOMER, ACTION, OBJECT, AMOUNT, MONEY, TAX, STORE, TYPE) VALUES ('" + result.getString("TIME") + "','" + result.getString("CUSTOMER") + "','" + result.getString("ACTION") + "','" + result.getString("OBJECT") + "','" + result.getDouble("AMOUNT") + "','" + result.getDouble("MONEY") + "','" + result.getDouble("TAX") + "','" + result.getString("STORE") + "','" + result.getString("TYPE") + "')");
								}
								result.close();
								if (includeHistory) {
									result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_history");
									while (result.next()) {
										tempWrite.executeSQL("INSERT INTO hyperconomy_history (OBJECT, ECONOMY, TIME, PRICE)" + " VALUES ('" + result.getString("OBJECT") + "','" + result.getString("ECONOMY") + "','" + result.getString("TIME") + "','" + result.getDouble("PRICE") + "')");
									}
									result.close();
								}
								result = sr.getDatabaseConnection().read("SELECT * FROM hyperconomy_settings");
								while (result.next()) {
									tempWrite.executeSQL("INSERT INTO hyperconomy_settings (SETTING, VALUE, TIME)" + " VALUES ('" + result.getString("SETTING") + "','" + result.getString("VALUE") + "','" + result.getString("TIME") + "')");
								}
								result.close();
								waitForFinish();
								hc.getServer().getScheduler().runTask(hc, new Runnable() {
						    		public void run() {
						    			sender.sendMessage(L.get("COPYDATABASE_STARTED"));
						    		}
						    	});
							} else {
								hc.getServer().getScheduler().runTask(hc, new Runnable() {
						    		public void run() {
						    			sender.sendMessage(L.get("COPYDATABASE_CONNECTION_FAILED_MYSQL"));
						    		}
						    	});
								return;
							}
						}
		    		}
		    	});

			} else {
				sender.sendMessage(L.get("COPYDATABASE_INVALID"));
			}
			return;
		} catch (Exception e) {
			sender.sendMessage(L.get("COPYDATABASE_INVALID"));
			hc.loadLock(false);
			return;
		}
	}
	
	
	
	
	
	
	
	private void waitForFinish() {
		waitTask = hc.getServer().getScheduler().runTaskTimerAsynchronously(hc, new Runnable() {
    		public void run() {
				hc.getServer().getScheduler().runTask(hc, new Runnable() {
		    		public void run() {
		    			if (tempWrite != null) {
		    				sender.sendMessage(tempWrite.getBufferSize() + " statements remaining.");
		    			}
		    		}
		    	});
    			if (tempWrite == null || tempWrite.getBufferSize() == 0) {
    				hc.s().sB("sql-connection.use-mysql", !hc.s().gB("sql-connection.use-mysql"));
    				hc.loadLock(false);
    				hc.getServer().getScheduler().runTask(hc, new Runnable() {
    		    		public void run() {
    		    			if (hc.s().gB("sql-connection.use-mysql")) {
    		    				sender.sendMessage(mysqlMessage);
    		    			} else {
    		    				sender.sendMessage(sqliteMessage);
    		    			}
    		    		}
    		    	});
    				endWait();
    			}
    		}
    	}, 40L, 40L);
	}
	
	
	private void endWait() {
		waitTask.cancel();
		tempWrite = null;
	}
	
	
}
