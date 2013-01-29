package regalowl.hyperconomy;


import java.io.File;
import java.util.ArrayList;

public class SQLShutdown {


	
	SQLShutdown(HyperConomy hyc, SQLWrite sw) {

		ArrayList<String> statements = sw.getBuffer();
		if (statements.size() > 0) {
			FileTools ft = new FileTools();
			SerializeArrayList sal =  new SerializeArrayList();
			String path = ft.getJarPath() + File.separator + "plugins" + File.separator + "HyperConomy" + File.separator + "temp";
			ft.makeFolder(path);
			path += File.separator + "buffer.txt";
			String stringBuffer = sal.stringArrayToStringA(statements);
			ft.writeStringToFile(stringBuffer, path);
		}

		
	}
}
