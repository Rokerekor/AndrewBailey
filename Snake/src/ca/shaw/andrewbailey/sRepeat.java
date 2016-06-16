package ca.shaw.andrewbailey;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class sRepeat extends BukkitRunnable {
	//Extends BukkitRunnable to create a run method
	 
	private final Snake plugin;
	private SnakeArena arena;
	private BlockLocation a;
	private BlockLocation b;
	private BlockLocation food;
	private int score = 0;
	int frozenAxis;
	ArrayList<BlockLocation> blockList = new ArrayList<BlockLocation>();
	public sRepeat(Snake plugin,SnakeArena arena, Player p) {
		arena.player = p.getName();
		this.plugin = plugin;
		this.arena = arena;
		a = new BlockLocation(arena.getFirstCorner());
		b = new BlockLocation(arena.getSecondCorner());
		int inc = 1;
		int inc2 = 1;
		if(a.getX() == b.getX()){
			frozenAxis = 1;
			if(a.getY() > b.getY()){
				arena.upDirection = new BlockLocation(0,1,0);
				inc = -1;
			}
			for(int y=a.getY();y!=b.getY()+inc;y+=inc){
				arena.leftDirection = new BlockLocation(0,0,-1);
				arena.currentDirection = new BlockLocation(0,0,1);
				if(a.getZ() > b.getZ()){
					arena.leftDirection = new BlockLocation(0,0,1);
					arena.currentDirection = new BlockLocation(0,0,-1);
					inc2 = -1;
				}
				for(int z=a.getZ();z!=b.getZ()+inc2;z+=inc2){
					Block c = Bukkit.getWorld(a.getWorld()).getBlockAt(a.getX(), y, z);
					if(c.getType() != Material.IRON_BLOCK){
						c.setType(Material.IRON_BLOCK);
					}
				}
			}
//		}else if(a.getY() == b.getY()){
//			if(a.getX() > b.getX()){
//				inc = -1;
//			}
//			for(int x=a.getX();x!=b.getX();x+=inc){
//				if(a.getZ() > b.getZ()){
//					inc2 = -1;
//				}
//				for(int z=a.getZ();z!=b.getZ();z+=inc2){
//					Block c = a.getWorld().getBlockAt(x, a.getY(), z);
//					if(c.getType() != Material.IRON_BLOCK){
//						c.setType(Material.IRON_BLOCK);
//						if(c.getType() != Material.IRON_BLOCK){
//							c.setType(Material.IRON_BLOCK);
//						}
//					}
//				}
//			}
		}else if(a.getZ() == b.getZ()){
			frozenAxis = 3;
			if(a.getY() > b.getY()){
				arena.upDirection = new BlockLocation(0,1,0);
				inc = -1;
			}
			for(int y=a.getY();y!=b.getY()+inc;y+=inc){
				arena.leftDirection = new BlockLocation(-1,0,0);
				arena.currentDirection = new BlockLocation(1,0,0);
				if(a.getX() > b.getX()){
					arena.leftDirection = new BlockLocation(1,0,0);
					arena.currentDirection = new BlockLocation(-1,0,0);
					inc2 = -1;
				}
				for(int x=a.getX();x!=b.getX()+inc2;x+=inc2){
					Block c = Bukkit.getWorld(a.getWorld()).getBlockAt(x, y, a.getZ());
					if(c.getType() != Material.IRON_BLOCK){
						c.setType(Material.IRON_BLOCK);
					}
				}
			}
		}
		BlockLocation start = new BlockLocation(Math.round((a.getX() + b.getX())/2),Math.round((a.getY() + b.getY())/2),Math.round((a.getZ() + b.getZ())/2),a.getWorld());
		BlockLocation secondBlock = start.add(arena.leftDirection);
		BlockLocation thirdBlock = secondBlock.add(arena.leftDirection);
		addBlock(start);
		addBlock(secondBlock);
		addBlock(thirdBlock);
		blockList.add(start);
		blockList.add(secondBlock);
		blockList.add(thirdBlock);
		arena.lastDirection = arena.currentDirection;
		arena.setScoreBoard(score);
		addFood();
	}
	private void addFood(){
		int x = (int) (b.getX() + Math.round(Math.random()*(a.getX() - b.getX())));
		int y = (int) (b.getY() + Math.round(Math.random()*(a.getY() - b.getY())));
		int z = (int) (b.getZ() + Math.round(Math.random()*(a.getZ() - b.getZ())));
		food = new BlockLocation(x,y,z,a.getWorld());
		Bukkit.getWorld(a.getWorld()).getBlockAt(x,y,z).setType(Material.GOLD_BLOCK);
	}
	private void addBlock(BlockLocation bL){
		Block b = Bukkit.getWorld(bL.getWorld()).getBlockAt(bL.getX(),bL.getY(),bL.getZ());
		b.setType(Material.OBSIDIAN);
	}
	private void removeBlock(BlockLocation bL){
		Block b = Bukkit.getWorld(bL.getWorld()).getBlockAt(bL.getX(),bL.getY(),bL.getZ());
		b.setType(Material.IRON_BLOCK);
	}
	private void advanceSnake(){
		BlockLocation newHead = blockList.get(0).add(arena.currentDirection);
		if(checkFood(newHead)){return;}
		if(checkBounds(newHead)){return;}
		addBlock(newHead);
		removeBlock(blockList.get(blockList.size()-1));
		blockList.add(0, newHead);
		blockList.remove(blockList.size()-1);
		arena.lastDirection = arena.currentDirection;
	}
	private Boolean checkBounds(BlockLocation test){
		if((test.getX() > a.getX() && test.getX() > b.getX()) || (test.getY() > a.getY() && test.getY() > b.getY()) || (test.getZ() > a.getZ() && test.getZ() > b.getZ()) || (test.getX() < a.getX() && test.getX() < b.getX()) || (test.getY() < a.getY() && test.getY() < b.getY()) || (test.getZ() < a.getZ() && test.getZ() < b.getZ())){
			end();
			return true;
		}
		for(int i=0;i<blockList.size();i++){
			if(test.equivalent(blockList.get(i))){	
				end();
				return true;
			}
		}
		return false;
	}
	private void end(){
		BlockLocation death = blockList.get(0);
		Bukkit.getWorld(death.getWorld()).getBlockAt(death.getX(), death.getY(), death.getZ()).setType(Material.REDSTONE_BLOCK);
		arena.end(score,arena.player);
		plugin.end(this);
	}
	private Boolean checkFood(BlockLocation test){
		for(int i=0;i<blockList.size();i++){
			BlockLocation t = blockList.get(i);
			if(t.equivalent(this.food)){
				blockList.add(0, test);
				addBlock(test);
				addFood();
				score+=10;
				arena.setScoreBoard(score);
				return true;
			}
		}
		return false;
	}
	public void run() {
		advanceSnake();
	}
}