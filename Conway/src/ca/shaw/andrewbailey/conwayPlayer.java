package ca.shaw.andrewbailey;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.InvalidConfigurationException;

public class conwayPlayer{
	private Conway plugin;
	private UUID id;
	private Block firstCorner;
	private Block secondCorner;
	private Block lever;
	private int status = 0;
	private String aName;
	public conwayPlayer(Conway plugin,UUID id){
		this.plugin = plugin;
		this.id = id;
	}
	public UUID getID(){ return id;}
	public int creatingConway(){return status;}
	public void createConway(Block block) throws FileNotFoundException, IOException, InvalidConfigurationException{
		if(status == 1){
			firstCorner = block;
			Bukkit.getPlayer(id).sendMessage("Click the second corner.");
			status = 2;
		}else if(status == 2){
			secondCorner = block;
			Bukkit.getPlayer(id).sendMessage("Click the toggle lever.");
			status = 3;
		}else if(status == 3){
			if(block.getType() == Material.LEVER){
				lever = block;
				Bukkit.getPlayer(id).sendMessage("Conway Machine created.");
				plugin.createConway(this);
				status = 0;
			}else{
				Bukkit.getPlayer(id).sendMessage("Click a lever.");
			}
		}
	}
	public Block getFirstCorner(){return firstCorner;}
	public Block getSecondCorner(){return secondCorner;}
	public Block getLever(){return lever;}
	public String getAName(){return aName;}
	public void isCreatingConway(String name){
		aName = name;
		Bukkit.getPlayer(id).sendMessage("Click the first corner.");
		status = 1;
	}
}
