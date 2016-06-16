package ca.shaw.andrewbailey;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class Proximity {
	public String world;
	public int X;
	public int Y;
	public int Z;
	public BlockFace face;
	public float radius;
	public Byte power=0;
	public static int count;
	public Proximity(Block b,float inputRadius){
		world = b.getWorld().getName();
		X = b.getX();
		Y = b.getY();
		Z = b.getZ();
		face = b.getState().getData().toString();
		radius = 0;
		if(count > -1){
			count++;
		}else{
			count = 1;
		}
	}
	public Proximity(String inputWorld,int xVal,int yVal,int zVal,float inputRadius){
		world = inputWorld;
		X = xVal;
		Y = yVal;
		Z = zVal;
		radius = inputRadius;
		if(count > -1){
			count++;
		}else{
			count = 1;
		}
	}
	public int checkPerimeter(int lag, int progress){
		Player[] pList = Bukkit.getServer().getOnlinePlayers();
		while(lag > 0 && progress < pList.length){
			if(Math.abs(pList[progress].getLocation().getX() - X) <= radius){
				if(Math.abs(pList[progress].getLocation().getY() - Y) <= radius){
					if(Math.abs(pList[progress].getLocation().getZ() - Z) <= radius){
						power=1;
						return -1;
					}else{
						lag--;
						progress++;
					}
				}else{
					lag--;
					progress++;
				}
			}else{
				lag--;
				progress++;
			}
		}
		if(lag==0){
			return progress;
		}else{
			power=0;
			return -1;
		}
	}
	public Boolean equals(Proximity compare){
		if(compare.X == X && compare.Y == Y && compare.Z == Z && compare.world.equals(world)){
			return true;
		}
		return false;
	}
	public Block getBlock(){
		World w = Bukkit.getWorld(world);
		Block b = w.getBlockAt(X, Y, Z);
		return b;
	}
	public void reset() {
		Block b = Bukkit.getWorld(world).getBlockAt(X,Y,Z);
		b.setType(Material.SIGN_POST);
		Sign s = (Sign)b.getState();
		
		org.bukkit.material.Sign mat = new org.bukkit.material.Sign();
		
		s.setData(BlockFace.valueOf(facing));
		s.setLine(0, ChatColor.GREEN + "[Proximity]");
		s.setLine(1, ChatColor.RED + "" + radius);
		s.update();
	}
}
