package com.github.lyokofirelyte.Spleef;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.reflections.Reflections;

import static com.github.lyokofirelyte.Spleef.SpleefModule.*;
import com.github.lyokofirelyte.Spleef.Commands.SpleefCommand;
import com.github.lyokofirelyte.Spleef.Commands.SpleefCommandMain;
import com.github.lyokofirelyte.Spleef.Game.SpleefActive;
import com.github.lyokofirelyte.Spleef.Storage.SpleefManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class SpleefSetup implements CommandExecutor {

	private Spleef main;
	
	public SpleefSetup(Spleef i) {
		main = i;
	}

	public void start(){
		
		injectClass("spleefManager", new SpleefManager(main));
		injectClass("spleefActive", new SpleefActive(main));
		injectClass("spleefCommandMain", new SpleefCommandMain(main));
		injectClass("worldEdit", (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit"));
		
		listeners(
			getActive()
		);
		
		cmds(
			getCommandMain()	
		);
	}
	
	private void listeners(Listener... listeners){
		for (Listener l : listeners){
			Bukkit.getPluginManager().registerEvents(l, main);
		}
	}
		
	private void cmds(Object... exs){
		for (Object exe : exs){
			for (Method m : exe.getClass().getMethods()){
				if (m.getAnnotation(SpleefCommand.class) != null){
					SpleefCommand anno = m.getAnnotation(SpleefCommand.class);
					List<String> aliases = new ArrayList<String>(Arrays.asList(anno.aliases()));

					if (aliases.size() > 1) { 
						aliases.remove(0);
						main.getCommand(anno.aliases()[0]).setAliases(aliases);
					}
					
					main.getCommand(anno.aliases()[0]).setExecutor(this);
					main.getCommand(anno.aliases()[0]).setDescription(anno.desc());
					main.getCommand(anno.aliases()[0]).setPermission(anno.perm());
					main.getCommand(anno.aliases()[0]).setUsage(anno.help());
				}
			}
		}
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	
    	switch (label){
    		case "spleef": 
    			if (sender instanceof Player){
    				main.getHooks().getCommandMain().onSpleef((Player) sender, args);
    			}
    		break;
    	}
    	
    	return true;
    }
}