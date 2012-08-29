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
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	}
	
	public String translateColor(String message){
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		String[] globalPlayers = {translateColor(getConfig().getString("WorldPlayerList.global-players.one-player")), translateColor(getConfig().getString("WorldPlayerList.global-players.more-players"))};
		String[] worldPlayers = {translateColor(getConfig().getString("WorldPlayerList.world-players.one-player")), translateColor(getConfig().getString("WorldPlayerList.world-players.more-players"))};
		
		if (commandLabel.equalsIgnoreCase("list") || commandLabel.equalsIgnoreCase("worldlist") || commandLabel.equalsIgnoreCase("wlist")){
			if (sender.hasPermission("worldlist.list")){
				int playerNum = getServer().getOnlinePlayers().length;
				if (playerNum == 1){
					sender.sendMessage(globalPlayers[0].replace("%players", ""+playerNum));
				}else{
					sender.sendMessage(globalPlayers[1].replace("%players", ""+playerNum));
				}
				for (World world : getServer().getWorlds()){
					int worldPlayerNum = world.getPlayers().size();
					if (worldPlayerNum == 0) continue;
					if (worldPlayerNum == 1){
						sender.sendMessage(worldPlayers[0].replace("%player", ""+worldPlayerNum).replace("%world", world.getName()));
					}else{
						sender.sendMessage(worldPlayers[1].replace("%player", ""+worldPlayerNum).replace("%world", world.getName()));
					}
					String worldPlayerMessage = "";
					for (int i = 0; i < worldPlayerNum; i ++){
						if (worldPlayerNum > 1){
							if (i + 1 < 2){
								worldPlayerMessage = worldPlayerMessage + world.getPlayers().get(i).getDisplayName() + ", ";
							}else{
								worldPlayerMessage = worldPlayerMessage + world.getPlayers().get(i).getDisplayName();
							}
						}else{
							worldPlayerMessage = worldPlayerMessage + world.getPlayers().get(i).getDisplayName();
						}
					}
					sender.sendMessage(worldPlayerMessage);
				}
			}else{
				sender.sendMessage(ChatColor.RED + "You do not have permission!");
			}
		}
		if (commandLabel.equalsIgnoreCase("wpl")){
			if (args.length > 0){
				if (args[0].equalsIgnoreCase("reload") || args[0].startsWith("r")){
					if (sender.hasPermission("worldlist.reload")){
						try {
							this.reloadConfig();
							sender.sendMessage(ChatColor.GREEN + "WorldPlayerList " + getDescription().getVersion() + " reloaded.");
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "WorldPlayerList " + getDescription().getVersion() + " could not be reloaded.");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
				}
			}
		}
		return false;
	}
}
