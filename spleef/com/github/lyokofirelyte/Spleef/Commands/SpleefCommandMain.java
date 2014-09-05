package com.github.lyokofirelyte.Spleef.Commands;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.github.lyokofirelyte.Spleef.Spleef;
import static com.github.lyokofirelyte.Spleef.SpleefModule.*;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefPlayer;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefPlayerData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefStorage;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class SpleefCommandMain {

	private Spleef main;
	
	public SpleefCommandMain(Spleef i) {
		main = i;
	}

	@SpleefCommand(aliases = {"spleef"}, desc = "Main Spleef Command", help = "/spleef help", player = true, min = 1, perm = "spleef.use")
	public void onSpleef(Player p, String[] args){
		
		SpleefPlayer sender = getSpleefPlayer(p.getUniqueId());
		
		if (args.length == 0){
			onSpleef(p, new String[]{"help"});
			return;
		}
		
		switch (args[0].toLowerCase()){
		
			case "author": case "copyright":
				
				for (String s : new String[]{
					"&oSpleef plugin custom coded for the World Spleef Federation.",
					"Author: &6Hugh_Jasses (lyokofirelyte@gmail.com)",
					"Rights: &6Full usage rights for WSF only.",
					"Source: &6Viewable on request (WSF only)"
				}){
					s(p, s);
				}
				
			break;
		
			case "help":
				
				for (String s : new String[]{
					"/spleef addarena",
					"/spleef remarena <ID>",
					"/spleef arenalist",
					"/spleef p1start <ID>",
					"/spleef p2start <ID>",
					"/spleef spec <player>",
					"/spleef invite <player>",
					"/spleef manreset <ID>",
					"/spleef mat <ID>",
					"/spleef enable/disable <ID>",
					"/spleef score <player>",
					"/spleef leaderboard",
					"/spleef copyright"
				}){
					s(p, s);
				}
				
			break;
			
			case "mat":
				
				if (!perms(p, "spleef.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						getSpleefGame(args[1]).putt(SpleefGameData.MATERIAL, p.getItemInHand().getType().toString());
						sender.s("Changed mat to &6" + p.getItemInHand().getType().toString() + "&b.");
					} else {
						sender.err("That game does not exist.");
					}
				}
				
			break;
			
			case "enable": case "disable":
				
				if (!perms(p, "spleef.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						getSpleefGame(args[1]).setEnabled(Boolean.valueOf(args[0].replace("enable", "true").replace("disable", "false")));
						sender.s("Updated.");
					} else {
						sender.err("No game found by that name.");
					}
				}
				
			break;
			
			case "manreset":
				
				if (!perms(p, "spleef.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						reset(getSpleefGame(args[1]));
					} else {
						sender.err("That game does not exist.");
					}
				}
				
			break;
			
			case "invite":
				
				if (a(sender, args.length, 2)){
					if (doesPlayerExist(args[1])){
						SpleefPlayer them = null;
						if (Bukkit.getPlayer((them = matchSpleefPlayer(args[1])).uuid()) != null){
							if ((them.getInvite() == null || them.getInvite().equals(them)) && !them.inGame()){
								them.setInvite(sender);
								sender.setInvite(sender);
								them.s(p.getDisplayName() + " &bhas invited you to spleef. Type &6/spleef accept &bor &6/spleef deny&b.");
								sender.s("Sent invite!");
							} else {
								sender.err("They already have an invite.");
							}
						} else {
							sender.err("That player is offline.");
						}
					} else {
						sender.err("That player does not exist!");
					}
				}
				
			break;
			
			case "accept":
				
				if (sender.getInvite() != null && !sender.getInvite().equals(sender)){
					if (Bukkit.getPlayer(sender.getInvite().uuid()) != null){
						for (SpleefGame game : getAllGames()){
							if (game.involvedPlayers().size() <= 0 && game.isEnabled()){
								Bukkit.getPlayer(sender.uuid()).getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE));
								Bukkit.getPlayer(sender.getInvite().uuid()).getInventory().addItem(new ItemStack(Material.DIAMOND_SPADE));
								game.involvedPlayers().add(sender);
								game.involvedPlayers().add(sender.getInvite());
								sender.setCurrentGame(game);
								sender.getInvite().setCurrentGame(game);
								sender.setInGame(true);
								sender.getInvite().setInGame(true);
								sender.setOpponent(sender.getInvite());
								sender.getInvite().setOpponent(sender);
								sender.getInvite().setInvite(sender.getInvite());
								sender.setInvite(sender);
								game.teleportPlayers();
								return;
							}
						}
						sender.err("No games are open at the moment!");
					} else {
						sender.err("They're offline. Invite incinerated.");
						sender.getInvite().setInvite(sender.getInvite());
						sender.setInvite(sender);
					}
				} else {
					sender.err("You don't have any invites.");
				}
				
			break;
			
			case "deny":
				
				if (!sender.inGame()){
					sender.getInvite().err("Invite denied.");
					sender.getInvite().setInvite(sender.getInvite());
					sender.setInvite(sender);
					sender.s("Denied!");
				}
				
			break;
			
			case "arenalist":
				
				if (!perms(p, "spleef.admin")){ return; }
				
				String list = "";
				
				for (SpleefStorage game : getManager().data.values()){
					if (game.type().equals(SpleefDataType.GAME)){
						list = list +( list.equals("") ? "&b" + getManager().stripDirName(game.name()) : "&7, &b" + getManager().stripDirName(game.name()));
					}
				}
				
				sender.s(list);
				
			break;
			
			case "score":
				
				if (a(sender, args.length, 2)){
					if (doesPlayerExist(args[1])){
						try {
							sender.s("Total Score: " + (int) matchSpleefPlayer(args[1]).gett(SpleefPlayerData.TOTAL_SCORE));
							sender.s("Game Wins: " + (int) matchSpleefPlayer(args[1]).gett(SpleefPlayerData.TOTAL_WINS));
							sender.s("Game Losses: " + (int) matchSpleefPlayer(args[1]).gett(SpleefPlayerData.TOTAL_LOSSES));
						} catch (Exception e){
							sender.s("No score found!");
						}
					} else {
						sender.err("That player does not exist.");
					}
				}
				
			break;
			
			case "setlobby":
				
				if (!perms(p, "spleef.admin")){ return; }
				
				Location l = p.getLocation();
				getSpleefSystem().setLobby(new Location(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ(), l.getYaw(), l.getPitch()));
				sender.s("Set lobby!");
				
			break;
			
			case "p1start": case "p2start":
				
				if (!perms(p, "spleef.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						l = p.getLocation();
						getSpleefGame(args[1]).putt((args[0].contains("p1") ? SpleefGameData.PLAYER_START_1 : SpleefGameData.PLAYER_START_2), l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
						sender.s("Updated &6" + args[0] + "&b!");
					} else {
						sender.err("That game does not exist.");
					}
				}
				
			break;
			
			case "addarena":
				
				if (!perms(p, "spleef.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					
					if (!doesGameExist(args[1])){
				
						if (getWorldEdit().getSelection(p) != null && getWorldEdit().getSelection(p) instanceof CuboidSelection){
							Selection sel = getWorldEdit().getSelection(p);
							
							if (sel.getHeight() <= 1){
								
								Vector max = sel.getMaximumPoint().toVector();
								Vector min = sel.getMinimumPoint().toVector();
								SpleefStorage game = new SpleefStorage(SpleefDataType.GAME, SpleefDataType.GAME.s() + " " + args[1]);
								game.put(SpleefGameData.MAX, p.getWorld().getName() + " " + max.getBlockX() + " " + max.getBlockY() + " " + max.getBlockZ());
								game.put(SpleefGameData.MIN, p.getWorld().getName() + " " + min.getBlockX() + " " + min.getBlockY() + " " + min.getBlockZ());
								game.put(SpleefGameData.MATERIAL, Material.SNOW_BLOCK.toString());
								game.toGame().setEnabled(false);
								getManager().data.put(game.name(), game);
								reset(game.toGame());
								
								sender.s("Added the arena named &6" + args[1] + "&b!");
								sender.s("Reminder, you must set all of the data & then run /spleef enable <name> to use this arena!");
								
							} else {
								sender.err("Your height must be 1!");
							}
							
						} else {
							sender.err("You must select a flat rectangle with WorldEdit.");
						}
						
					} else {
						sender.err("That arena already exists.");
					}
				}
				
			break;
			
			case "remarena":
				
				if (!perms(p, "spleef.admin")){ return; }
				
				if (a(sender, args.length, 2)){
					if (doesGameExist(args[1])){
						getManager().data.remove(SpleefDataType.GAME.s() + " " + args[1]);
						new File("./plugins/Spleef/game/" + args[1] + ".yml").delete();
						sender.s("Deleted!");
					} else {
						sender.err("That arena does not exist!");
					}
				}
				
			break;
		}
	}
	
	public void reset(SpleefGame game){
		
		String[] max = ((String) game.gett(SpleefGameData.MAX)).split(" ");
		String[] min = ((String) game.gett(SpleefGameData.MIN)).split(" ");
		
		Vector maxLoc = new Location(Bukkit.getWorld(max[0]), Integer.parseInt(max[1]), Integer.parseInt(max[2]), Integer.parseInt(max[3])).toVector();
		Vector minLoc = new Location(Bukkit.getWorld(min[0]), Integer.parseInt(min[1]), Integer.parseInt(min[2]), Integer.parseInt(min[3])).toVector();
		
		for (int x = minLoc.getBlockX(); x <= maxLoc.getBlockX(); x++){
			for (int z = minLoc.getBlockZ(); z <= maxLoc.getBlockZ(); z++){
				new Location(Bukkit.getWorld(max[0]), x, maxLoc.getBlockY(), z).getBlock().setType(Material.valueOf((String) game.gett(SpleefGameData.MATERIAL)));
			}
		}
	}
	
	private boolean a(SpleefPlayer sender, int supplied, int needed){
		if (supplied >= needed){
			return true;
		}
		sender.err("Invalid arg count.");
		return false;
	}
	
	private boolean perms(Player p, String perm){
		return p.hasPermission(perm);
	}
}