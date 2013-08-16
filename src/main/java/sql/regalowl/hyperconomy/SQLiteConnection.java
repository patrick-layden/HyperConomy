package regalowl.hyperconomy;

import java.io.File;
import java.sql.DriverManager;

public class SQLiteConnection extends DatabaseConnection {

	private String sqlitePath;

	
	SQLiteConnection() {
		hc = HyperConomy.hc;
		FileTools ft = new FileTools();
		sqlitePath = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "HyperConomy.db";
		openConnection();
		inUse = false;
	}

	
	protected void openConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
		} catch (Exception e) {
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + sqlitePath);
			} catch (Exception e2) {
				new HyperError(e, "Fatal database connection error.");
				return;
			}
		}
	}


}
