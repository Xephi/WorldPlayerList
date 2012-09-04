package org.Dragonphase.PlayerList;

import java.util.logging.Logger;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerList extends JavaPlugin{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static PlayerList plugin;
	public static Permission permission = null;

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }
	
	@Override
	public void onDisable(){
		PluginDescriptionFile PDF = this.getDescription();
		logger.info(PDF.getName() + " disabled.");
	}

	@Override
	public void onEnable(){
		PluginDescriptionFile PDF = this.getDescription();
		logger.info(PDF.getName() + " version " + PDF.getVersion() + " enabled.");
		getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
		if (getServer().getPluginManager().getPlugin("Vault") == null){
			logger.info(ChatColor.RED + "Vault is not installed. Disabling PlayerList.");
			getServer().getPluginManager().disablePlugin(this);
		}else{
			logger.info(ChatColor.GREEN + "Vault is installed. Continuing");
			setupPermissions();
		}
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
	}
	
	public String translateColor(String message){
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		String[] globalPlayers = {translateColor(getConfig().getString("PlayerList.global-players.one-player")), translateColor(getConfig().getString("PlayerList.global-players.more-players"))};
		
		if (commandLabel.equalsIgnoreCase("list") || commandLabel.equalsIgnoreCase("playerlist") || commandLabel.equalsIgnoreCase("plist")){
			if (sender.hasPermission("playerlist.list")){
				int playerNum = getServer().getOnlinePlayers().length;
				if (playerNum == 1){
					sender.sendMessage(globalPlayers[0].replace("%players", ""+playerNum));
				}else{
					sender.sendMessage(globalPlayers[1].replace("%players", ""+playerNum));
				}
				
				for (String key : getConfig().getKeys(true)){
					if (!key.contains("PlayerList.ranks.")) continue;
					
					String displayRank = key.replace("PlayerList.ranks.", "");
					try{
						displayRank = displayRank.substring(0, displayRank.indexOf("."));
						String actualRank = key.replace("PlayerList.ranks." + displayRank + ".", "");
						String rankTabColor = translateColor(getConfig().getString("PlayerList.ranks." + displayRank + "." + actualRank));
						
						String title = rankTabColor + displayRank + ChatColor.RESET;
						String message = "  ";
						int numPeople = 0;
						
						for (Player onlinePlayer : getServer().getOnlinePlayers()){
							if (permission.getPrimaryGroup(onlinePlayer).equalsIgnoreCase(actualRank)){
								numPeople ++;
								message = message + rankTabColor + onlinePlayer.getName() + ChatColor.RESET + ", ";
							}
						}
						if (numPeople == 0) continue;
						
						if (message.endsWith(", ")){
							message = message.substring(0, message.length() - 2);
						}
						
						sender.sendMessage(title + ":");
						sender.sendMessage(message);
						
					}catch (Exception ex){
						continue;
					}
				}
			}else{
				sender.sendMessage(ChatColor.RED + "You do not have permission!");
			}
		}
		if (commandLabel.equalsIgnoreCase("playerlist")){
			if (args.length > 0){
				if (args[0].equalsIgnoreCase("reload") || args[0].startsWith("r")){
					if (sender.hasPermission("playerlist.reload")){
						try {
							this.reloadConfig();
							sender.sendMessage(ChatColor.GREEN + "PlayerList " + getDescription().getVersion() + " reloaded.");
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "PlayerList " + getDescription().getVersion() + " could not be reloaded.");
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
