package com.github.lyokofirelyte.Spleef;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Spleef extends JavaPlugin {
	
	private SpleefModule module;
	private SpleefSetup setup;

	@Override
	public void onEnable(){
		
		module = new SpleefModule(this);
		setup = new SpleefSetup(this);
		setup.start();
		
		try {
			module.getManager().load();
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Error loading - see above stack.");
		}
	}
	
	@Override
	public void onDisable(){
		
		Bukkit.getScheduler().cancelTasks(this);
		
		try {
			module.getManager().save();
		} catch (Exception e){
			e.printStackTrace();
			System.out.println("Error saving - see above stack.");
		}
	}
	
	public SpleefModule getHooks(){
		return module;
	}
}