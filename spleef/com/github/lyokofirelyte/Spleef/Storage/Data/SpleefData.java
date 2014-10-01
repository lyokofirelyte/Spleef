package com.github.lyokofirelyte.Spleef.Storage.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 *We only want to put data to be saved & loaded on reboot into enums, as that's handled automatically.
 *All temp data (opponent / booleans, etc) can be in the interface for quick references.
 */
public @interface SpleefData {
	
	public SpleefDataType[] appliesTo() default {SpleefDataType.SYSTEM};
	
	public enum SpleefDataType {
		
		PLAYER("PLAYER"),
		SYSTEM("SYSTEM"),
		GAME("GAME");
		
		SpleefDataType(String type){
			this.type = type;
		}
		
		String type;
		
		public String s(){
			return type;
		}
	}
	
	public enum SpleefSystemData {
		
		LOBBY("LOBBY");
		
		SpleefSystemData(String type){
			this.type = type;
		}
		
		String type;
		
		@SpleefData(appliesTo = {SpleefDataType.SYSTEM})
		public String s(){
			return type;
		}
	}
	
	public enum SpleefGameData {

		PLAYERS("PLAYERS"),
		MAX("MAX"),
		MIN("MIN"),
		PLAYER_START_1("PLAYER_START_1"),
		PLAYER_START_2("PLAYER_START_2"),
		MATERIAL("MATERIAL");
		
		SpleefGameData(String type){
			this.type = type;
		}
		
		String type;
		
		@SpleefData(appliesTo = {SpleefDataType.GAME})
		public String s(){
			return type;
		}
	}
	
	public enum SpleefPlayerData {

		TOTAL_SCORE("TOTAL_SCORE"),
		TOTAL_WINS("TOTAL_WINS"),
		TOTAL_LOSSES("TOTAL_LOSSES");
		
		SpleefPlayerData(String type){
			this.type = type;
		}
		
		String type;
		
		@SpleefData(appliesTo = {SpleefDataType.PLAYER})
		public String s(){
			return type;
		}
	}
	
	public interface SpleefGame extends SpleefInfo {
		public List<SpleefPlayer> involvedPlayers();
		public boolean isEnabled();
		public boolean isReady();
		public int getPlayTo();
		public int getVotedToPlayTo();
		public void setEnabled(boolean enable);
		public void bc(String message);
		public void teleportPlayers();
		public void scoreboard(Player... playerz);
		public void setPlayTo(int i);
		public void barriers(Player p1, Player p2, boolean form);
		public void addVotedToPlayTo(SpleefPlayer player, int vote);
	}
	
	public interface SpleefPlayer extends SpleefInfo {
		public UUID uuid();
		public boolean inGame();
		public int getPoints();
		public String playerName();
		public SpleefPlayer opponent();
		public SpleefGame currentGame();
		public SpleefPlayer getInvite();
		public void s(String message);
		public void err(String message);
		public void setInvite(SpleefPlayer player);
		public void setOpponent(SpleefPlayer player);
		public void setCurrentGame(SpleefGame game);
		public void setInGame(boolean inGame);
		public void addPoint();
		public void setPoints(int points);
	}
	
	public interface SpleefInfo {
		public void putt(Enum<?> enumm, Object o);
		public Object gett(Enum<?> enumm);
	}
	
	public interface SpleefSystem {
		public Location getLobby();
		public Map<SpleefGame, Integer> getCounts();
		public void setLobby(Location l);
		public void setCount(SpleefGame g, int i);
	}
}