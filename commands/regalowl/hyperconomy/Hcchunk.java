package regalowl.hyperconomy;



public class Hcchunk {
	

/*
	Hcchunk(String args[], Player player) {
		try {
			if (args.length == 1 && args[0].equalsIgnoreCase("unload")) {
				player.getLocation().getChunk().unload();
				player.sendMessage(ChatColor.GOLD + "The chunk that you're in has been unloaded.");
				return;
			} else if (args.length >= 3 && args[0].equalsIgnoreCase("unload")) {
				if (args.length == 3) {
					int x = Integer.parseInt(args[1]);
					int z = Integer.parseInt(args[2]);
					World w = player.getWorld();
					Chunk c = w.getChunkAt(x, z);
					c.unload();
					player.sendMessage(ChatColor.GOLD + "The selected chunk has been unloaded.");
				} else if (args.length == 4) {
					int x = Integer.parseInt(args[1]);
					int z = Integer.parseInt(args[2]);
					World w = Bukkit.getWorld(args[3]);
					Chunk c = w.getChunkAt(x, z);
					c.unload();
					player.sendMessage(ChatColor.GOLD + "The selected chunk has been unloaded.");
				}
			} else if (args.length == 1 && args[0].equalsIgnoreCase("info")) {
				Chunk c = player.getLocation().getChunk();
				int x = c.getX();
				int z = c.getZ();
				boolean loaded = c.isLoaded();
				Entity[] entities = c.getEntities();
				ArrayList<String> etypes = new ArrayList<String>();
				for (int i = 0; i < entities.length; i++) {
					etypes.add(entities[i].getType().getName());
				}
				player.sendMessage(ChatColor.BLUE + "X=" + x + " Z=" + z + " Loaded=" + loaded);
				player.sendMessage(ChatColor.GREEN + "Entities in chunk=" + etypes.toString());
			} else if (args.length == 3 && args[0].equalsIgnoreCase("info")) {
				int x = Integer.parseInt(args[1]);
				int z = Integer.parseInt(args[2]);
				World w = player.getWorld();
				Chunk c = w.getChunkAt(x, z);
				boolean loaded = c.isLoaded();
				Entity[] entities = c.getEntities();
				ArrayList<String> etypes = new ArrayList<String>();
				for (int i = 0; i < entities.length; i++) {
					etypes.add(entities[i].getType().getName());
				}
				player.sendMessage(ChatColor.BLUE + "Loaded=" + loaded);
				player.sendMessage(ChatColor.GREEN + "Entities in chunk=" + etypes.toString());
			} else {
				player.sendMessage(ChatColor.DARK_RED + "Use /hcchunk [unload/load/info] (x) (z) (world)");
			}
		} catch (Exception e) {
			player.sendMessage(ChatColor.DARK_RED + "Use /hcchunk [unload/load/info] (x) (z) (world)");
		}
	}
	*/
	
}
