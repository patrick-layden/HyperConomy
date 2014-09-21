package regalowl.hyperconomy.command;




import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;









public class Hctest implements CommandExecutor {

	//private String input;
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		/*
		input = "test!";
		if (args.length > 0) input = args[0];

		HyperConomy.hc.getServer().getScheduler().runTaskAsynchronously(HyperConomy.hc, new Runnable() {
			public void run() {
				try {

					Socket serverSocket = new Socket();   
					serverSocket.connect(new InetSocketAddress("192.168.100.45", 3312), 2000); 
					
					ObjectOutputStream out = new ObjectOutputStream(serverSocket.getOutputStream());
					out.writeObject(input);
					out.flush();
					HyperConomy.hc.log().severe("wrote message [" + input + "]");
					
					ObjectInputStream input = new ObjectInputStream(serverSocket.getInputStream());
					String objectReceived = (String) input.readObject();
					HyperConomy.hc.log().severe("got reply [" + objectReceived + "]");
					serverSocket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		*/
		return true;
		
	}
	

}
