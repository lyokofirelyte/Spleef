package com.github.lyokofirelyte.Spleef.Storage.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import static com.github.lyokofirelyte.Spleef.SpleefModule.*;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefPlayer;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefSystem;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefSystemData;

public class SpleefStorage extends HashMap<Enum<?>, Object> {
	
	private static final long serialVersionUID = 1L;
	private String name;
	private SpleefDataType type = SpleefDataType.SYSTEM;
	
	public SpleefStorage(SpleefDataType dataType, String name){
		this.name = name;
		type = dataType;
		start();
	}
	
	private SpleefPlayer spleefPlayer;
	private SpleefGame spleefGame;
	private SpleefSystem spleefSystem;
	
	public void start(){
		
		spleefPlayer = type.equals(SpleefDataType.PLAYER) ? new SpleefPlayer(){

			private UUID uuid = UUID.fromString(getManager().stripDirName(name));
			private String playerName = Bukkit.getOfflinePlayer(uuid).getName();
			private SpleefPlayer opponent;
			private SpleefPlayer invite;
			private SpleefGame currentGame;
			private boolean inGame = false;
			private int points = 0;
			
			public String playerName(){
				return playerName;
			}
	
			public UUID uuid(){
				return uuid;
			}
	
			public void s(String message){
				Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpleef &f// &b" + message));
			}
	
			public void err(String message){
				Bukkit.getPlayer(uuid).sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpleef &f// &c&o" + message));
			}
			
			public void putt(Enum<?> enumm, Object o){
				put(enumm, o);
			}
			
			public Object gett(Enum<?> enumm){
				return get(enumm);
			}
	
			public SpleefPlayer opponent(){
				return opponent;
			}
			
			public SpleefPlayer getInvite(){
				return invite;
			}
	
			public SpleefGame currentGame(){
				return currentGame;
			}
			
			public int getPoints(){
				return points;
			}
	
			public boolean inGame(){
				return inGame;
			}
			
			public void setOpponent(SpleefPlayer player){
				opponent = player;
			}
			
			public void setCurrentGame(SpleefGame game){
				currentGame = game;
			}
			
			public void setInGame(boolean inGame){
				this.inGame = inGame;
			}
			
			public void setInvite(SpleefPlayer player){
				invite = player;
			}
			
			public void addPoint(){
				points++;
			}
			
			public void setPoints(int point){
				points = point;
			}
		
		} : null;
	
		spleefGame = type.equals(SpleefDataType.GAME) ? new SpleefGame(){
	
			private List<SpleefPlayer> players = new ArrayList<SpleefPlayer>();
			private long cd = 0L;
			private boolean enabled = true;
			
			public boolean isEnabled(){
				return enabled;
			}
			
			public List<SpleefPlayer> involvedPlayers(){
				return players;
			}
	
			public void bc(String message){
				for (SpleefPlayer p : involvedPlayers()){
					p.s(message);
				}
			}
			
			public void putt(Enum<?> enumm, Object o){
				put(enumm, o);
			}
			
			public void setEnabled(boolean enable){
				enabled = enable;
			}
			
			public void teleportPlayers(){
				for (int i = 0; i < 2; i++){
					String[] loc = i == 0 ? ((String) get(SpleefGameData.PLAYER_START_1)).split(" ") : ((String) get(SpleefGameData.PLAYER_START_2)).split(" "); 
					Bukkit.getPlayer(players.get(i).uuid()).teleport(new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5])));
					Bukkit.getPlayer(players.get(i).uuid()).setWalkSpeed(0);
				}
				
				new Thread(new Runnable(){ public void run(){
					
					int count = 5;
					
					for (int i = 0; i < 5; i++){
						
						bc(count + "...");
						count = count - 1;
						
						if (count == 0){
							bc("GO!");
							Bukkit.getPlayer(players.get(0).uuid()).setWalkSpeed(0.2F);
							Bukkit.getPlayer(players.get(1).uuid()).setWalkSpeed(0.2F);
						}
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
				}}).start();
			}
			
			public Object gett(Enum<?> enumm){
				return get(enumm);
			}
			
		} : null;
		
		spleefSystem = type.equals(SpleefDataType.SYSTEM) ? new SpleefSystem(){
			
			private String[] lobbyCoords = containsKey(SpleefSystemData.LOBBY) && !((String) get(SpleefSystemData.LOBBY)).equals("none") ? ((String) get(SpleefSystemData.LOBBY)).split(" ") : new String[]{"none"};
			private Location lobby = !lobbyCoords[0].equals("none") ? new Location(Bukkit.getWorld(lobbyCoords[0]), Integer.parseInt(lobbyCoords[1]), Integer.parseInt(lobbyCoords[2]), Integer.parseInt(lobbyCoords[3]), Float.parseFloat(lobbyCoords[4]), Float.parseFloat(lobbyCoords[5])) : null;
			private Map<SpleefGame, Integer> counts = new HashMap<>();
			
			public Location getLobby(){
				lobbyCoords = containsKey(SpleefSystemData.LOBBY) && !((String) get(SpleefSystemData.LOBBY)).equals("none") ? ((String) get(SpleefSystemData.LOBBY)).split(" ") : new String[]{"none"};
				lobby = !lobbyCoords[0].equals("none") ? new Location(Bukkit.getWorld(lobbyCoords[0]), Integer.parseInt(lobbyCoords[1]), Integer.parseInt(lobbyCoords[2]), Integer.parseInt(lobbyCoords[3]), Float.parseFloat(lobbyCoords[4]), Float.parseFloat(lobbyCoords[5])) : null;
				return lobby;
			}
			
			public void setLobby(Location l){
				lobby = l;
				put(SpleefSystemData.LOBBY, l.getWorld().getName() + " " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " " + l.getYaw() + " " + l.getPitch());
			}
			
			public Map<SpleefGame, Integer> getCounts() {
				return counts;
			}
			
			public void setCount(SpleefGame g, int i) {
				counts.put(g, i);
			}
			
		} : null;
	}
	
	public String name(){
		return name;
	}
	
	public SpleefDataType type(){
		return type;
	}
	
	public SpleefPlayer toPlayer(){
		return spleefPlayer;
	}
	
	public SpleefGame toGame(){
		return spleefGame;
	}
	
	public SpleefSystem toSystem(){
		return spleefSystem;
	}
}