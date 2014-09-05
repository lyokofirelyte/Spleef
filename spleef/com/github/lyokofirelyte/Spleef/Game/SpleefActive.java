package com.github.lyokofirelyte.Spleef.Game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.github.lyokofirelyte.Spleef.Spleef;
import static com.github.lyokofirelyte.Spleef.SpleefModule.*;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefPlayer;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefPlayerData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefStorage;

public class SpleefActive implements Listener {

	private Spleef main;
	
	public SpleefActive(Spleef i) {
		main = i;
	}

	@EventHandler
	public void onFall(PlayerMoveEvent e){
		
		SpleefPlayer sp = getSpleefPlayer(e.getPlayer().getUniqueId());
		
		if (sp.inGame()){
			SpleefGame game = sp.currentGame();
			if (e.getTo().getBlockY() < Integer.parseInt(((String) game.gett(SpleefGameData.MIN)).split(" ")[2])){
				sp.opponent().addPoint();
				getCommandMain().reset(game);
				if (sp.opponent().getPoints() >= 3 && sp.opponent().getPoints() > sp.getPoints()+1){
					sp.putt(SpleefPlayerData.TOTAL_LOSSES, (int)sp.gett(SpleefPlayerData.TOTAL_LOSSES)+1);
					sp.opponent().putt(SpleefPlayerData.TOTAL_WINS, (int)sp.opponent().gett(SpleefPlayerData.TOTAL_WINS)+1);
					sp.opponent().putt(SpleefPlayerData.TOTAL_SCORE, (int)sp.opponent().gett(SpleefPlayerData.TOTAL_SCORE)+sp.opponent().getPoints());
					sp.opponent().setPoints(0);
					sp.setPoints(0);
					sp.setInGame(false);
					sp.opponent().setInGame(false);
					game.bc(Bukkit.getPlayer(sp.opponent().uuid()).getDisplayName() + " &bhas won!");
					for (SpleefPlayer player : game.involvedPlayers()){
						Bukkit.getPlayer(player.uuid()).teleport(getSpleefSystem().getLobby());
						Bukkit.getPlayer(player.uuid()).getInventory().clear();
					}
					game.involvedPlayers().clear();
				} else {
					game.teleportPlayers();
					getCommandMain().reset(game);
					game.bc("Point scored by " + Bukkit.getPlayer(sp.opponent().uuid()).getDisplayName() + "&b!");
				}
			}
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e){
		
		SpleefPlayer sp = getSpleefPlayer(e.getPlayer().getUniqueId());
		 
		if (sp.inGame()){
			if (e.getBlock().getType().equals(Material.valueOf((String) sp.currentGame().gett(SpleefGameData.MATERIAL)))){
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		
		if (!doesPlayerExist(e.getPlayer().getUniqueId())){
			try {
				getManager().mod(SpleefDataType.PLAYER, YamlConfiguration.loadConfiguration(getManager().checkFile(SpleefDataType.PLAYER, e.getPlayer().getUniqueId().toString() + ".yml")), new SpleefStorage(SpleefDataType.PLAYER, SpleefDataType.PLAYER.s() + " " + e.getPlayer().getUniqueId().toString()), true);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		
		SpleefPlayer sp = getSpleefPlayer(e.getPlayer().getUniqueId());
		
		if (sp.inGame()){
			Location l = e.getPlayer().getLocation();
			onFall(new PlayerMoveEvent(e.getPlayer(), l, new Location(l.getWorld(), l.getX(), 0, l.getY())));
		}
	}
}