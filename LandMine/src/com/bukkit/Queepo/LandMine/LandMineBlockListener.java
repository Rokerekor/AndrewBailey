//The package
package com.bukkit.Queepo.LandMine;
//All the imports
import com.bukkit.Queepo.LandMine.LandMinePlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockListener;
import com.bukkit.Queepo.LandMine.LandMine;
//Start the class LandMineBlockListener
public class LandMineBlockListener extends BlockListener{
	 public static LandMine plugin;
	 public String lastBlock;
	 public String destroyedBlock;
	 public LandMineBlockListener(LandMine instance) {
    	 plugin = instance;
    }
	 //This method is called when ever a block is placed.
 	public void onBlockPlace(BlockPlaceEvent event) {
	 //Get the player doing the placing
		Player player = event.getPlayer();
		//Get the block that was placed
		Block block = event.getBlockPlaced();
		if((block.getType() == Material.STONE_PLATE || (block.getType() == Material.WOOD_PLATE))){
			if (LandMinePlayerListener.plugin.small(player)){
				player.sendMessage(ChatColor.RED + "**CAUTION**" + ChatColor.WHITE + " You have just placed a land mine!");
				Block blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX(), block.getY() - 2, block.getZ());
				lastBlock = String.valueOf(blocktnt.getType());
				blocktnt.setTypeId(46);
			}else if(LandMinePlayerListener.plugin.medium(player)){
				player.sendMessage(ChatColor.RED + "**CAUTION**" + ChatColor.WHITE + " You have just placed a big land mine!");
				Block blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX(), block.getY() - 2, block.getZ());
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX(), block.getY() - 2, block.getZ() + 1);
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX(), block.getY() - 2, block.getZ() - 1);
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX() + 1, block.getY() - 2, block.getZ());
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX() - 1, block.getY() - 2, block.getZ());
				blocktnt.setTypeId(46);
			}else if(LandMinePlayerListener.plugin.large(player)){
				player.sendMessage(ChatColor.RED + "**CAUTION**" + ChatColor.WHITE + " You have just placed a huge land mine!");
				Block blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX(), block.getY() - 2, block.getZ());
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX(), block.getY() - 2, block.getZ() + 1);
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX() + 1, block.getY() - 2, block.getZ() + 1);
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX() - 1, block.getY() - 2, block.getZ() + 1);
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX() + 1, block.getY() - 2, block.getZ() - 1);
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX() - 1, block.getY() - 2, block.getZ() - 1);
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX(), block.getY() - 2, block.getZ() - 1);
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX() + 1, block.getY() - 2, block.getZ());
				blocktnt.setTypeId(46);
				blocktnt = event.getPlayer().getWorld().getBlockAt(block.getX() - 1, block.getY() - 2, block.getZ());
				blocktnt.setTypeId(46);
			}
		}
	}
 	
	 public void onBlockDamage(BlockDamageEvent event) {
	     if (event.getDamageLevel() == BlockDamageLevel.BROKEN){
	    	Block destroyed = event.getBlock();
	    	destroyedBlock = String.valueOf(event.getBlock().getType());
	    	Player disarmer = event.getPlayer();
	 		if(destroyed.getType() == Material.STONE_PLATE || destroyed.getType() == Material.WOOD_PLATE){
	 			if (!LandMine.Permissions.has(disarmer, "landmine.disarm")) {
				    return;
				}else{
		 			Block blockbelow = event.getPlayer().getWorld().getBlockAt(destroyed.getX(), destroyed.getY() - 2, destroyed.getZ());
		 			Block blockbeside = event.getPlayer().getWorld().getBlockAt(destroyed.getX(), destroyed.getY() - 3, destroyed.getZ());
		 			if(blockbelow.getType() == Material.TNT){
		 				blockbelow.setType(blockbeside.getType());
		 				disarmer.sendMessage(ChatColor.GREEN + "You have just disarmed a landmine.");
		 			}
				}
	 	 	} 
	     }
	 }
}
