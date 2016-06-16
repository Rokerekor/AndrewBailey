package ca.shaw.andrewbailey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class ConwayArena{
	//Extends BukkitRunnable to create a run method
	 
	private final Conway plugin;
	private Location c1;
	private Location c2;
	private Location l;
	BukkitTask task;
	public ConwayArena(Conway plugin,conwayPlayer player) {
		this.plugin = plugin;
		c1 = player.getFirstCorner().getLocation();
		c2 = player.getSecondCorner().getLocation();
		l = player.getLever().getLocation();
	}
	public ConwayArena(Conway plugin, File file) throws FileNotFoundException, IOException, InvalidConfigurationException {
		this.plugin = plugin;
		FileConfiguration config = new YamlConfiguration();
		config.load(file);
		c1 = Bukkit.getWorld(config.getString("FirstCorner.World")).getBlockAt(config.getInt("FirstCorner.X"),config.getInt("FirstCorner.Y"),config.getInt("FirstCorner.Z")).getLocation();
		c2 = Bukkit.getWorld(config.getString("SecondCorner.World")).getBlockAt(config.getInt("SecondCorner.X"),config.getInt("SecondCorner.Y"),config.getInt("SecondCorner.Z")).getLocation();
		l = Bukkit.getWorld(config.getString("Lever.World")).getBlockAt(config.getInt("Lever.X"),config.getInt("Lever.Y"),config.getInt("Lever.Z")).getLocation();
	}
	public void start(){
		task = new ConwayTask(plugin,this).runTaskTimer(plugin, 10, 10);
	}
	public void stop(){
		if(task != null){
			task.cancel();
		}
	}
	public Location getFirstCorner() {return c1;}
	public Location getSecondCorner() {return c2;}
	public Location getLever(){return l;}
}