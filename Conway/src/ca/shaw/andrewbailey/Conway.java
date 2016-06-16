package ca.shaw.andrewbailey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Lever;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class Conway extends JavaPlugin{
	public final conwayListener cwayListner =  new conwayListener(this);
	public Block firstCorner;
	public Block secondCorner;
	public ArrayList<String> firstStep =  new ArrayList();
	public ArrayList<String> secondStep =  new ArrayList();
	public ArrayList<conwayPlayer> playerList = new ArrayList();
	public ArrayList<ConwayArena> arenaList = new ArrayList();
	public void onEnable(){
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(cwayListner, this);
		try {
			loadArenas();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		getLogger().info("Conway Enabled");
	}
	public void onDisable(){
		getLogger().info("Conway Disabled");
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player player = (Player) sender;
		if(cmd.getName().equalsIgnoreCase("conway")){
			if(player.hasPermission("conway.create")){
				if(args.length == 1){
					conwayPlayer p = checkPlayer(player);
					if(p.creatingConway() == 0){
						p.isCreatingConway(args[0]);
					}else{
						player.sendMessage("You are already creating a Conway Machine.");
					}
					return true;
				}else{
					player.sendMessage("The usage is '/conway <name>'");
				}
			}else{
				player.sendMessage("You do not have permission to play the game of life.");
			}
		}
		return false;
	}
	public void createConway(conwayPlayer p) throws FileNotFoundException, IOException, InvalidConfigurationException{
		ConwayArena newArena = new ConwayArena(this,p);
		arenaList.add(newArena);
		
		File newArenaFile = new File(this.getDataFolder() + "/Machines/", p.getAName() + ".yml");
		if(!newArenaFile.exists()){                        // checks if the yaml does not exists
			newArenaFile.getParentFile().mkdirs();         // creates the /plugins/<pluginName>/ directory if not found
			this.copy(this.getResource("arenaTemplate.yml"), newArenaFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
		    setCoords(newArenaFile,"FirstCorner",p.getFirstCorner());
		    setCoords(newArenaFile,"SecondCorner",p.getSecondCorner());
		    setCoords(newArenaFile,"Lever",p.getLever());
		}
	}
	public conwayPlayer checkPlayer(Player player){
		for(int i=0;i<playerList.size();i++){
			if(playerList.get(i).getID().equals(player.getUniqueId())){
				return playerList.get(i);
			}
		}
		conwayPlayer newPlayer = new conwayPlayer(this,player.getUniqueId());
		playerList.add(newPlayer);
		return newPlayer;
	}
	public void leverSwitched(Block clickedBlock) {
		for(int i=0;i<arenaList.size();i++){
			ConwayArena a = arenaList.get(i);
			if(a.getLever().equals(clickedBlock.getLocation())){
				Lever l = (Lever)clickedBlock.getState().getData();
				if(l.isPowered()){
					a.start();
				}else{
					a.stop();
				}
			}
		}
	}
	private void setCoords(File file, String string, Block firstCorner) throws FileNotFoundException, IOException, InvalidConfigurationException{
		FileConfiguration config = new YamlConfiguration();
		config.load(file);
		config.set(string + ".World", firstCorner.getWorld().getName());
		config.set(string + ".X", firstCorner.getX());
		config.set(string + ".Y", firstCorner.getY());
		config.set(string + ".Z", firstCorner.getZ());
		config.save(file);
	}
	public void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len=in.read(buf))>0){
                out.write(buf,0,len);
            }
            out.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	private void loadArenas() throws FileNotFoundException, IOException, InvalidConfigurationException{
		File folder = new File(this.getDataFolder() + "/Machines/");
		if(!folder.exists()){
			folder.mkdirs();
		}
		File[] files = folder.listFiles();
		for(int i=0;i<files.length;i++){
			ConwayArena newArena = new ConwayArena(this,files[i]);
			arenaList.add(newArena);
		}
	}
}
