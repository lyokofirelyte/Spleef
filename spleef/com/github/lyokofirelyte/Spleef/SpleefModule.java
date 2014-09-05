package com.github.lyokofirelyte.Spleef;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.github.lyokofirelyte.Spleef.Commands.SpleefCommandMain;
import com.github.lyokofirelyte.Spleef.Game.SpleefActive;
import com.github.lyokofirelyte.Spleef.Storage.SpleefManager;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefPlayer;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefSystem;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefStorage;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class SpleefModule {
	
	public Spleef main;
	private static Map<String, Object> classes = new HashMap<String, Object>();

	public SpleefModule(Spleef i){
		main = i;
	}
	
	// UTILS
	
	public static void s(CommandSender cs, String message){
		cs.sendMessage(AS("&bSpleef &f// &b" + message));
	}

	public static String AS(String message){
		return ChatColor.translateAlternateColorCodes('&', message);
	}
	
	// DATA
	
	public static boolean doesPlayerExist(UUID uuid){
		return getManager().data.containsKey(SpleefDataType.PLAYER.s() + " " + uuid);
	}
	
	public static boolean doesPlayerExist(String name){
		return matchSpleefPlayer(name) != null;
	}
	
	public static boolean doesGameExist(String name){
		return getManager().data.containsKey(SpleefDataType.GAME.s() + " " + name);
	}
	
	public static SpleefPlayer matchSpleefPlayer(String name){
		for (SpleefStorage s : getManager().data.values()){
			if (s.type().equals(SpleefDataType.PLAYER)){
				if (s.toPlayer().playerName().equals(name)){
					return s.toPlayer();
				}
			}
		}
		return null;
	}
	
	public static SpleefPlayer getSpleefPlayer(UUID uuid){
		return getManager().data.get(SpleefDataType.PLAYER.s() + " " + uuid.toString()).toPlayer();
	}
	
	public static SpleefGame getSpleefGame(String name){
		return getManager().data.get(SpleefDataType.GAME.s() + " " + name).toGame();
	}
	
	public static SpleefSystem getSpleefSystem(){
		return getManager().data.get(SpleefDataType.SYSTEM.s() + " system").toSystem();
	}
	
	public static List<SpleefGame> getAllGames(){
		
		List<SpleefGame> list = new ArrayList<SpleefGame>();
		
		for (SpleefStorage s : getManager().data.values()){
			if (s.type().equals(SpleefDataType.GAME)){
				list.add(s.toGame());
			}
		}
		
		return list;
	}
	
	public static List<SpleefPlayer> getAllUsers(){
		
		List<SpleefPlayer> list = new ArrayList<SpleefPlayer>();
		
		for (SpleefStorage s : getManager().data.values()){
			if (s.type().equals(SpleefDataType.PLAYER)){
				list.add(s.toPlayer());
			}
		}
		
		return list;
	}
	
	// CLASS REFERENCES

	public static SpleefManager getManager(){
		return ((SpleefManager) classes.get("spleefManager"));
	}
	
	public static SpleefActive getActive(){
		return ((SpleefActive) classes.get("spleefActive"));
	}
	
	public static SpleefCommandMain getCommandMain(){
		return ((SpleefCommandMain) classes.get("spleefCommandMain"));
	}
	
	public static WorldEditPlugin getWorldEdit(){
		return ((WorldEditPlugin) classes.get("worldEdit"));
	}
	
	public Spleef getMain(){
		return main;
	}
	
	public static Object getUndefinedClass(String name){
		return classes.get(name);
	}
	
	public static void injectClass(String name, Object clazz){
		classes.put(name, clazz);
	}
}