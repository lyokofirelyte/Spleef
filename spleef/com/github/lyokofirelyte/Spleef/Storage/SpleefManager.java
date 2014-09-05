package com.github.lyokofirelyte.Spleef.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.YamlConfiguration;

import com.github.lyokofirelyte.Spleef.Spleef;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefDataType;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefGameData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefPlayerData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefData.SpleefSystemData;
import com.github.lyokofirelyte.Spleef.Storage.Data.SpleefStorage;

public class SpleefManager {

	private Spleef main;
	
	public SpleefManager(Spleef i) {
		main = i;
		dirs();
	}
	
	public List<Class<? extends Enum>> enums = new ArrayList<>();
	public Map<String, SpleefStorage> data = new HashMap<String, SpleefStorage>();
	
	private Map<SpleefDataType, String> dirs = new HashMap<SpleefDataType, String>();
	
	public void mod(SpleefDataType dataType, YamlConfiguration yaml, SpleefStorage storage, boolean load) throws Exception {
		
		for (Class<? extends Enum> enumClass : enums){
			//if (enumClass.getClass().getMethod("s", null).getAnnotation(SpleefData.class) != null && Arrays.asList(enumClass.getClass().getMethod("s", null).getAnnotation(SpleefData.class).appliesTo()).contains(dataType)){
				for (Enum<?> enumValue : enumClass.getEnumConstants()){
					if (load){
						if (yaml.contains(enumValue.toString())){
							storage.put(enumValue, yaml.get(enumValue.toString()));
						} else {
							storage.put(enumValue, 0);
						}
					} else {
						if (storage.containsKey(enumValue)){
							yaml.set(enumValue.toString(), storage.get(enumValue));
						}
					}
				}
			//}
		}
		
		if (load){
			data.put(storage.name(), storage);
		}
		
		File file = checkFile(dataType, stripDirName(storage.name()) + ".yml");
		yaml.save(file);
	}
	
	public void load() throws Exception {
		
		enums.add(SpleefPlayerData.class);
		enums.add(SpleefGameData.class);
		enums.add(SpleefSystemData.class);
		
		for (SpleefDataType directory : dirs.keySet()){
			File folder = new File(dirs.get(directory));
			if (!folder.exists()){ folder.mkdirs(); }
			for (String fileName : folder.list()){
				mod(directory, YamlConfiguration.loadConfiguration(checkFile(directory, fileName)), new SpleefStorage(directory, directory.s() + " " + fileName.replace(".yml", "")), true);
			}
		}
		
		if (!data.containsKey(SpleefDataType.SYSTEM.s() + " system")){
			mod(SpleefDataType.SYSTEM, YamlConfiguration.loadConfiguration(checkFile(SpleefDataType.SYSTEM, "system.yml")), new SpleefStorage(SpleefDataType.SYSTEM, "SYSTEM system"), true);
		}
	}
	
	public void save() throws Exception {
		for (SpleefStorage storage : data.values()){
			mod(storage.type(), YamlConfiguration.loadConfiguration(checkFile(storage.type(), stripDirName(storage.name()) + ".yml")), storage, false);
		}
	}
	
	public File checkFile(SpleefDataType dir, String name){
		
		File file = new File(dirs.get(dir) + name);
		
		if (!file.exists()){
			try {
				file.createNewFile();
			} catch (Exception e){}
		}
		
		return file;
	}
	
	private void dirs(){
		dirs.put(SpleefDataType.PLAYER, "./plugins/Spleef/users/");
		dirs.put(SpleefDataType.SYSTEM, "./plugins/Spleef/system/");
		dirs.put(SpleefDataType.GAME, "./plugins/Spleef/game/");
	}
	
	public String stripDirName(String fileName){
		for (SpleefDataType type : SpleefDataType.values()){
			fileName = fileName.replace(type.s() + " ", "");
		}
		return fileName;
	}
}
