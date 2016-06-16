package ca.shaw.andrewbailey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.io.Files;

public class Snake extends JavaPlugin{
	public final SnakeListener snakeListener =  new SnakeListener(this);
	public ArrayList<SnakePlayer> playerList = new ArrayList();
	public ArrayList<SnakeArena> snakeList = new ArrayList();
	public void onEnable(){
		loadArenas();
		PluginManager pm = Bukkit.getServer().getPluginManager();
		pm.registerEvents(snakeListener, this);
		Bukkit.getLogger().info("Snake enabled.");
	}
	public void onDisable(){
		Bukkit.getLogger().info("Snake disabled.");
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		Player p = (Player)sender;
		SnakePlayer pp = checkPlayer(p);
		if(cmd.getName().equalsIgnoreCase("snake") && p.hasPermission("snake.create")){
			if(args.length == 1){
				startSnakeCreation(pp,args[0]);
			}else{
				p.sendMessage("Usage: /snake NAME");
			}
			return true;
		}
		return false;
	}
	private void startSnakeCreation(SnakePlayer pp, String name){
		Player p = Bukkit.getPlayer(pp.getName());
		File newArenaFile = new File(this.getDataFolder() + "/Arenas/", name + ".yml");
		if(newArenaFile.exists()){  
			p.sendMessage("Snake Arena under that name already exists.");
			return;
		}
		if(pp.getStatus() == 0){
			pp.nextStep(name);
		}
	}
	public SnakePlayer checkPlayer(Player player){
		for(int i=0;i<playerList.size();i++){
			if(playerList.get(i).getName().equals(player.getName())){
				return playerList.get(i);
			}
		}
		SnakePlayer newPlayer = new SnakePlayer(this,player.getName());
		playerList.add(newPlayer);
		return newPlayer;
	}
	private void loadArenas(){
		File folder = new File(this.getDataFolder() + "/Arenas/");
		if(!folder.exists()){
			folder.mkdirs();
		}
		File[] files = folder.listFiles();
		for(int i=0;i<files.length;i++){
			SnakeArena newArena = new SnakeArena(this,files[i]);
			snakeList.add(newArena);
		}
	}
	public void createSnakeArena(SnakePlayer snakePlayer) throws FileNotFoundException, IOException, InvalidConfigurationException{
		File newArenaFile = new File(this.getDataFolder() + "/Arenas/", snakePlayer.arenaName + ".yml");
		FileConfiguration newArenaConfig = new YamlConfiguration();
		if(!newArenaFile.exists()){                        // checks if the yaml does not exists
			newArenaFile.getParentFile().mkdirs();         // creates the /plugins/<pluginName>/ directory if not found
			this.copy(this.getResource("arenaTemplate.yml"), newArenaFile); // copies the yaml from your jar to the folder /plugin/<pluginName>
		    setCoords(newArenaFile,"FirstCorner",snakePlayer.getFirstCorner());
		    setCoords(newArenaFile,"SecondCorner",snakePlayer.getSecondCorner());
		    setCoords(newArenaFile,"UpArrow",snakePlayer.getUpArrow());
		    setCoords(newArenaFile,"DownArrow",snakePlayer.getDownArrow());
		    setCoords(newArenaFile,"LeftArrow",snakePlayer.getLeftArrow());
		    setCoords(newArenaFile,"RightArrow",snakePlayer.getRightArrow());
		    setCoords(newArenaFile,"StartButton",snakePlayer.getStartButton());
		    setCoords(newArenaFile,"LeaderBoard",snakePlayer.getLeaderBoard());
		    setCoords(newArenaFile,"ScoreBoard",snakePlayer.getScoreBoard());
		}
		SnakeArena newArena = new SnakeArena(this,snakePlayer,newArenaFile);
		snakeList.add(newArena);
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
	public void buttonPressed(Block clickedBlock, Player player){
		for(int i=0;i<snakeList.size();i++){
			if(snakeList.get(i).checkStartButton(clickedBlock,player)){
				return;
			}
		}
		
	}
	public void createRepeater(SnakeArena snakeArena, Player player){
		BukkitTask TaskName = new sRepeat(this,snakeArena,player).runTaskTimer(this, 5, 5);
	}
	public boolean blockPunched(Block block, Player p){
		for(int i=0;i<snakeList.size();i++){
			if(snakeList.get(i).blockPunched(block,p)){
				return true;
			}
		}
		return false;
	}
	public void end(sRepeat sRepeat){
		sRepeat.cancel();
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
}
