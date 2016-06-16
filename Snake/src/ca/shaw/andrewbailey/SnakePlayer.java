package ca.shaw.andrewbailey;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class SnakePlayer{
	Snake plugin;
	String name;
	int status = 0;
	Block firstCorner;
	Block secondCorner;
	Block upArrow;
	Block downArrow;
	Block leftArrow;
	Block rightArrow;
	Block startButton;
	Block leaderBoard;
	Block scoreBoard;
	String arenaName;
	public SnakePlayer(Snake plugin,String name){
		this.plugin = plugin;
		this.name = name;
	}
	public String getName(){ return name;}
	public int getStatus(){ return status;}
	public Block getFirstCorner(){return firstCorner;}
	public Block getSecondCorner(){return secondCorner;}
	public Block getUpArrow(){return upArrow;}
	public Block getDownArrow(){return downArrow;}
	public Block getLeftArrow(){return leftArrow;}
	public Block getRightArrow(){return rightArrow;}
	public Block getStartButton(){return startButton;}
	public Block getLeaderBoard(){return leaderBoard;}
	public Block getScoreBoard(){return scoreBoard;}
	public void nextStep(String aName){
		Player p = Bukkit.getServer().getPlayer(name);
		arenaName = aName;
		if(status == 0){
			p.sendMessage("Click the top left corner of your arena.");
		}
		status++;
	}
	public boolean clickedBlock(Block block){
		Player p = Bukkit.getServer().getPlayer(name);
		if(status > 0){
			if(status == 1){
				firstCorner = block;
				p.sendMessage("Click the bottom right corner of your arena.");
			}else if(status == 2){
				secondCorner = block;
				p.sendMessage("Click the up arrow of your gamepad.");
			}else if(status == 3){
				upArrow = block;
				p.sendMessage("Click the down arrow of your gamepad.");
			}else if(status == 4){
				downArrow = block;
				p.sendMessage("Click the left arrow of your gamepad.");
			}else if(status == 5){
				leftArrow = block;
				p.sendMessage("Click the right arrow of your gamepad.");
			}else if(status == 6){
				rightArrow = block;
				p.sendMessage("Click the sign you want to be your leaderboard.");
			}else if(status == 7){
				if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
					leaderBoard = block;
					p.sendMessage("Click the sign you want to be your scoreboard.");
				}else{
					p.sendMessage("Click a sign.");
					return true;
				}
			}else if(status == 8){
				if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST){
					scoreBoard = block;
					p.sendMessage("Click the start button of your arena.");
				}else{
					p.sendMessage("Click a sign.");
					return true;
				}
			}else if(status == 9){
				if(block.getType() == Material.WOOD_BUTTON || block.getType() == Material.STONE_BUTTON){
					startButton = block;
					p.sendMessage("Snake Created.");
					try{
						plugin.createSnakeArena(this);
					} catch (Exception e){
						e.printStackTrace();
					}
					status=0;
					return true;
				}else{
					p.sendMessage("Click a button.");
					return true;
				}
			}
			status++;
			return true;
		}
		return false;
	}
}
