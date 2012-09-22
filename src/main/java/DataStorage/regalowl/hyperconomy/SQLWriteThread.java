package regalowl.hyperconomy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


public class SQLWriteThread {


	private HyperConomy hc;
	private SQLWrite sw;
	private int savecompletetaskid;
	private int writethreadid;
	private String statement;
	private boolean savecomplete;
	private ConnectionPool cp;
	private Connection connection;
		

		
	public void writeThread(HyperConomy hyc, SQLWrite sqw, ConnectionPool cop, String state) {
		hc = hyc;
		sw = sqw;
		cp = cop;
		statement = state;

			
		savecomplete = false;

		writethreadid = hc.getServer().getScheduler().scheduleAsyncDelayedTask(hc, new Runnable() {
			public void run() {
				try {
					connection = cp.getConnection();
					if (connection == null || connection.isClosed()) {
						failed();
						return;
					} else {
						Statement state = connection.createStatement();
						state.execute(statement);
					    state.close();
					    cp.returnConnection(connection);
					    savecomplete = true;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					cp.returnConnection(connection);
					sw.writeFailed(statement);
					savecomplete = true;
				}
			}
		}, 0L);
		

			
		savecompletetaskid = hc.getServer().getScheduler().scheduleSyncRepeatingTask(hc, new Runnable() {
			public void run() {
				if (savecomplete) {
					sw.writeSuccess(statement);
					hc.getServer().getScheduler().cancelTask(savecompletetaskid);
					hc.getServer().getScheduler().cancelTask(writethreadid);
				}
			}
		}, 1L, 1L);
			
		
		
	}


	private void failed() {
		sw.writeFailed(statement);
		savecomplete = true;
	}

	
}
