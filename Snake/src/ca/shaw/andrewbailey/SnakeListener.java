package ca.shaw.andrewbailey;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SnakeListener implements Listener{
	Snake plugin;
	public SnakeListener(Snake plugin){
		this.plugin = plugin;
	}
	@EventHandler
	public void playerInteract(PlayerInteractEvent event){
		Player p = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.getClickedBlock().getType() == Material.WOOD_BUTTON || event.getClickedBlock().getType() == Material.STONE_BUTTON)){
			plugin.buttonPressed(event.getClickedBlock(),event.getPlayer());
		}else if(event.getAction() == Action.LEFT_CLICK_BLOCK){
			if(plugin.blockPunched(event.getClickedBlock(),p)){
				event.setCancelled(true);
			}
			SnakePlayer pp = plugin.checkPlayer(p);
			if(pp.clickedBlock(event.getClickedBlock())){
				event.setCancelled(true);
			}
		}
	}
}
