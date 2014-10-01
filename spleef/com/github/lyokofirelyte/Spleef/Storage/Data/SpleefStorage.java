package com.github.lyokofirelyte.Spleef.Storage.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.minecraft.util.gnu.trove.map.hash.THashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import static com.github.lyokofirelyte.Spleef.SpleefModule.*;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGame;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefPlayer;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefSystem;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefSystemData;

public class SpleefStorage extends THashMap<Enum<?>, Object> {
	
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
			private Map<SpleefPlayer, Integer> playToVote = new THashMap<SpleefPlayer, Integer>();
			private long cd = 0L;
			private int playTo = 5;
			private boolean enabled = true;
			private boolean ready = false;
			
			public int getVotedToPlayTo(){
				
				if (playToVote.size() == 2){
					if (playToVote.values().toArray()[0] == playToVote.values().toArray()[1]){
						playTo = (int) playToVote.values().toArray()[0];
						return (int) playToVote.values().toArray()[0];
					}
				}
				
				return playTo;
			}
			
			public int getPlayTo(){
				return playTo;
			}
			
			public boolean isEnabled(){
				return enabled;
			}
			
			public List<SpleefPlayer> involvedPlayers(){
				return players;
			}
			
			public void addVotedToPlayTo(SpleefPlayer player, int vote){
				playToVote.put(player, vote);
				if (new Integer(getPlayTo()) != getVotedToPlayTo()){
					bc("The play to has been changed to &6" + getPlayTo() + "&b!");
					scoreboard(Bukkit.getPlayer(players.get(0).uuid()), Bukkit.getPlayer(players.get(1).uuid()));
				} else {
					bc(player.playerName() + " &bwants to change the playto to &6" + vote + "&b!");
					player.opponent().s("&oType /spleef playto " + vote + " to confirm this change.");
				}
			}
			
			public void setPlayTo(int i){
				playTo = i;
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
			
			public boolean isReady(){
				return ready;
			}
			
			public void teleportPlayers(){
				
				ready = false;
				ItemStack item = new ItemStack(Material.DIAMOND_SPADE, 1);
				ItemMeta im = item.getItemMeta();
				im.addEnchant(Enchantment.DURABILITY, 10, true);
				im.setDisplayName(AS("&b&oSPLEEF!"));
				item.setItemMeta(im);
				
				for (int i = 0; i < 2; i++){
					String[] loc = i == 0 ? ((String) get(SpleefGameData.PLAYER_START_1)).split(" ") : ((String) get(SpleefGameData.PLAYER_START_2)).split(" "); 
					Bukkit.getPlayer(players.get(i).uuid()).teleport(new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5])));
					Bukkit.getPlayer(players.get(i).uuid()).getInventory().clear();
					Bukkit.getPlayer(players.get(i).uuid()).setGameMode(GameMode.SURVIVAL);
					Bukkit.getPlayer(players.get(i).uuid()).getInventory().addItem(item);
					Bukkit.getPlayer(players.get(i).uuid()).setWalkSpeed(0F);
				}
				
				barriers(Bukkit.getPlayer(players.get(0).uuid()), Bukkit.getPlayer(players.get(1).uuid()), true);
				scoreboard(Bukkit.getPlayer(players.get(0).uuid()), Bukkit.getPlayer(players.get(1).uuid()));
				
				new Thread(new Runnable(){ public void run(){
					  
					int count = 3;
					
					for (int i = 0; i <= 3; i++){
						
						if (count == 0){
							bc("GO!");
							barriers(Bukkit.getPlayer(players.get(0).uuid()), Bukkit.getPlayer(players.get(1).uuid()), false);
							Bukkit.getPlayer(players.get(0).uuid()).setWalkSpeed(0.2F);
							Bukkit.getPlayer(players.get(1).uuid()).setWalkSpeed(0.2F);
							ready = true;
						} else {
						
							bc(count + "...");
							count = count - 1;
							
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					
				}}).start();
			}
			
			public void scoreboard(Player... playerz){
				
				ScoreboardManager manager = Bukkit.getScoreboardManager();
				Scoreboard board = manager.getNewScoreboard();
				boolean form = false;
				
				for (Player p : playerz){
					Objective o = p.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
					if (o == null){
						try {
							o = board.registerNewObjective("gameSpleef", "dummy");
							o.setDisplaySlot(DisplaySlot.SIDEBAR);
						} catch (Exception e){
							o = board.getObjective(DisplaySlot.SIDEBAR);
						}
						form = true;
					}
					o.setDisplayName(AS("&bSPLEEF! &6(&b" + getPlayTo() + "&6)"));
					Score s1 = o.getScore(AS(playerz[0].getDisplayName()));
					s1.setScore(players.get(0).getPoints());
					Score s2 = o.getScore(AS(playerz[1].getDisplayName()));
					s2.setScore(players.get(1).getPoints());
					if (form){
						p.setScoreboard(board);
					}
				}
				
			}
			
			public void barriers(Player p1, Player p2, boolean form){
				
				String[] loc = ((String) get(SpleefGameData.PLAYER_START_1)).split(" ");
				Location l1 = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
				
				for (Location l : circle(l1, 3, 4, form ? true : false, false, 0)){
					l.getBlock().setType(form ? Material.GLASS : Material.AIR);
				}
				
				loc = ((String) get(SpleefGameData.PLAYER_START_2)).split(" ");
				Location l2 = new Location(Bukkit.getWorld(loc[0]), Integer.parseInt(loc[1]), Integer.parseInt(loc[2]), Integer.parseInt(loc[3]), Float.parseFloat(loc[4]), Float.parseFloat(loc[5]));
				
				for (Location l : circle(l2, 3, 4, form ? true : false, false, 0)){
					l.getBlock().setType(form ? Material.GLASS : Material.AIR);
				}
			}
			
			public Object gett(Enum<?> enumm){
				return get(enumm);
			}
			
		} : null;
		
		spleefSystem = type.equals(SpleefDataType.SYSTEM) ? new SpleefSystem(){
			
			private String[] lobbyCoords = containsKey(SpleefSystemData.LOBBY) && !((String) get(SpleefSystemData.LOBBY)).equals("none") ? ((String) get(SpleefSystemData.LOBBY)).split(" ") : new String[]{"none"};
			private Location lobby = !lobbyCoords[0].equals("none") ? new Location(Bukkit.getWorld(lobbyCoords[0]), Integer.parseInt(lobbyCoords[1]), Integer.parseInt(lobbyCoords[2]), Integer.parseInt(lobbyCoords[3]), Float.parseFloat(lobbyCoords[4]), Float.parseFloat(lobbyCoords[5])) : null;
			private Map<SpleefGame, Integer> counts = new THashMap<>();
			
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
	
    public List<Location> circle (Location loc, Integer r, Integer h, Boolean hollow, Boolean sphere, int plus_y) {
        List<Location> circleblocks = new ArrayList<Location>();
        int cx = loc.getBlockX();
        int cy = loc.getBlockY();
        int cz = loc.getBlockZ();
        for (int x = cx - r; x <= cx +r; x++)
            for (int z = cz - r; z <= cz +r; z++)
                for (int y = (sphere ? cy - r : cy); y < (sphere ? cy + r : cy + h); y++) {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r*r && !(hollow && dist < (r-1)*(r-1))) {
                        Location l = new Location(loc.getWorld(), x, y + plus_y, z);
                        circleblocks.add(l);
                        }
                    }
     
        return circleblocks;
    }
}