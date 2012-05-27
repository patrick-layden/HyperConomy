package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ChestShop implements Listener{
	
	
	private HyperConomy hc;
	private Transaction tran;
	private Calculation calc;
	private ETransaction ench;
	private Log l;
	private Account acc;
	private Notify not;
	private Economy economy;
	private InfoSign isign;
	private Shop s;
	
	private ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
	
	
	ChestShop() {
		
		HyperObject ho = HyperConomy.hyperobject;
		
		hc = ho.getHyperConomy();
		tran = ho.getTransaction();
		calc = ho.getCalculation();
		ench = ho.getETransaction();
		l = ho.getLog();
		acc = ho.getAccount();
		not = ho.getNotify();
		economy = ho.getEconomy();
		isign = ho.getInfoSign();
		s = ho.getShop();
		
		faces.add(BlockFace.EAST);
		faces.add(BlockFace.WEST);
		faces.add(BlockFace.NORTH);
		faces.add(BlockFace.SOUTH);
		
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
		}
		
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
				String line2 = ChatColor.stripColor(scevent.getLine(1)).trim();
		    	if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
		    		//String line34 = ChatColor.stripColor(scevent.getLine(2)).trim() + ChatColor.stripColor(scevent.getLine(3)).trim();
		    		if (scevent.getPlayer().hasPermission("hyperconomy.chestshop")) {
		    			Block signblock = scevent.getBlock();
		    			int x = signblock.getX();
		    			int y = signblock.getY() - 1;
		    			int z = signblock.getZ();
		    			String world = scevent.getBlock().getWorld().getName();
		    			
		    			BlockState chestblock = Bukkit.getWorld(world).getBlockAt(x, y, z).getState();
		    			if (chestblock instanceof Chest) {
		    				
		    				s.setinShop(scevent.getPlayer());
		    				if (!hc.getYaml().getConfig().getBoolean("config.require-chest-shops-to-be-in-shop") || s.inShop() != -1) {
		    				//scevent.getPlayer().sendMessage(hc.getYaml().getConfig().getBoolean("config.require-chest-shops-to-be-in-shop") + " " + s.inShop() + "");
		    				Chest c = (Chest) chestblock;
		    				int count = 0;
		    				int emptyslots = 0;
		    				while (count < 27) {
		    					if (c.getInventory().getItem(count) == null) {
		    						emptyslots++;
		    					}
		    					count++;
		    				}
		    				if (emptyslots == 27) {
		    					//probably add a check for lockette/deadbolt/lwc chests
		    					
		    					String fline = "";
		    					if (line2.equalsIgnoreCase("[Trade]")) {
		    						fline = "[Trade]";
		    					} else if (line2.equalsIgnoreCase("[Buy]")) {
		    						fline = "[Buy]";
		    					} else if (line2.equalsIgnoreCase("[Sell]")) {
		    						fline = "[Sell]";
		    					}
		    					String pname = scevent.getPlayer().getName();
		    					int nlength = pname.length();
		    					String line3 = "";
		    					String line4 = "";	
		    					if (nlength > 12) {
		    						line3 = pname.substring(0, 11);
		    						line4 = pname.substring(12, pname.length());
		    					} else {
		    						line3 = pname;
		    					}
		    					
		    					scevent.setLine(1, "§b" + fline);
				    			scevent.setLine(2, "§f" + line3);
				    			scevent.setLine(3, "§f" + line4);
		    				} else {
		    					scevent.setLine(0, "§4You must");
		    					scevent.setLine(1, "§4use an");
		    					scevent.setLine(2, "§4empty");
		    					scevent.setLine(3, "§4chest.");
		    				}
		    				
		    			} else {
	    					scevent.setLine(0, "§4You must");
	    					scevent.setLine(1, "§4place your");
	    					scevent.setLine(2, "§4chest shop");
	    					scevent.setLine(3, "§4in a shop.");
		    			}
		    				
		    			} else {
		    				scevent.setLine(1, "");
		    			}
		    		} else {
		    			scevent.setLine(1, "");	
		    		}
			    	if (scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.SIGN_POST) || scevent.getBlock() != null && scevent.getBlock().getType().equals(Material.WALL_SIGN)) {
			    	    Sign s = (Sign) scevent.getBlock().getState();
			    	    s.update();
			    	}													    	    	
			    }

	    }
    }
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreakEvent(BlockBreakEvent bbevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			Block b = bbevent.getBlock();
					
	    	if (b != null && b.getType().equals(Material.WALL_SIGN)) {
	    	    Sign s = (Sign) b.getState();
				String line2 = s.getLine(1).trim();
		    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {
					BlockState chestblock = Bukkit.getWorld(s.getBlock().getWorld().getName()).getBlockAt(s.getX(), s.getY() - 1, s.getZ()).getState();
			    	if (chestblock instanceof Chest) {
			    		if (!(ChatColor.stripColor(s.getLine(2)).trim() + ChatColor.stripColor(s.getLine(3)).trim()).equalsIgnoreCase(bbevent.getPlayer().getName()) && !bbevent.getPlayer().hasPermission("hyperconomy.admin")) {
				    		bbevent.setCancelled(true);
				    		s.update();
				    		return;
			    		} else {
			    			return;
			    		}
			    	}
		    	}
	    	} else if (b.getState() instanceof Chest) {
	    		Chest c = (Chest) b.getState();
				Block signblock = Bukkit.getWorld(c.getBlock().getWorld().getName()).getBlockAt(c.getX(), c.getY() + 1, c.getZ());
				if (signblock != null && signblock.getType().equals(Material.WALL_SIGN)) {
		    		Sign s = (Sign) signblock.getState();
					String line2 = s.getLine(1).trim();
			    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {
			    		bbevent.setCancelled(true);
			    		return;
			    	}
		    	}
	    	} else {	
	    		int count = 0;
	    		while (count < 4) {		
	    			//Gets the blocks around the broken block.
	    			BlockFace cface = faces.get(count);
	            	Block relative = b.getRelative(cface);         	
	            	//If a block surrounding the broken block is a sign with they chestshop keyword it continues.
	            	if (relative.getType().equals(Material.WALL_SIGN)) {
	            		Sign s = (Sign) relative.getState();
	        			String line2 = s.getLine(1).trim();
	        	    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {       	    		   			
	        	    		//Gets the material.Sign version of the sign surrounding the broken block
		        	    	org.bukkit.material.Sign sign = (org.bukkit.material.Sign)relative.getState().getData();
		        	    	BlockFace attachedface = sign.getFacing();
		        	    	if (attachedface == cface) {
		        	    		bbevent.setCancelled(true);
		        	    		return;
		        	    	}	
	        	    	}
	            	}	
	    			count++;
	    		}	     	
	    	}
		}  	
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityExplodeEvent(EntityExplodeEvent eeevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			List<Block> blocks = eeevent.blockList();
			int count = 0;
			while (count < blocks.size()) {
				Block b= blocks.get(count);
		    	if (b != null && b.getType().equals(Material.WALL_SIGN)) {
		    	    Sign s = (Sign) b.getState();
					String line2 = s.getLine(1).trim();
			    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {
						BlockState chestblock = Bukkit.getWorld(s.getBlock().getWorld().getName()).getBlockAt(s.getX(), s.getY() - 1, s.getZ()).getState();
				    	if (chestblock instanceof Chest) {
					    	eeevent.setCancelled(true);
					    	s.update();
					    	return;
				    	}
			    	}
		    	} else if (b.getState() instanceof Chest) {
		    		Chest c = (Chest) b.getState();
					Block signblock = Bukkit.getWorld(c.getBlock().getWorld().getName()).getBlockAt(c.getX(), c.getY() + 1, c.getZ());
					if (signblock != null && signblock.getType().equals(Material.WALL_SIGN)) {
			    		Sign s = (Sign) signblock.getState();
						String line2 = s.getLine(1).trim();
				    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {
				    		eeevent.setCancelled(true);
				    		return;
				    	}
			    	}
		    	} else {	
		    		int count2 = 0;
		    		while (count2 < 4) {		
		    			//Gets the blocks around the broken block.
		    			BlockFace cface = faces.get(count2);
		            	Block relative = b.getRelative(cface);         	
		            	//If a block surrounding the broken block is a sign with they chestshop keyword it continues.
		            	if (relative.getType().equals(Material.WALL_SIGN)) {
		            		Sign s = (Sign) relative.getState();
		        			String line2 = s.getLine(1).trim();
		        	    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {       	    		   			
		        	    		//Gets the material.Sign version of the sign surrounding the broken block
			        	    	org.bukkit.material.Sign sign = (org.bukkit.material.Sign)relative.getState().getData();
			        	    	BlockFace attachedface = sign.getFacing();
			        	    	if (attachedface == cface) {
			        	    		eeevent.setCancelled(true);
			        	    		return;
			        	    	}	
		        	    	}
		            	}	
		    			count2++;
		    		}	     	
		    	}
				count++;
			}
		}
	}
	
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent bpeevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			List<Block> blocks = bpeevent.getBlocks();
			int count = 0;
			while (count < blocks.size()) {
				Block b= blocks.get(count);
		    	if (b != null && b.getType().equals(Material.WALL_SIGN)) {
		    	    Sign s = (Sign) b.getState();
					String line2 = s.getLine(1).trim();
			    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {
						BlockState chestblock = Bukkit.getWorld(s.getBlock().getWorld().getName()).getBlockAt(s.getX(), s.getY() - 1, s.getZ()).getState();
				    	if (chestblock instanceof Chest) {
					    	bpeevent.setCancelled(true);
					    	s.update();
					    	return;
				    	}
			    	}
		    	} else if (b.getState() instanceof Chest) {
		    		Chest c = (Chest) b.getState();
					Block signblock = Bukkit.getWorld(c.getBlock().getWorld().getName()).getBlockAt(c.getX(), c.getY() + 1, c.getZ());
					if (signblock != null && signblock.getType().equals(Material.WALL_SIGN)) {
			    		Sign s = (Sign) signblock.getState();
						String line2 = s.getLine(1).trim();
				    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {
				    		bpeevent.setCancelled(true);
				    		return;
				    	}
			    	}
		    	} else {	
		    		int count2 = 0;
		    		while (count2 < 4) {		
		    			//Gets the blocks around the broken block.
		    			BlockFace cface = faces.get(count2);
		            	Block relative = b.getRelative(cface);         	
		            	//If a block surrounding the broken block is a sign with they chestshop keyword it continues.
		            	if (relative.getType().equals(Material.WALL_SIGN)) {
		            		Sign s = (Sign) relative.getState();
		        			String line2 = s.getLine(1).trim();
		        	    	if (line2.equalsIgnoreCase("§b[Trade]") || line2.equalsIgnoreCase("§b[Buy]") || line2.equalsIgnoreCase("§b[Sell]")) {       	    		   			
		        	    		//Gets the material.Sign version of the sign surrounding the broken block
			        	    	org.bukkit.material.Sign sign = (org.bukkit.material.Sign)relative.getState().getData();
			        	    	BlockFace attachedface = sign.getFacing();
			        	    	if (attachedface == cface) {
			        	    		bpeevent.setCancelled(true);
			        	    		return;
			        	    	}	
		        	    	}
		            	}	
		    			count2++;
		    		}	     	
		    	}
				count++;
			}	
		}
	}
	
	
	
	
	
	
	
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClickEvent(InventoryClickEvent icevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			if (icevent.getInventory().getHolder() instanceof Chest) {
				Chest invchest = (Chest) icevent.getInventory().getHolder();
				int x = invchest.getX();
				int y = invchest.getY() + 1;
				int z = invchest.getZ();
				String world = invchest.getBlock().getWorld().getName();
				BlockState signblock = Bukkit.getWorld(world).getBlockAt(x, y, z).getState();
				if (signblock instanceof Sign) {
					Sign s = (Sign) signblock;
					String line2 = ChatColor.stripColor(s.getLine(1)).trim();
			    	if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
			    		
			    		int slot = icevent.getRawSlot();
			    		
			    		
						boolean buy = false;
						boolean sell = false;
						if (line2.equalsIgnoreCase("[Trade]")) {
							buy = true;
							sell = true;
						} else if (line2.equalsIgnoreCase("[Buy]")) {
							buy = true;
						} else if (line2.equalsIgnoreCase("[Sell]")) {
							sell = true;
						}
			    		
			    		String line34 = ChatColor.stripColor(s.getLine(2)).trim() + ChatColor.stripColor(s.getLine(3)).trim();
			    		String clicker = icevent.getWhoClicked().getName();
			    		//Handles everyone besides the owner of the chest. (make it ! when done testing)
			    		if (!clicker.equalsIgnoreCase(line34)) {
			    			
			    			if (icevent.getCurrentItem() == null) {
				    			icevent.setCancelled(true);
				    			return;
			    			}

			    			
			    			if (icevent.isShiftClick()) {
			    				
			    				Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
				    			ench.setHE(icevent.getCurrentItem());
				    			if (!ench.hasenchants()) {
				    				
					    			String key = icevent.getCurrentItem().getTypeId() + ":" + icevent.getCurrentItem().getData().getData();
				    				String name = hc.getnameData(key);
				    				int id = icevent.getCurrentItem().getTypeId();
				    				int data =  icevent.getCurrentItem().getData().getData();
				    				int camount = icevent.getCurrentItem().getAmount();
				    				
					    			if (slot < 27 && name != null) {
					    				
					    				if (buy) {
							    			tran.setChestShop(hc, id, data, camount, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getTopInventory());
							    			tran.buyChest(line34);
					    				} else {
					    					p.sendMessage(ChatColor.BLUE + "You cannot purchase items from this chest.");
					    				}

					    			} else if (slot >= 27 && name != null){
					    				
					    				if (sell) {
					    					//tran.setAddRemoveItems(hc, id, data, camount, calc, ench, icevent.getView().getTopInventory());
					    					tran.setChestShop(hc, id, data, camount, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getTopInventory());
					    					int itemamount = tran.countItems();
					    					
					    					if (itemamount > 0) {
						    					//tran.setAddRemoveItems(hc, id, data, camount, calc, ench, icevent.getView().getBottomInventory());
					    						tran.setChestShop(hc, id, data, camount, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getBottomInventory());
							    				int space = tran.getSpace();
							    				if (space >= camount) {
						    						double bal = acc.getBalance(line34);
						    						calc.setVC(hc, null, camount, name, ench);
						    						double cost = calc.getTvalue();
						    						if (bal >= cost) {
								    					//tran.setAddRemoveItems(hc, id, data, camount, calc, ench, icevent.getView().getTopInventory());
									    				//tran.addItems();
										    			//tran.setAll(hc, id, data, camount, name, p, economy, calc, ench, l, acc, not, isign);
						    							tran.setChestShop(hc, id, data, camount, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getTopInventory());
										    			tran.sellChest(line34);
						    						} else {
						    							p.sendMessage(ChatColor.BLUE + line34 + " doesn't have enough money for this transaction.");
						    						}
							    				} else {
							    					p.sendMessage(ChatColor.BLUE + "You don't have enough space.");
							    				}
					    					} else {
					    						p.sendMessage(ChatColor.BLUE + "This chest will not accept that item.");
					    					}
		
						    				
					    				} else {
					    					p.sendMessage(ChatColor.BLUE + "You cannot sell items to this chest.");
					    				}
					    				
					    				
					    			}
				    			}

				    			icevent.setCancelled(true);
				    			return;
				    			
				    			
				    		} else if (icevent.isLeftClick()) {
				    			Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
				    			ench.setHE(icevent.getCurrentItem());
				    			if (!ench.hasenchants()) {
					    			String key = icevent.getCurrentItem().getTypeId() + ":" + icevent.getCurrentItem().getData().getData();
				    				String name = hc.getnameData(key);
				    				int id = icevent.getCurrentItem().getTypeId();
				    				int data =  icevent.getCurrentItem().getData().getData();
				    				
					    			if (slot < 27 && name != null) {
					    				
					    				if (buy) {
							    			calc.setVC(hc, null, 1, name, null);
							    			p.sendMessage("§0-----------------------------------------------------");
							    			p.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "1 " + ChatColor.AQUA + "" + ChatColor.ITALIC + name + ChatColor.BLUE + ChatColor.ITALIC + " can be purchased from " + Bukkit.getPlayer(line34).getName() + " for: " + ChatColor.GREEN + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + calc.getTvalue());
							    			p.sendMessage("§0-----------------------------------------------------");
					    				} else {
					    					p.sendMessage(ChatColor.BLUE + "You cannot buy items from this chest.");
					    				}
		
						    			
					    			} else if (slot >= 27 && name != null) {
					    				
					    				if (sell) {
					    					//tran.setAddRemoveItems(hc, id, data, 1, calc, ench, icevent.getView().getTopInventory());
					    					tran.setChestShop(hc, id, data, 1, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getTopInventory());
					    					int itemamount = tran.countItems();
					    					
					    					if (itemamount > 0) {
					    						calc.setVC(hc, null, 1, name, null);
							    				p.sendMessage("§0-----------------------------------------------------");
							    				p.sendMessage(ChatColor.GREEN + "" + ChatColor.ITALIC + "1 "  + ChatColor.AQUA + ""  + ChatColor.ITALIC + name + ChatColor.BLUE + ChatColor.ITALIC + " can be sold to " + Bukkit.getPlayer(line34).getName() + " for: " + ChatColor.GREEN + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + calc.getTvalue());
							    				p.sendMessage("§0-----------------------------------------------------");	
					    					} else {
					    						p.sendMessage(ChatColor.BLUE + "This chest will not accept that item.");
					    					}
					    				} else {
					    					p.sendMessage(ChatColor.BLUE + "You cannot sell items to this chest.");
					    				}
		
					    			}
				    			} else {

				        			String key = icevent.getCurrentItem().getTypeId() + ":" + icevent.getCurrentItem().getData().getData();
					    				String name = hc.getnameData(key);
					    				
						    			if (slot < 27 && name != null) {
						    				
						    				if (buy) {
			
						    						double price = 0;
							    					Iterator<Enchantment> ite = icevent.getCurrentItem().getEnchantments().keySet().iterator();
							        				while (ite.hasNext()) {;
							        					String rawstring = ite.next().toString();
							        					String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
							        					Enchantment en = null;
							        					en = Enchantment.getByName(enchname);
							        					int lvl = icevent.getCurrentItem().getEnchantmentLevel(en);
							        					String nam = hc.getenchantData(enchname);
							        					String fnam = nam + lvl;
							        					ench.setVC(hc, fnam, p.getItemInHand().getType().toString(), calc);
							        					price = price + ench.getValue();
							        				}
							        				price = calc.twoDecimals(price);
							        			if (ench.isEnchantable(p.getItemInHand())) {
									    			p.sendMessage("§0-----------------------------------------------------");
									    			p.sendMessage(ChatColor.BLUE + "The selected item's enchantments can be purchased from " + Bukkit.getPlayer(line34).getName() + " for: " + ChatColor.GREEN + ChatColor.ITALIC + hc.getYaml().getConfig().getString("config.currency-symbol") + price);
									    			p.sendMessage("§0-----------------------------------------------------");
						    					} else {
						    						p.sendMessage(ChatColor.BLUE + "That item cannot accept enchantments.");
						    					}
						    					

						    				} else {
						    					p.sendMessage(ChatColor.BLUE + "You cannot purchase enchantments from this chest.");
						    				}
			
						    				
						    			} else if (slot >= 27 && name != null) {
						    				
						    				p.sendMessage(ChatColor.BLUE + "You cannot sell enchantments here.");
						    				
						    			}
				    			}

				    			icevent.setCancelled(true);
				    			return;
				    		} else if (icevent.isRightClick()) {
				    			Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
				    			ench.setHE(icevent.getCurrentItem());
				    			if (!ench.hasenchants()) {
				        			String key = icevent.getCurrentItem().getTypeId() + ":" + icevent.getCurrentItem().getData().getData();
				    				String name = hc.getnameData(key);
				    				int id = icevent.getCurrentItem().getTypeId();
				    				int data =  icevent.getCurrentItem().getData().getData();
				    				
					    			if (slot < 27 && name != null) {
					    				
					    				if (buy) {
					    					tran.setChestShop(hc, id, data, 1, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getTopInventory());
						    				//tran.setAddRemoveItems(hc, id, data, 1, calc, ench, icevent.getView().getTopInventory());
						    				//tran.removeItems();
							    			//tran.setAll(hc, id, data, 1, name, p, economy, calc, ench, l, acc, not, isign);
							    			tran.buyChest(line34);
					    				} else {
					    					p.sendMessage(ChatColor.BLUE + "You cannot buy items from this chest.");
					    				}
		
					    				
					    			} else if (slot >= 27 && name != null) {
					    				
					    				if (sell) {
					    					tran.setChestShop(hc, id, data, 1, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getTopInventory());
					    					//tran.setAddRemoveItems(hc, id, data, 1, calc, ench, icevent.getView().getTopInventory());
					    					int itemamount = tran.countItems();
					    					
					    					if (itemamount > 0) {
					    						tran.setChestShop(hc, id, data, 1, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getBottomInventory());
					    						//tran.setAddRemoveItems(hc, id, data, 1, calc, ench, icevent.getView().getBottomInventory());
						    					int space = tran.getSpace();
						    					if (space >= 1) {
						    						double bal = acc.getBalance(line34);
						    						calc.setVC(hc, null, 1, name, ench);
						    						double cost = calc.getTvalue();
						    						if (bal >= cost) {
						    							tran.setChestShop(hc, id, data, 1, name, p, economy, calc, ench, l, acc, not, isign, icevent.getView().getTopInventory());
							    						//tran.setAddRemoveItems(hc, id, data, 1, calc, ench, icevent.getView().getTopInventory());
								    					//tran.addItems();
									    				//tran.setAll(hc, id, data, 1, name, p, economy, calc, ench, l, acc, not, isign);
									    				tran.sellChest(line34);
						    						} else {
						    							p.sendMessage(ChatColor.BLUE + line34 + " doesn't have enough money for this transaction.");
						    						}
						    					} else {
						    						p.sendMessage(ChatColor.BLUE + "You don't have enough space.");
						    					}
					    					} else {
					    						p.sendMessage(ChatColor.BLUE + "This chest will not accept that item.");
					    					}
					    				} else {
					    					p.sendMessage(ChatColor.BLUE + "You cannot sell items to this chest.");
					    				}
		
		
					    				
					    			}
				    			} else {
				    				
				    				
				    				
				    				
				        			String key = icevent.getCurrentItem().getTypeId() + ":" + icevent.getCurrentItem().getData().getData();
					    				String name = hc.getnameData(key);
					    				
						    			if (slot < 27 && name != null) {
						    				
						    				if (buy) {					    					
						    					Iterator<Enchantment> ite = icevent.getCurrentItem().getEnchantments().keySet().iterator();
						        				while (ite.hasNext()) {;
						        					String rawstring = ite.next().toString();
						        					String enchname = rawstring.substring(rawstring.indexOf(",") + 2, rawstring.length() - 1);
						        					Enchantment en = null;
						        					en = Enchantment.getByName(enchname);
						        					int lvl = icevent.getCurrentItem().getEnchantmentLevel(en);
						        					String nam = hc.getenchantData(enchname);
						        					String fnam = nam + lvl;
						        					ench.setSBE(hc, p, fnam, economy, l, acc, isign, not, calc);
						        					ench.buyChestEnchant(icevent.getCurrentItem(), line34);
						        				}

						    				} else {
						    					p.sendMessage(ChatColor.BLUE + "You cannot buy items from this chest.");
						    				}
			
						    				
						    			} else if (slot >= 27 && name != null) {
						    				
						    				p.sendMessage(ChatColor.BLUE + "You cannot sell enchantments here.");
						    				
						    			}
				    				
				    				
				    				
				    				
				    				
				    				
				    				
				    			}
				
				    			
				    			
				    			icevent.setCancelled(true);
				    			return;
				    		}
			    		}		
			    	}					
				}
			}		
		}		
	}

	
	
	
	
	
	
	

}
