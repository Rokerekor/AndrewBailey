package ca.shaw.andrewbailey;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ProximityDetector extends JavaPlugin{
	
	private static int progress = 0;
	private static int proxNum = 0;
	private static int proxNum2 = 0;
	private static String sqlConnect = null;
	private static String sqlDBName = null;
	private static String databaseType = null;
	public static ArrayList<Proximity> proximityList = new ArrayList<Proximity>();
	public static String prefix = ChatColor.GREEN + "[" + ChatColor.RED + "ProximityDetector" + ChatColor.GREEN + "] ";
	public static int lagRestrict = 100;
	static float maxProx = 0;
	static boolean enforceProx = false;
	public static ProximityDetector plugin;
	@Override
	public void onEnable(){
		plugin = this;
		enableMetrics();
		loadConfiguration();
		databaseSettings();
		if(this.isEnabled()){
			loadDetectors();
			Bukkit.getServer().getPluginManager().registerEvents(new pListener(), this);
			this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			    public void run() {
			    	checkProximities();
			    	checkPower();
			    }
			}, 1L, 1L);
			getLogger().info("ProximityDetector Enabled");
		}
		checkDuds();
	}
	@Override	
	public void onDisable(){
		getLogger().info("ProximityDetector Disabled");
	}
	private Runnable checkProximities() {
		while(proxNum<proximityList.size()){
			progress = proximityList.get(proxNum).checkPerimeter(lagRestrict, progress);
			if(progress == -1){
				proxNum++;
				progress=0;
				return null;
			}else{
				return null;
			}
		}
		proxNum=0;
		return null;
	}	
	private Runnable checkPower(){
		int lag = lagRestrict;
		while(proxNum2<proximityList.size() && lag>0){
			Proximity prox = proximityList.get(proxNum2);
			World world = Bukkit.getServer().getWorld(prox.world);
			Block b = world.getBlockAt(prox.X,prox.Y,prox.Z);
			if(prox.power == 1){
				b.setType(Material.REDSTONE_TORCH_ON);
			}else{
				prox.reset();
			}
			proxNum2++;
			lag--;
		}
		if(lag==0){
			return null;
		}
		proxNum2=0;
		return null;
	}
	private void enableMetrics() {
		try {
		    Metrics metrics = new Metrics(this);
		    metrics.start();
		} catch (IOException e) {
		    // Failed to submit the stats :-(
		}
	}
	public void loadConfiguration(){
	    getConfig().addDefault("LagRestricter", lagRestrict);
	    getConfig().addDefault("RangeSettings.MaxProximity", maxProx);	
	    getConfig().addDefault("RangeSettings.EnforceOnExistingSigns", enforceProx);
	    getConfig().addDefault("database", "flatfile");
	    getConfig().addDefault("mysql.hostname", "");
	    getConfig().addDefault("mysql.port", "3306");
	    getConfig().addDefault("mysql.databasename", "");
	    getConfig().addDefault("mysql.username", "");
	    getConfig().addDefault("mysql.password", "");
	    getConfig().options().copyDefaults(true);
	    saveConfig();
	    if(getConfig().getInt("LagRestricter") > 0){
	    	lagRestrict = getConfig().getInt("LagRestricter");
	    	getLogger().info("LagValue set to: " + lagRestrict);
	    }else{
	    	getLogger().info("LagValue not recognizable, defaulting to: " + lagRestrict);
	    }
	    getLogger().info("" + getConfig().getInt("MaxProximity"));
	    maxProx = (float) getConfig().getDouble("RangeSettings.MaxProximity");
	    enforceProx = getConfig().getBoolean("RangeSettings.EnforceOnExistingSigns");
	}
	private void databaseSettings(){
		Statement statement = null;
		ResultSet rs = null;
		try {
    	   	Class.forName("com.mysql.jdbc.Driver");
		}catch (Exception e) {
	       	getLogger().info("Could not class thingy");
	       	//e.printStackTrace();
		}
	   	if(getConfig().getString("database").equalsIgnoreCase("mysql")){ //getConfig().getString("database") == "mysql"
	   		getLogger().info("Connecting to MySQL...");
	   		databaseType = "mysql";
	   		String sqlHost = getConfig().getString("mysql.hostname");
	   		String sqlPort = getConfig().getString("mysql.port");
	   		sqlDBName = getConfig().getString("mysql.databasename");
	   		String sqlUser = getConfig().getString("mysql.username");
	   		String sqlPass = getConfig().getString("mysql.password");
	   		try{
	   			sqlConnect = ("jdbc:mysql://"+sqlHost+":"+sqlPort+"/"+sqlDBName+"?user="+sqlUser+"&password="+sqlPass);
	   			Connection sql = DriverManager.getConnection(sqlConnect);
	   			statement = sql.createStatement();
	   			try {
	   	            rs = statement.executeQuery("SELECT * FROM proximity");
	   			}catch (Exception e) {
	   				getLogger().info("No table found, creating now..");
					statement.executeUpdate("CREATE TABLE " + sqlDBName + ".proximity(radius TINYINT, World VARCHAR(25), X INT,Y INT,Z INT) ENGINE=MyISAM DEFAULT CHARSET=latin1");
					getLogger().info("Table Created");
	   			}
	   			sql.close();
	   		}catch (Exception e) {
	   			getLogger().info("Could not connect to MySQL, please check your login info.");
	   			this.setEnabled(false);
	   			return;
	   		}
	   		getLogger().info("Connected to MySQL");
	   	}else if(getConfig().getString("database").equalsIgnoreCase("flatfile")){
	   		databaseType = "flatfile";
	   		File file = new File(getDataFolder() + "/database.yml");
	   		if(!file.exists()){
				copy(getResource("database.yml"), file);
			}
	   	}else{
	   		getLogger().info("Database Setting Invalid, please choose mysql or flatfile.");
	   		getLogger().info("Plugin Disabled");
	   		this.setEnabled(false);
	   	}
	}
	private void loadDetectors(){
		Statement statement = null;
		ResultSet rs = null;
		if(databaseType.equalsIgnoreCase("mysql")){
			try{
				Connection sql = DriverManager.getConnection(sqlConnect);
				statement = sql.createStatement(); 
				try {
		            rs = statement.executeQuery("SELECT * FROM proximity");
				}catch (Exception e) {
					getLogger().info("SQL Error");
				}
				while(rs.next()){
					Proximity newProximity = new Proximity(rs.getString(2),rs.getInt(3),rs.getInt(4),rs.getInt(5),rs.getInt(1));
					proximityList.add(newProximity);
				}
				sql.close();
			}catch (Exception e) {
				getLogger().info("Could not connect to MySQL, please check your login info.");
				return;
			}
		}else if(databaseType.equalsIgnoreCase("flatfile")){
			File file = new File(plugin.getDataFolder() + "/database.yml");
			FileConfiguration f = new YamlConfiguration();
			try {
				f.load(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
			List<String> list;
			list = f.getStringList("Proximities");
			if(list != null){
				for(int i=0;i<list.size();i++){
					String[] s = list.get(i).split(":");
					Proximity newProximity = new Proximity(s[1],Integer.parseInt(s[2]),Integer.parseInt(s[3]),Integer.parseInt(s[4]),Float.parseFloat(s[0]));
					proximityList.add(newProximity);
				}
			}
			f.set("Proximities", list);
			try {
				f.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void checkDuds() {
		for(int i=0;i<proximityList.size();i++){
			Block b = proximityList.get(i).getBlock();
			if(b.getType() == Material.SIGN_POST){
				Sign s = (Sign)b.getState();
				if(s.getLine(0).equalsIgnoreCase("[Proximity]")){
					s.setLine(0, ChatColor.GREEN + "[Proximity]");
					s.update();
				}
				if(s.getLine(0).equalsIgnoreCase(ChatColor.GREEN + "[Proximity]")){
					float radius = Float.parseFloat(ChatColor.stripColor(s.getLine(1)));
					Bukkit.getLogger().info(""+radius);
					if(enforceProx && maxProx != 0 && radius > maxProx){
						Bukkit.getLogger().info("yes");
						proximityList.get(i).radius = maxProx;
						s.setLine(1, "" + ChatColor.RED + maxProx);
						s.update();
					}else{
						if(!s.getLine(1).contains("" + ChatColor.RED)){
							s.setLine(1, ChatColor.RED + s.getLine(1));
							s.update();
						}
					}
					
					continue;
				}
			}else{
				try {
					deleteSign(b.getWorld().getName(), b.getX(), b.getY(), b.getZ());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void greenMessage(Player target, String message){
		target.sendMessage(prefix + ChatColor.GREEN + message);
	}
	public static void redMessage(Player target, String message){
		target.sendMessage(prefix + ChatColor.RED + message);
	}
	public static void addProximity(Proximity newProximity) throws SQLException {
		proximityList.add(newProximity);
		storeProximity(newProximity);
	}
	private static void storeProximity(Proximity newProximity) throws SQLException {
		if(databaseType.equalsIgnoreCase("mysql")){
			Connection sql = DriverManager.getConnection(sqlConnect);
			Statement statement = sql.createStatement();
			statement.executeUpdate("INSERT INTO " + sqlDBName +  ".proximity (radius,World,X,Y,Z) VALUES ('" + newProximity.radius + "', '" + newProximity.world + "', '" + newProximity.X + "', '"  + newProximity.Y + "', '" + newProximity.Z + "')");
		}else if(databaseType.equalsIgnoreCase("flatfile")){
			File file = new File(plugin.getDataFolder() + "/database.yml");
			FileConfiguration f = new YamlConfiguration();
			try {
				f.load(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> list;
			list = f.getStringList("Proximities");
			if(list == null){
				list = Arrays.asList(newProximity.radius + ":" + newProximity.world + ":" + newProximity.X + ":" + newProximity.Y + ":" + newProximity.Z);
			}else{
				list.add(newProximity.radius + ":" + newProximity.world + ":" + newProximity.X + ":" + newProximity.Y + ":" + newProximity.Z);
			}
			f.set("Proximities", list);
			try {
				f.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static boolean deleteSign(String world, int x, int y, int z) throws SQLException {
		Proximity toDelete = new Proximity(world,x,y,z,0);
		for(int i=0;i<proximityList.size();i++){
			if(proximityList.get(i).equals(toDelete)){
				proximityList.remove(i);
			}
		}
		if(databaseType.equalsIgnoreCase("mysql")){
			Connection sql = DriverManager.getConnection(sqlConnect);
			Statement statement = sql.createStatement();
			try {
	            ResultSet rs = statement.executeQuery("SELECT * FROM proximity WHERE World='" + world + "' AND X=" + x + " AND Y=" + y + " AND Z=" + z);
	            if(rs.next() == true){
	            	statement.executeUpdate("DELETE FROM " + sqlDBName + ".proximity WHERE World='" + world + "' AND X=" + x + " AND Y=" + y + " AND Z=" + z);
	            	return true;
	    		}
	            return false;
			}catch (Exception e) {
				e.printStackTrace();
			}
			sql.close();
			return false;
		}else{
			File file = new File(plugin.getDataFolder() + "/database.yml");
			FileConfiguration f = new YamlConfiguration();
			try {
				f.load(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			List<String> l = f.getStringList("Proximities");
			for(int i=0;i<l.size();i++){
				String[] s = l.get(i).split(":");
				if(world.equalsIgnoreCase(s[1]) && x == Integer.parseInt(s[2]) && y == Integer.parseInt(s[3]) && z == Integer.parseInt(s[4])){
					l.remove(i);
				}
			}
			f.set("Proximities", l);
			try {
				f.save(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		}
	}
	public static float checkRadius(float radius){
		if(maxProx > 0){
			if(radius > maxProx){
				return maxProx;
			}
		}
		return radius;
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
	public static void torchDestroyed(Block block) {
		Proximity toDelete = new Proximity(block);
		for(int i=0;i<proximityList.size();i++){
			if(proximityList.get(i).equals(toDelete)){
				proximityList.get(i).reset();
			}
		}
	}
}
