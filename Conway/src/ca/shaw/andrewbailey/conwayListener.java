package ca.shaw.andrewbailey;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class conwayListener implements Listener{
	private Conway plugin;
	public conwayListener(Conway conway) {
		plugin = conway;
		
	}
	
	@EventHandler
	public void playerInteract(PlayerInteractEvent event) throws FileNotFoundException, IOException, InvalidConfigurationException{
		Player p = event.getPlayer();
		if(event.getAction() == Action.LEFT_CLICK_BLOCK){
			conwayPlayer pp = plugin.checkPlayer(p);
			if(pp.creatingConway() > 0){
				pp.createConway(event.getClickedBlock());
				event.setCancelled(true);
			}
		}else if(event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock().getType() == Material.LEVER){
			plugin.leverSwitched(event.getClickedBlock());
		}
	}
}
