package regalowl.hyperconomy;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ChestShop implements Listener{
	
	
	private HyperConomy hc;
	private Calculation calc;
	private Account acc;
	private ShopFactory s;
	private LanguageFile L;
	private DataHandler dh;
	private InventoryManipulation im;
	
	private ArrayList<BlockFace> faces = new ArrayList<BlockFace>();
	private ArrayList<BlockFace> allfaces = new ArrayList<BlockFace>();
	
	ChestShop() {
		
		hc = HyperConomy.hc;
		im = hc.getInventoryManipulation();
		dh = hc.getDataFunctions();
		calc = hc.getCalculation();
		acc = hc.getAccount();
		s = hc.getShopFactory();
		L = hc.getLanguageFile();
		
		faces.add(BlockFace.EAST);
		faces.add(BlockFace.WEST);
		faces.add(BlockFace.NORTH);
		faces.add(BlockFace.SOUTH);
		
		
		allfaces.add(BlockFace.EAST);
		allfaces.add(BlockFace.WEST);
		allfaces.add(BlockFace.NORTH);
		allfaces.add(BlockFace.SOUTH);
		allfaces.add(BlockFace.DOWN);
		allfaces.add(BlockFace.UP);
		
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			hc.getServer().getPluginManager().registerEvents(this, hc);
		}
		
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChangeEvent(SignChangeEvent scevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
				String line2 = ChatColor.stripColor(scevent.getLine(1)).trim();
		    	if (line2.equalsIgnoreCase("[Trade]") || line2.equalsIgnoreCase("[Buy]") || line2.equalsIgnoreCase("[Sell]")) {
		    		if (scevent.getPlayer().hasPermission("hyperconomy.chestshop")) {
		    			Block signblock = scevent.getBlock();
	        	    	org.bukkit.material.Sign msign = (org.bukkit.material.Sign)signblock.getState().getData();
	        	    	BlockFace attachedface = msign.getAttachedFace();
	        	    	Block attachedblock = signblock.getRelative(attachedface);
	        	    	Material am = attachedblock.getType();
	        	    	
		    			
		    			BlockState chestblock = signblock.getRelative(BlockFace.DOWN).getState();
		    			if (chestblock instanceof Chest) {
		    				Block cblock = chestblock.getBlock();
		    				BlockState pchest1 = cblock.getRelative(BlockFace.EAST).getState();
		    				BlockState pchest2 = cblock.getRelative(BlockFace.WEST).getState();
		    				BlockState pchest3 = cblock.getRelative(BlockFace.NORTH).getState();
		    				BlockState pchest4 = cblock.getRelative(BlockFace.SOUTH).getState();
		    				
		    				if (!(pchest1 instanceof Chest) && !(pchest2 instanceof Chest) && !(pchest3 instanceof Chest) && !(pchest4 instanceof Chest)) {
			    				if (!hc.getYaml().getConfig().getBoolean("config.require-chest-shops-to-be-in-shop") || s.inAnyShop(scevent.getPlayer())) {    				
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
			    					
				    					if (am == Material.ICE || am == Material.LEAVES || am == Material.SAND || am == Material.GRAVEL || am == Material.SIGN || am == Material.SIGN_POST || am == Material.TNT) {
				    						
					    					scevent.setLine(0, "\u00A74You can't");
					    					scevent.setLine(1, "\u00A74attach your");
					    					scevent.setLine(2, "\u00A74sign to that");
					    					scevent.setLine(3, "\u00A74block!");
	
				    					} else {
					    					//probably add a check for lockette/deadbolt/lwc chests
				    						
				    						String line1 = scevent.getLine(0);
				    						if (line1.startsWith(HyperConomy.currency)) {
				    							try {
				    								String price = line1.substring(1, line1.length());
				    								Double.parseDouble(price);
				    								scevent.setLine(0, "\u00A7a$" + price);
				    							} catch (Exception e) {
				    								scevent.setLine(0, "");
				    							}
				    						} else {
				    							try {
				    								String price = line1.substring(0, line1.length());
				    								Double.parseDouble(price);
				    								scevent.setLine(0, "\u00A7a$" + price);
				    							} catch (Exception e) {
					    							try {
					    								String price = line1.substring(0, line1.length() - 1);
					    								Double.parseDouble(price);
					    								scevent.setLine(0, "\u00A7a$" + price);
					    							} catch (Exception e2) {
					    								scevent.setLine(0, "");
					    							}
				    							}
				    						}
					    					
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
					    						line3 = pname.substring(0, 12);
					    						line4 = pname.substring(12, pname.length());
					    					} else {
					    						line3 = pname;
					    					}
					    					
					    					scevent.setLine(1, "\u00A7b" + fline);
							    			scevent.setLine(2, "\u00A7f" + line3);
							    			scevent.setLine(3, "\u00A7f" + line4);
				    					}

				    				} else {
				    					scevent.setLine(0, "\u00A74You must");
				    					scevent.setLine(1, "\u00A74use an");
				    					scevent.setLine(2, "\u00A74empty");
				    					scevent.setLine(3, "\u00A74chest.");
				    				}
			    				
				    			} else {
			    					scevent.setLine(0, "\u00A74You must");
			    					scevent.setLine(1, "\u00A74place your");
			    					scevent.setLine(2, "\u00A74chest shop");
			    					scevent.setLine(3, "\u00A74in a shop.");
				    			}
		    				} else {
		    					scevent.setLine(0, "\u00A74You can't");
		    					scevent.setLine(1, "\u00A74use a");
		    					scevent.setLine(2, "\u00A74double");
		    					scevent.setLine(3, "\u00A74chest.");
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
		    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
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
			    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
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
	        	    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {       	    		   			
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
			    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
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
				    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
				    		eeevent.setCancelled(true);
				    		return;
				    	}
			    	}
		    	} else {	
		    		int count2 = 0;
		    		while (count2 < 4) {		
		    			BlockFace cface = faces.get(count2);
		            	Block relative = b.getRelative(cface);         	
		            	if (relative.getType().equals(Material.WALL_SIGN)) {
		            		Sign s = (Sign) relative.getState();
		        			String line2 = s.getLine(1).trim();
		        	    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {       	    		   			
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
			    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
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
				    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
				    		bpeevent.setCancelled(true);
				    		return;
				    	}
			    	}
		    	} else {	
		    		int count2 = 0;
		    		while (count2 < 4) {		
		    			BlockFace cface = faces.get(count2);
		            	Block relative = b.getRelative(cface);         	
		            	if (relative.getType().equals(Material.WALL_SIGN)) {
		            		Sign s = (Sign) relative.getState();
		        			String line2 = s.getLine(1).trim();
		        	    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {       	    		   			
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
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent bprevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			Location l = bprevent.getRetractLocation();
			Block b = l.getBlock();
			int count = 0;
			while (count < 4) {
				BlockFace cface = faces.get(count);
				Block relative = b.getRelative(cface);
				if (relative.getType().equals(Material.WALL_SIGN)) {
					Sign s = (Sign) relative.getState();
					String line2 = s.getLine(1).trim();
					if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
						org.bukkit.material.Sign sign = (org.bukkit.material.Sign) relative.getState().getData();
						BlockFace attachedface = sign.getFacing();
						if (attachedface == cface) {
							bprevent.setCancelled(true);
							return;
						}
					}
				}
				count++;
			}
		}
	}
	
	
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlaceEvent(BlockPlaceEvent bpevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			Block b = bpevent.getBlock();
			if (b.getState() instanceof Chest) {
	    		int count = 0;
	    		while (count < 4) {		
	    			BlockFace cface = faces.get(count);
	            	Block relative = b.getRelative(cface);         	
	            	if (relative.getState() instanceof Chest) {
	            		Block signblock = relative.getRelative(BlockFace.UP);
						if (signblock != null && signblock.getType().equals(Material.WALL_SIGN)) {
				    		Sign s = (Sign) signblock.getState();
							String line2 = s.getLine(1).trim();
					    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
					    		bpevent.setCancelled(true);
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
	public void onBlockPlaceEventTekkit(BlockPlaceEvent bpevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			Player p = bpevent.getPlayer();
			Block block = bpevent.getBlock();
			for (BlockFace bf:allfaces) {
				Block b = block.getRelative(bf);
				if (b.getState() instanceof Chest) {
		    		Chest c = (Chest) b.getState();
					Block signblock = Bukkit.getWorld(c.getBlock().getWorld().getName()).getBlockAt(c.getX(), c.getY() + 1, c.getZ());
					if (signblock != null && signblock.getType().equals(Material.WALL_SIGN)) {
			    		Sign s = (Sign) signblock.getState();
						String line2 = s.getLine(1).trim();
				    	if (line2.equalsIgnoreCase("\u00A7b[Trade]") || line2.equalsIgnoreCase("\u00A7b[Buy]") || line2.equalsIgnoreCase("\u00A7b[Sell]")) {
				    		String line34 = ChatColor.stripColor(s.getLine(2)).trim() + ChatColor.stripColor(s.getLine(3)).trim();
				    		if (!line34.equalsIgnoreCase(p.getName())) {
					    		bpevent.setCancelled(true);
				    		}
				    	}
			    	}
		    	}
			}
		}
	}
	
	
	
	
	
	
	
	@EventHandler(priority = EventPriority.LOW)
	public void onInventoryClickEvent(InventoryClickEvent icevent) {
		if (hc.getYaml().getConfig().getBoolean("config.use-chest-shops")) {
			if (!hc.isLocked()) { 
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

				    			boolean setprice = false;
				    			double staticprice = 0.0;
				    			String line1 = ChatColor.stripColor(s.getLine(0)).trim();
	    						if (line1.startsWith(HyperConomy.currency)) {
	    							try {
	    								String price = line1.substring(1, line1.length());
	    								staticprice = calc.twoDecimals(Double.parseDouble(price));
	    								setprice = true;
	    							} catch (Exception e) {
	    								setprice = false;
	    							}
	    						}
				    			if (icevent.isShiftClick()) {
				    				
				    				Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
				    				HyperPlayer hp = dh.getHyperPlayer(p);
					    			if (!im.hasenchants(icevent.getCurrentItem())) {
					    				
					    				HyperObject ho = dh.getHyperObject(icevent.getCurrentItem().getTypeId(), icevent.getCurrentItem().getDurability(), hp.getEconomy());
					    				int id = icevent.getCurrentItem().getTypeId();
					    				int data =  icevent.getCurrentItem().getDurability();
					    				int camount = icevent.getCurrentItem().getAmount();
					    				
						    			if (slot < 27 && ho != null) {
						    				if (buy) {
						    					/*
								    			if (setprice) {
								    				tran.buyChest(name, id, data, line34, p, camount, icevent.getView().getTopInventory(), calc.twoDecimals((camount * staticprice)));
								    			} else {
								    				tran.buyChest(name, id, data, line34, p, camount, icevent.getView().getTopInventory());
								    			}
								    			*/
												PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
												pt.setHyperObject(ho);
												pt.setTradePartner(dh.getHyperPlayer(line34));
												pt.setAmount(camount);
												pt.setGiveInventory(icevent.getView().getTopInventory());
								    			if (setprice) {
								    				pt.setMoney(calc.twoDecimals((camount * staticprice)));
								    			}
												TransactionResponse response = hp.processTransaction(pt);
												response.sendMessages();
						    				} else {
						    					p.sendMessage(L.get("CANNOT_PURCHASE_ENCHANTMENTS_FROM_CHEST"));
						    				}

						    			} else if (slot >= 27 && ho != null){
						    				if (sell) {
						    					int itemamount = im.countItems(id, data, icevent.getView().getTopInventory());
						    					
						    					if (itemamount > 0) {
								    				int space = im.getAvailableSpace(id, data, icevent.getView().getTopInventory());
								    				if (space >= camount) {
								    					if (acc.checkAccount(line34)) {
								    						double bal = acc.getBalance(line34);
								    						double cost = ho.getValue(camount);
								    						if (setprice) {
								    							cost = staticprice * camount;
								    						}
								    						
								    						if (bal >= cost) {
								    							/*
												    			if (setprice) {
												    				tran.sellChest(name, id, data, camount, line34, p, icevent.getView().getTopInventory(), calc.twoDecimals(cost));
												    			} else {
												    				tran.sellChest(name, id, data, camount, line34, p, icevent.getView().getTopInventory());
												    			}
												    			*/
																PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
																pt.setHyperObject(ho);
																pt.setTradePartner(dh.getHyperPlayer(line34));
																pt.setAmount(camount);
																pt.setReceiveInventory(icevent.getView().getTopInventory());
												    			if (setprice) {
												    				pt.setMoney(calc.twoDecimals(cost));
												    			}
																TransactionResponse response = hp.processTransaction(pt);
																response.sendMessages();
								    						} else {
								    							//p.sendMessage(ChatColor.BLUE + line34 + " doesn't have enough money for this transaction.");
								    							L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), line34);
								    						}
								    					}
								    				} else {
								    					p.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
								    				}
						    					} else {
						    						p.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						    					}
			
							    				
						    				} else {
						    					p.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
						    				}
						    				
						    				
						    			}
					    			}
					    			icevent.setCancelled(true);
					    			return;
					    			
					    			
					    		} else if (icevent.isLeftClick()) {
					    			Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
					    			HyperPlayer hp = dh.getHyperPlayer(p);
					    			if (!im.hasenchants(icevent.getCurrentItem())) {
					    				HyperObject ho = dh.getHyperObject(icevent.getCurrentItem().getTypeId(), icevent.getCurrentItem().getDurability(), hp.getEconomy());
					    				int id = icevent.getCurrentItem().getTypeId();
					    				int data =  icevent.getCurrentItem().getDurability();
					    				
						    			if (slot < 27 && ho != null) {
						    				String name = ho.getName();
						    				if (buy) {
						    					double price = ho.getValue(1);
						    					if (setprice) {
						    						price = staticprice;
						    					}
								    			p.sendMessage(L.get("LINE_BREAK"));
								    			p.sendMessage(L.f(L.get("CHEST_SHOP_BUY_VALUE"), 1, price, name, line34));
								    			p.sendMessage(L.get("LINE_BREAK"));
						    				} else {
						    					p.sendMessage(L.get("CANNOT_PURCHASE_ITEMS_FROM_CHEST"));
						    				}
			
							    			
						    			} else if (slot >= 27 && ho != null) {
						    				String name = ho.getName();
						    				if (sell) {
						    					int itemamount = im.countItems(id, data, icevent.getView().getTopInventory());
						    					
						    					if (itemamount > 0) {
						    						double price = ho.getValue(1);
							    					if (setprice) {
							    						price = staticprice;
							    					}
								    				p.sendMessage(L.get("LINE_BREAK"));
								    				p.sendMessage(L.f(L.get("CHEST_SHOP_SELL_VALUE"), 1, price, name, line34));
								    				p.sendMessage(L.get("LINE_BREAK"));	
						    					} else {
						    						p.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						    					}
						    				} else {
						    					p.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
						    				}
			
						    			}
					    			} else {

					    				//HyperObject ho = dh.getHyperObject(icevent.getCurrentItem().getTypeId(), icevent.getCurrentItem().getDurability(), hp.getEconomy());
						    				
							    			if (slot < 27) {
							    				
							    				if (buy) {
				
							    						double price = 0;
								    					for (Enchantment enchantment : im.listEnchantments(icevent.getCurrentItem())) {
								    						int lvl = im.getEnchantmentLevel(icevent.getCurrentItem(), enchantment);
								    						String nam = dh.getEnchantNameWithoutLevel(enchantment.getName());
								        					String fnam = nam + lvl;
								        					HyperObject ho = dh.getHyperObject(fnam, hp.getEconomy());
								        					price += ho.getValue(EnchantmentClass.fromString(p.getItemInHand().getType().name()));
									    					if (setprice) {
									    						price = staticprice;
									    					}
								    					}
								        				price = calc.twoDecimals(price);
								        			if (im.canEnchantItem(p.getItemInHand())) {
										    			p.sendMessage(L.get("LINE_BREAK"));
										    			p.sendMessage(L.f(L.get("CHEST_SHOP_ENCHANTMENT_VALUE"), price, line34));
										    			p.sendMessage(L.get("LINE_BREAK"));
							    					} else {
							    						p.sendMessage(L.get("ITEM_CANNOT_ACCEPT_ENCHANTMENTS"));
							    					}
							    					

							    				} else {
							    					p.sendMessage(L.get("CANNOT_PURCHASE_ENCHANTMENTS_FROM_CHEST"));
							    				}
				
							    				
							    			} else {
							    				
							    				p.sendMessage(L.get("CANNOT_SELL_ENCHANTMENTS_HERE"));
							    				
							    			}
					    			}

					    			icevent.setCancelled(true);
					    			return;
					    		} else if (icevent.isRightClick()) {
					    			Player p = Bukkit.getPlayer(icevent.getWhoClicked().getName());
					    			HyperPlayer hp = dh.getHyperPlayer(p);
					    			if (!im.hasenchants(icevent.getCurrentItem())) {
					    				HyperObject ho = dh.getHyperObject(icevent.getCurrentItem().getTypeId(), icevent.getCurrentItem().getDurability(), hp.getEconomy());
					    				
					    				int id = icevent.getCurrentItem().getTypeId();
					    				int data =  icevent.getCurrentItem().getDurability();
					    				
						    			if (slot < 27 && ho != null) {
						    				if (buy) {
						    					/*
						    					if (setprice) {
						    						tran.buyChest(name, id, data, line34, p, 1, icevent.getView().getTopInventory(), staticprice);
						    					} else {
						    						tran.buyChest(name, id, data, line34, p, 1, icevent.getView().getTopInventory());
						    					}
						    					*/
												PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_INVENTORY);
												pt.setHyperObject(ho);
												pt.setTradePartner(dh.getHyperPlayer(line34));
												pt.setAmount(1);
												pt.setGiveInventory(icevent.getView().getTopInventory());
								    			if (setprice) {
								    				pt.setMoney(staticprice);
								    			}
												TransactionResponse response = hp.processTransaction(pt);
												response.sendMessages();
								    			
						    				} else {
						    					p.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
						    				}
			
						    				
						    			} else if (slot >= 27 && ho != null) {
						    				if (sell) {
						    					int itemamount = im.countItems(id, data, icevent.getView().getTopInventory());
						    					
						    					if (itemamount > 0) {
							    					int space = im.getAvailableSpace(id, data, icevent.getView().getTopInventory());
							    					if (space >= 1) {
							    						if (acc.checkAccount(line34)) {
								    						double bal = acc.getBalance(line34);
								    						double cost = ho.getValue(1);
								    						if (setprice) {
								    							cost = staticprice;
								    						}
								    						if (bal >= cost) {
								    							/*
								    							if (setprice) {
								    								tran.sellChest(name, id, data, 1, line34, p, icevent.getView().getTopInventory(), cost);
								    							} else {
								    								tran.sellChest(name, id, data, 1, line34, p, icevent.getView().getTopInventory());
								    							}
								    							*/
																PlayerTransaction pt = new PlayerTransaction(TransactionType.SELL_TO_INVENTORY);
																pt.setHyperObject(ho);
																pt.setTradePartner(dh.getHyperPlayer(line34));
																pt.setAmount(1);
																pt.setReceiveInventory(icevent.getView().getTopInventory());
												    			if (setprice) {
												    				pt.setMoney(cost);
												    			}
																TransactionResponse response = hp.processTransaction(pt);
																response.sendMessages();
								    						} else {
								    							//p.sendMessage(ChatColor.BLUE + line34 + " doesn't have enough money for this transaction.");
								    							p.sendMessage(L.f(L.get("PLAYER_DOESNT_HAVE_ENOUGH_MONEY"), line34));
								    						}
							    						}
							    					} else {
							    						p.sendMessage(L.get("CHEST_SHOP_NOT_ENOUGH_SPACE"));
							    					}
						    					} else {
						    						p.sendMessage(L.get("CHEST_WILL_NOT_ACCEPT_ITEM"));
						    					}
						    				} else {
						    					p.sendMessage(L.get("CANNOT_SELL_ITEMS_TO_CHEST"));
						    				}
			
			
						    				
						    			}
					    			} else {				    				
							    			if (slot < 27) {
							    				if (buy) {	
							    					for (Enchantment enchantment : im.listEnchantments(icevent.getCurrentItem())) {
							    						int lvl = im.getEnchantmentLevel(icevent.getCurrentItem(), enchantment);
							    						String nam = dh.getEnchantNameWithoutLevel(enchantment.getName());
							        					String fnam = nam + lvl;
							        					HyperObject ho = dh.getHyperObject(fnam, hp.getEconomy());
														PlayerTransaction pt = new PlayerTransaction(TransactionType.BUY_FROM_ITEM);
														pt.setHyperObject(ho);
														pt.setTradePartner(dh.getHyperPlayer(line34));
														pt.setGiveItem(icevent.getCurrentItem());
										    			if (setprice) {
										    				pt.setMoney(staticprice);
										    			}
														TransactionResponse response = hp.processTransaction(pt);
														response.sendMessages();
							    					}
							    				} else {
							    					p.sendMessage(L.get("CANNOT_BUY_ITEMS_FROM_CHEST"));
							    				}
							    			} else if (slot >= 27) {
							    				p.sendMessage(L.get("CANNOT_SELL_ENCHANTMENTS_HERE"));
							    			}
					    			}
					
					    			
					    			
					    			icevent.setCancelled(true);
					    			return;
					    		}
				    		}		
				    	}					
					}
				}	
			} else {
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
					    		Bukkit.getPlayer(icevent.getWhoClicked().getName()).sendMessage(L.get("GLOBAL_SHOP_LOCKED"));
					    		icevent.setCancelled(true);
					    	}
						}
					}
				}
			}
		}		
	}

	
	
	
	
	
	
	

}
