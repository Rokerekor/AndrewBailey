package ca.shaw.andrewbailey;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SnakeArena{
	public BlockLocation currentDirection;
	public BlockLocation lastDirection;
	public BlockLocation upDirection;
	public BlockLocation leftDirection;
	public String player;
	Block firstCorner;
	Block secondCorner;
	Block upArrow;
	Block downArrow;
	Block leftArrow;
	Block rightArrow;
	Block startButton;
	Block leaderBoard;
	Block scoreBoard;
	boolean gameInProgress = false;
	Snake plugin;
	String[] highScoreNames = new String[3];
	File file;
	int[] highScores = new int[3];
	public SnakeArena(Snake plugin,SnakePlayer p, File file){
		this.file = file;
		this.plugin = plugin;
		firstCorner = p.getFirstCorner();
		secondCorner = p.getSecondCorner();
		upArrow = p.getUpArrow();
		downArrow = p.getDownArrow();
		leftArrow = p.getLeftArrow();
		rightArrow = p.getRightArrow();
		startButton = p.getStartButton();
		leaderBoard = p.getLeaderBoard();
		scoreBoard = p.getScoreBoard();
		YamlConfiguration config = new YamlConfiguration();
		try{
			config.load(file);
		} catch (Exception e){
			e.printStackTrace();
		}
		for(int i=0;i<3;i++){
			highScoreNames[i] = config.getString("HighScores." + (i+1) + ".Name");
			highScores[i] = config.getInt("HighScores." + (i+1) + ".Score");
		}
		setLeaderBoard();
	}
	public SnakeArena(Snake plugin,File file){
		this.plugin = plugin;
		this.file = file;
		YamlConfiguration config = new YamlConfiguration();
		try{
			config.load(file);
		} catch (Exception e){
			e.printStackTrace();
		}
		firstCorner = Bukkit.getWorld(config.getString("FirstCorner.World")).getBlockAt(config.getInt("FirstCorner.X"),config.getInt("FirstCorner.Y"),config.getInt("FirstCorner.Z"));
		secondCorner = Bukkit.getWorld(config.getString("SecondCorner.World")).getBlockAt(config.getInt("SecondCorner.X"),config.getInt("SecondCorner.Y"),config.getInt("SecondCorner.Z"));
		upArrow = Bukkit.getWorld(config.getString("UpArrow.World")).getBlockAt(config.getInt("UpArrow.X"),config.getInt("UpArrow.Y"),config.getInt("UpArrow.Z"));
		downArrow = Bukkit.getWorld(config.getString("DownArrow.World")).getBlockAt(config.getInt("DownArrow.X"),config.getInt("DownArrow.Y"),config.getInt("DownArrow.Z"));
		leftArrow = Bukkit.getWorld(config.getString("LeftArrow.World")).getBlockAt(config.getInt("LeftArrow.X"),config.getInt("LeftArrow.Y"),config.getInt("LeftArrow.Z"));
		rightArrow = Bukkit.getWorld(config.getString("RightArrow.World")).getBlockAt(config.getInt("RightArrow.X"),config.getInt("RightArrow.Y"),config.getInt("RightArrow.Z"));
		startButton = Bukkit.getWorld(config.getString("StartButton.World")).getBlockAt(config.getInt("StartButton.X"),config.getInt("StartButton.Y"),config.getInt("StartButton.Z"));
		scoreBoard = Bukkit.getWorld(config.getString("ScoreBoard.World")).getBlockAt(config.getInt("ScoreBoard.X"),config.getInt("ScoreBoard.Y"),config.getInt("ScoreBoard.Z"));
		leaderBoard = Bukkit.getWorld(config.getString("LeaderBoard.World")).getBlockAt(config.getInt("LeaderBoard.X"),config.getInt("LeaderBoard.Y"),config.getInt("LeaderBoard.Z"));		
		for(int i=0;i<3;i++){
			highScoreNames[i] = config.getString("HighScores." + (i+1) + ".Name");
			highScores[i] = config.getInt("HighScores." + (i+1) + ".Score");
		}
		setLeaderBoard();
		setScoreBoard(0);
	}
	private void setLeaderBoard(){
		Sign sign = (Sign) leaderBoard.getState();
		sign.setLine(0, "[LeaderBoard]");
		for(int i=0;i<3;i++){
			sign.setLine(i+1, highScoreNames[i] + ":" + highScores[i]);
		}
		sign.update();
	}
	public void setScoreBoard(int score){
		Sign sign = (Sign) scoreBoard.getState();
		sign.setLine(0, "[ScoreBoard]");
		sign.setLine(1, "" + score);
		sign.setLine(2, "");
		sign.setLine(3, "");
		sign.update();
	}
	public boolean checkStartButton(Block clickedBlock, Player player){
		if(clickedBlock.equals(startButton) && gameInProgress == false){
			plugin.createRepeater(this,player);
			gameInProgress = true;
			return true;
		}
		return false;
	}
	public Block getFirstCorner(){return firstCorner;}
	public Block getSecondCorner(){return secondCorner;}
	public boolean blockPunched(Block block, Player p){
		if(p.getName().equals(player)){
			if(block.equals(upArrow)){
				if(!lastDirection.equivalent(upDirection.invert())){
					currentDirection = upDirection;
				}
			}else if(block.equals(downArrow)){
				if(!lastDirection.equivalent(upDirection))
				{
					currentDirection = upDirection.invert();
				}
			}else if(block.equals(leftArrow)){
				if(!lastDirection.equivalent(leftDirection.invert())){
					currentDirection = leftDirection;
				}
			}else if(block.equals(rightArrow)){
				if(!lastDirection.equivalent(leftDirection)){
					currentDirection = leftDirection.invert();
				}
			}else{
				return false;
			}
			return true;
		}
		return false;
	}
	public void end(int score, String player){
		for(int i=0;i<3;i++){
			if(score > highScores[i]){
				for(int j=2;j>i;j--){
					highScores[j] = highScores[j-1];
					highScoreNames[j] = highScoreNames[j-1];
				}
				highScores[i] = score;
				highScoreNames[i] = player;
				break;
			}
		}
		YamlConfiguration config = new YamlConfiguration();
		try{
			config.load(file);
		} catch (Exception e){
			e.printStackTrace();
		}
		for(int i=0;i<3;i++){
			config.set("HighScores." + (i+1) + ".Name",highScoreNames[i]);
			config.set("HighScores." + (i+1) + ".Score",highScores[i]);
		}
		try{
			config.save(file);
		} catch (IOException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setLeaderBoard();
		gameInProgress = false;
	}
}
