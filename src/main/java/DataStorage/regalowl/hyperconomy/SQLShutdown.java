package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SQLShutdown {

	private double totalStatements;
	private double finishedStatements;
	private ArrayList<String> statements;
	private Logger log;
	private HyperConomy hc;
	private int counter;
	
	SQLShutdown(HyperConomy hyc, SQLWrite sw) {
		hc = hyc;
		finishedStatements = 0;
		log = Logger.getLogger("Minecraft");
		counter = 0;
		
		statements = sw.getBuffer();
		if (statements.size() > 0) {
			FileTools ft = new FileTools();
			SerializeArrayList sal =  new SerializeArrayList();
			String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "temp";
			ft.makeFolder(path);
			path += File.separator + "buffer.txt";
			String stringBuffer = sal.stringArrayToString(statements);
			ft.writeStringToFile(stringBuffer, path);
		}

		/*
		ArrayList<ArrayList<String>> sstatements = new ArrayList<ArrayList<String>>();
		
		for (int i = 0; i < 20; i++) {
			ArrayList<String> substatements = new ArrayList<String>();
			sstatements.add(substatements);
		}
		totalStatements = statements.size();
		int counter = 0;
		for (int i = 0; i < statements.size(); i++) {
			if (counter >= 20) {
				counter = 0;
			}
			ArrayList<String> cstatement = sstatements.get(counter);
			cstatement.add(statements.get(i));
			sstatements.set(counter, cstatement);
			counter++;
		}
		
		for (int i = 0; i < 20; i++) {
			new ShutdownThread(hc, this, sstatements.get(i));
		}
		*/
		
	}
	
	


	
	
	public void statementComplete() {
		finishedStatements++;
		counter++;
		if (counter >= 60) {
			showStatus();
			counter = 0;
		}
	}
	
	
	public void showStatus() {
		double pc = hc.getCalculation().twoDecimals((finishedStatements/totalStatements) * 100);
		log.info("HyperConomy: Saving to database: " + pc + "% complete!");
	}
	
}
