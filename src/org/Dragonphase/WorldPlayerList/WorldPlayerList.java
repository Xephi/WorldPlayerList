package org.Dragonphase.WorldPlayerList;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldPlayerList extends JavaPlugin{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static WorldPlayerList plugin;
	
	@Override
	public void onDisable(){
		PluginDescriptionFile PDF = this.getDescription();
		logger.info(PDF.getName() + " disabled.");
	}

	@Override
	public void onEnable(){
		PluginDescriptionFile PDF = this.getDescription();
		logger.info(PDF.getName() + " version " + PDF.getVersion() + " enabled.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (commandLabel.equalsIgnoreCase("list") || commandLabel.equalsIgnoreCase("worldlist") || commandLabel.equalsIgnoreCase("wlist")){
			if (sender.hasPermission("worldlist.list")){
				int playerNum = getServer().getOnlinePlayers().length;
				if (playerNum == 1){
					sender.sendMessage(ChatColor.BLUE + "There is " + ChatColor.RED + playerNum + ChatColor.BLUE + " player online.");
				}else{
					sender.sendMessage(ChatColor.BLUE + "There are " + ChatColor.RED + playerNum + ChatColor.BLUE + " players online.");
				}
				for (World world : getServer().getWorlds()){
					int worldPlayerNum = world.getPlayers().size();
					if (worldPlayerNum == 0) continue;
					if (worldPlayerNum == 1){
						sender.sendMessage(ChatColor.RED + "" + worldPlayerNum + ChatColor.BLUE + " player is in " + ChatColor.RED + world.getName() + ChatColor.BLUE + ":");
					}else{
						sender.sendMessage(ChatColor.RED + "" + worldPlayerNum + ChatColor.BLUE + " players are in " + ChatColor.RED + world.getName() + ChatColor.BLUE + ":");
					}
					String worldPlayers = "";
					for (int i = 0; i < worldPlayerNum; i ++){
						if (worldPlayerNum > 1){
							if (i + 1 < 2){
								worldPlayers = worldPlayers + world.getPlayers().get(i).getDisplayName() + ", ";
							}else{
								worldPlayers = worldPlayers + world.getPlayers().get(i).getDisplayName();
							}
						}else{
							worldPlayers = worldPlayers + world.getPlayers().get(i).getDisplayName();
						}
					}
					sender.sendMessage(worldPlayers);
				}
			}else{
				sender.sendMessage(ChatColor.RED + "You do not have permission!");
			}
		}
		return false;
	}
}
