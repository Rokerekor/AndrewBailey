package ca.shaw.andrewbailey;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.material.MaterialData;

public class pListener implements Listener {
	@EventHandler
	public void onSignChange(SignChangeEvent event) throws SQLException{
		Player placer = event.getPlayer();
		String line1 = event.getLine(0);
		String line2 = event.getLine(1);
		//String line3 = event.getLine(2);
		//String line4 = event.getLine(3);
		float radius = 0;
		if(line1.equalsIgnoreCase("[Proximity]") && event.getBlock().getType() == Material.SIGN_POST && placer.hasPermission("proximity.create")){
			try{
				radius = Float.parseFloat( line2 );
				event.setLine(0, ChatColor.GREEN + "[Proximity]");
				float newRadius = ProximityDetector.checkRadius(radius);
				if(radius != newRadius){
					radius = newRadius;
					ProximityDetector.redMessage(placer, "Proximity too large, reduced to maximum allowed of " + ChatColor.GREEN + radius + ChatColor.RED + ".");
				}
				String newLine = "" + radius;
				event.setLine(1, ChatColor.RED + newLine);
				Proximity newProximity = new Proximity(event.getBlock().getWorld().getName(),event.getBlock().getX(),event.getBlock().getY(),event.getBlock().getZ(),radius);
				ProximityDetector.addProximity(newProximity);
				ProximityDetector.greenMessage(placer, "Proximity detector of " + radius + " radius created.");
			}catch(Exception e){
				ProximityDetector.redMessage(placer, "Invalid Number");
				event.getBlock().breakNaturally();
			}
		}
	}
	@EventHandler
	public void onBlockDestroy(BlockBreakEvent event) throws SQLException{
		Block block = event.getBlock();
		Player destroyer = event.getPlayer();
		if(block.getType() == Material.SIGN_POST){
			Sign sign = (Sign) event.getBlock().getState();
			Bukkit.getServer().broadcastMessage(""+sign.getData());
			if(sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[Proximity]")){
				if(ProximityDetector.deleteSign(block.getWorld().getName(),block.getX(),block.getY(),block.getZ()) == true){
					ProximityDetector.greenMessage(destroyer, "Proximity detector destroyed");
				}
			}
		}else{
			Block ba = block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
			if(ba.getType() == Material.SIGN_POST){
				Sign sign = (Sign) ba.getState();
				if(sign.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[Proximity]")){
					if(ProximityDetector.deleteSign(ba.getWorld().getName(),ba.getX(),ba.getY(),ba.getZ()) == true){
						ProximityDetector.greenMessage(destroyer, "Proximity detector destroyed");
					}
				}
			}
		}
		if(block.getType() == Material.REDSTONE_TORCH_ON){
			ProximityDetector.torchDestroyed(block);
		}
	}
	@EventHandler
	 public void onBlockRedstoneChange(BlockRedstoneEvent event)
	 {	 
		for(int i=0;i<ProximityDetector.proximityList.size();i++)
		{
			Proximity prox = ProximityDetector.proximityList.get(i);
			World world = Bukkit.getServer().getWorld(prox.world);
			Block[] powered = new Block[4];
			powered[0] = world.getBlockAt(prox.X-1, prox.Y, prox.Z);
			powered[1] = world.getBlockAt(prox.X+1, prox.Y, prox.Z);
			powered[2] = world.getBlockAt(prox.X, prox.Y, prox.Z+1);
			powered[3] = world.getBlockAt(prox.X, prox.Y, prox.Z-1);
			for(int j=0;j<4;j++)
			{
				Block pow = powered[j];
				Block eve = event.getBlock();
				if(pow.getWorld() == eve.getWorld() && pow.getX() == eve.getX() && pow.getY() == eve.getY() && pow.getZ() == eve.getZ()){
					event.setNewCurrent(event.getOldCurrent());
					return;
				}
			}
		}
	 }
}
