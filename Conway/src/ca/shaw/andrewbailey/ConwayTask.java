package ca.shaw.andrewbailey;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

public class ConwayTask extends BukkitRunnable {
	//Extends BukkitRunnable to create a run method
	 
	private final Conway plugin;
	private Location c1;
	private Location c2;
	private Location l;	 
	public ConwayTask(Conway plugin,ConwayArena arena) {
		this.plugin = plugin;
		c1 = arena.getFirstCorner();
		c2 = arena.getSecondCorner();
	}	
	
	public void run() {
		ArrayList<Block> toO =  new ArrayList();
		ArrayList<Block> toI =  new ArrayList();
		if(c1 != null && c2 != null){
			int count=0;
			if(c1.getBlockX() == c2.getBlockX()){
				int y1 = c1.getBlockY();
				int y2 = c2.getBlockY();
				if(c1.getBlockY() > c2.getBlockY()){
					y1 = c2.getBlockY();
					y2 = c1.getBlockY();
				}
				for(int y=y1;y<=y2;y++){
					int z1 = c1.getBlockZ();
					int z2 = c2.getBlockZ();
					if(c1.getBlockZ() > c2.getBlockZ()){
						z1 = c2.getBlockZ();
						z2 = c1.getBlockZ();
					}
					for(int z=z1;z<=z2;z++){
						Block c = c1.getWorld().getBlockAt(c1.getBlockX(), y, z);
						Block[] bA = new Block[8];
						bA[0] = c1.getWorld().getBlockAt(c1.getBlockX(), y+1, z+1);
						bA[1] = c1.getWorld().getBlockAt(c1.getBlockX(), y+1, z);
						bA[2] = c1.getWorld().getBlockAt(c1.getBlockX(), y+1, z-1);
						bA[3] = c1.getWorld().getBlockAt(c1.getBlockX(), y, z+1);
						bA[4] = c1.getWorld().getBlockAt(c1.getBlockX(), y, z-1);
						bA[5] = c1.getWorld().getBlockAt(c1.getBlockX(), y-1, z+1);
						bA[6] = c1.getWorld().getBlockAt(c1.getBlockX(), y-1, z-1);
						bA[7] = c1.getWorld().getBlockAt(c1.getBlockX(), y-1, z);
						for(int k=0;k<8;k++){
							if(bA[k].getType() == Material.OBSIDIAN){
								count++;
							}
						}
						if(count<2){
							toI.add(c);
						}
						else if(count<4 && c.getType() == Material.OBSIDIAN){
							toO.add(c);
						}else if(count == 3){
							toO.add(c);
						}else{
							toI.add(c);
						}
						count = 0;
					}
				}
			}else if(c1.getBlockY() == c2.getBlockY()){
				int x1 = c1.getBlockX();
				int x2 = c2.getBlockX();
				if(c1.getBlockX() > c2.getBlockX()){
					x1 = c2.getBlockX();
					x2 = c1.getBlockX();
				}
				for(int x=x1;x<=x2;x++){
					int z1 = c1.getBlockZ();
					int z2 = c2.getBlockZ();
					if(c1.getBlockZ() > c2.getBlockZ()){
						z1 = c2.getBlockZ();
						z2 = c1.getBlockZ();
					}
					for(int z=z1;z<=z2;z++){
						Block c = c1.getWorld().getBlockAt(x, c1.getBlockY(), z);
						Block[] bA = new Block[8];
						bA[0] = c1.getWorld().getBlockAt(x+1, c1.getBlockY(), z+1);
						bA[1] = c1.getWorld().getBlockAt(x+1, c1.getBlockY(), z);
						bA[2] = c1.getWorld().getBlockAt(x+1, c1.getBlockY(), z-1);
						bA[3] = c1.getWorld().getBlockAt(x, c1.getBlockY(), z+1);
						bA[4] = c1.getWorld().getBlockAt(x, c1.getBlockY(), z-1);
						bA[5] = c1.getWorld().getBlockAt(x-1, c1.getBlockY(), z+1);
						bA[6] = c1.getWorld().getBlockAt(x-1, c1.getBlockY(), z-1);
						bA[7] = c1.getWorld().getBlockAt(x-1, c1.getBlockY(), z);
						for(int k=0;k<8;k++){
							if(bA[k].getType() == Material.OBSIDIAN){
								count++;
							}
						}
						if(count<2){
							toI.add(c);
						}
						else if(count<4 && c.getType() == Material.OBSIDIAN){
							toO.add(c);
						}else if(count == 3){
							toO.add(c);
						}else{
							toI.add(c);
						}
						count = 0;
					}
				}
			}else if(c1.getBlockZ() == c2.getBlockZ()){
				int y1 = c1.getBlockY();
				int y2 = c2.getBlockY();
				if(c1.getBlockY() > c2.getBlockY()){
					y1 = c2.getBlockY();
					y2 = c1.getBlockY();
				}
				for(int y=y1;y<=y2;y++){
					int x1 = c1.getBlockX();
					int x2 = c2.getBlockX();
					if(c1.getBlockX() > c2.getBlockX()){
						x1 = c2.getBlockX();
						x2 = c1.getBlockX();
					}
					for(int x=x1;x<=x2;x++){
						Block c = c1.getWorld().getBlockAt(x, y, c1.getBlockZ());
						Block[] bA = new Block[8];
						bA[0] = c1.getWorld().getBlockAt(x+1, y+1, c1.getBlockZ());
						bA[1] = c1.getWorld().getBlockAt(x, y+1, c1.getBlockZ());
						bA[2] = c1.getWorld().getBlockAt(x-1, y+1, c1.getBlockZ());
						bA[3] = c1.getWorld().getBlockAt(x+1, y, c1.getBlockZ());
						bA[4] = c1.getWorld().getBlockAt(x-1, y, c1.getBlockZ());
						bA[5] = c1.getWorld().getBlockAt(x+1, y-1, c1.getBlockZ());
						bA[6] = c1.getWorld().getBlockAt(x-1, y-1, c1.getBlockZ());
						bA[7] = c1.getWorld().getBlockAt(x, y-1,c1.getBlockZ());
						for(int k=0;k<8;k++){
							if(bA[k].getType() == Material.OBSIDIAN){
								count++;
							}
						}
						if(count<2){
							toI.add(c);
						}
						else if(count<4 && c.getType() == Material.OBSIDIAN){
							toO.add(c);
						}else if(count == 3){
							toO.add(c);
						}else{
							toI.add(c);
						}
						count = 0;
					}
				}
			}
			for(int i=0;i<toO.size();i++){
				Block block = toO.get(i);
				block.setType(Material.OBSIDIAN);
			}
			for(int i=0;i<toI.size();i++){
				Block block = toI.get(i);
				block.setType(Material.IRON_BLOCK);
			}
		}
	}
}