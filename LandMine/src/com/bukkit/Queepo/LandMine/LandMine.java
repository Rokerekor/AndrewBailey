//The Package
package com.bukkit.Queepo.LandMine;

//All the imports
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * LandMine for Bukkit
 * 
 * @author Queepo
 */
public class LandMine extends JavaPlugin {
	private final LandMinePlayerListener playerListener = new LandMinePlayerListener(this);
	private final LandMineBlockListener blockListener = new LandMineBlockListener(this);
	public final HashMap<Player, ArrayList<Block>> landmineSmall = new HashMap();
	public final HashMap<Player, ArrayList<Block>> landmineMedium = new HashMap();
	public final HashMap<Player, ArrayList<Block>> landmineLarge = new HashMap();
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	public static PermissionHandler Permissions;
	static int mineLocation;
	public LandMine(PluginLoader pluginLoader, Server instance,PluginDescriptionFile desc, File folder, File plugin,		ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
	}
	
	
	@Override
	// When the plugin is disabled this method is called.
	public void onDisable() {
		// Print "LandMine Disabled" on the log.
		System.out.println("LandMine Disabled");

	}

	@Override
	// When the plugin is enabled this method is called.
	public void onEnable() {
		// Create the pluginmanage pm.
		PluginManager pm = getServer().getPluginManager();
		// Create PlayerCommand listener
		pm.registerEvent(Event.Type.PLAYER_COMMAND, this.playerListener,Event.Priority.Normal, this);
		// Create BlockPlaced listener
		pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener,Event.Priority.Normal, this);
		// Create BlockPlaced listener
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener,Event.Priority.Normal, this);
		// Get the infomation from the yml file.
		PluginDescriptionFile pdfFile = this.getDescription();
		// Print that the plugin has been enabled!
		System.out.println(pdfFile.getName() + " by Queepo, version " + pdfFile.getVersion() + " is enabled!");		setupPermissions();
	}

	// Used when debugging
	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}
	public void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if(this.Permissions == null) {
		    if(test != null) {
			Permissions = ((Permissions)test).getHandler();
		    } else {
			//log.info("["+name+"] Permission system not enabled. Disabling plugin.");
			this.getServer().getPluginManager().disablePlugin(this);
		    }
		}
    }
	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}

	// The method enabled which checks to see if the player is in the hashmap
	// landmineUsers
	public boolean small(Player player) {
		return this.landmineSmall.containsKey(player);
	}
	public boolean medium(Player player) {
		return this.landmineMedium.containsKey(player);
	}
	public boolean large(Player player) {
		return this.landmineLarge.containsKey(player);
	}

	// The method toggleVision which if the player is on the hashmap will remove
	// the player else it will add the player.
	// Also sends user a message to notify them.
	public void toggleLandmine(Player player) {
		if (small(player)) {
			this.landmineSmall.remove(player);
			player.sendMessage("Landmine disabled");
		}else if (medium(player)) {
			this.landmineMedium.remove(player);
			player.sendMessage("Landmine disabled");
		}else if (large(player)) {
			this.landmineLarge.remove(player);
			player.sendMessage("Landmine disabled");
		}else {
			this.landmineSmall.put(player, null);
			player.sendMessage("Landmine enabled, your landmines are normal size.");
		}
	}
	public void toggleLandmineSmall(Player player) {
		if (small(player)) {
			player.sendMessage("Your landmines are already small");
		} else if (medium(player)) {
			this.landmineMedium.remove(player);
			player.sendMessage("Your landmines are now normal size.");
			this.landmineSmall.put(player, null);
		} else if (large(player)){
			this.landmineLarge.remove(player);
			player.sendMessage("Your landmines are now normal size.");
			this.landmineSmall.put(player, null);
		} else {
			player.sendMessage("Landmine enabled, your landmines are normal sized.");
			this.landmineSmall.put(player, null);
		}
	}
	public void toggleLandmineMedium(Player player) {
		if (small(player)) {
			this.landmineSmall.remove(player);
			player.sendMessage("Your landmines are now pretty big");
			this.landmineMedium.put(player, null);
		} else if (medium(player)) {
			player.sendMessage("Your landmines are already pretty big.");
		} else if (large(player)){
			this.landmineLarge.remove(player);
			player.sendMessage("Your landmines are now pretty big.");
			this.landmineMedium.put(player, null);
		} else {
			player.sendMessage("Landmine enabled, your landmines are pretty big.");
			this.landmineMedium.put(player, null);
		}
	}
	public void toggleLandmineLarge(Player player) {
		if (small(player)) {
			this.landmineSmall.remove(player);
			player.sendMessage("Your landmines are now huge!");
			this.landmineLarge.put(player, null);
		} else if (medium(player)) {
			this.landmineMedium.remove(player);
			player.sendMessage("Your landmines are now huge!");
			this.landmineLarge.put(player, null);
		} else if (large(player)){
			player.sendMessage("Your landmines are already huge.");
		} else {
			player.sendMessage("Landmine enabled, your landmines are huge!");
			this.landmineMedium.put(player, null);
		}
	}

}
