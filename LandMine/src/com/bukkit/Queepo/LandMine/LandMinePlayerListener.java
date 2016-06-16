//The Package
package com.bukkit.Queepo.LandMine;
//All the imports
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import com.bukkit.Queepo.LandMine.LandMine;
//Starts the class LandMinePlayer listener
public class LandMinePlayerListener extends PlayerListener{
	 public static LandMine plugin;
	  public LandMinePlayerListener(LandMine instance) {
	        plugin = instance;
	    }
	  //This method is called whenever a player uses a command.
	  	public void onPlayerCommand(PlayerChatEvent event) {
		  //Make the message a string.
			String[] split = event.getMessage().split(" ");
			//Get the player that talked.
			Player player = event.getPlayer();
			//If the first part of the string is /landmine or /b then do this.
			if(split.length == 2 && (split[0].equalsIgnoreCase("/landmine") || split[0].equalsIgnoreCase("/lm"))){
				if (!LandMine.Permissions.has(player, "landmine.place")) {
				    return;
				}else if(split[1].equalsIgnoreCase("big")){
					plugin.toggleLandmineMedium(player);
				}else if(split[1].equalsIgnoreCase("huge")){
					plugin.toggleLandmineLarge(player);
				}else if(split[1].equalsIgnoreCase("small")){
					plugin.toggleLandmineSmall(player);
				}else{
					player.sendMessage("Wrong command");
				}
			}else if(split.length == 1){
				if (split[0].equalsIgnoreCase("/landmine") || split[0].equalsIgnoreCase("/lm")) {
					//Run the method toggleVision for player
					if (!LandMine.Permissions.has(player, "landmine.place")) {
					    return;
					}else{
						plugin.toggleLandmine(player);
						event.setCancelled(true);
					}
				}
			}
		}
}
