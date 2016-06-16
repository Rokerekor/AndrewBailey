package ca.shaw.andrewbailey;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;


public class BlockLocation{
	String world;
	int x;
	int y;
	int z;
	public BlockLocation(Block b){
		world = b.getWorld().getName();
		x = b.getX();
		y = b.getY();
		z = b.getZ();
	}
	public BlockLocation(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public BlockLocation(int x, int y, int z, String world){
		this.x = x;
		this.y = y;
		this.z = z;
		this.world = world;
	}
	public Block getBlock(){return Bukkit.getWorld(world).getBlockAt(x,y,z);}
	public String getWorld(){return world;}
	public int getX(){return x;}
	public int getY(){return y;}
	public int getZ(){return z;}
	public BlockLocation minus(BlockLocation toMinus){
		return new BlockLocation(x-toMinus.x,y-toMinus.y,z-toMinus.z,world);
	}
	public BlockLocation add(BlockLocation toAdd){
		return new BlockLocation(x+toAdd.x,y+toAdd.y,z+toAdd.z,world);
	}
	public void set(BlockLocation bL){
		x=bL.x;
		y=bL.y;
		z=bL.z;
	}
	public BlockLocation invert(){
		return new BlockLocation(-x,-y,-z);
	}
	public Boolean equivalent(BlockLocation toTest){
		if(x == toTest.getX() && y == toTest.getY() && z == toTest.getZ()){
			return true;
		}
		return false;
	}
	public String toString(){
		return("World=" + world + "x=" + x + "y=" + y + "z=" + z);
				
	}
}
