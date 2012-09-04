package org.Dragonphase.PlayerList;

import java.util.ArrayList;
import java.util.List;
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
	public static List<String> Handled = new ArrayList<String>();

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
	
	public void setHandled(String string){
		if (Handled.contains(string)) return;
		Handled.add(string);
	}
	
	public boolean getHandled(String string){
		if (Handled.contains(string)) return true;
		return false;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		String[] globalPlayers = {translateColor(getConfig().getString("PlayerList.global-players.one-player")), translateColor(getConfig().getString("PlayerList.global-players.more-players"))};
		
		if (commandLabel.equalsIgnoreCase("list") || commandLabel.equalsIgnoreCase("plist")){
			if (sender.hasPermission("playerlist.list")){
				int playerNum = getServer().getOnlinePlayers().length;
				if (playerNum == 1){
					sender.sendMessage(globalPlayers[0].replace("%players", ""+playerNum));
				}else{
					sender.sendMessage(globalPlayers[1].replace("%players", ""+playerNum));
				}
				
				Handled.clear();
				
				for (String key : getConfig().getKeys(true)){
					if (!key.contains("PlayerList.ranks.")) continue;
					
					int numPlayers = 0;
					String displayRank = key.replace("PlayerList.ranks.", "");
					String players = "";
					
					try{
						displayRank = displayRank.substring(0, displayRank.indexOf("."));
						if (getHandled(displayRank)) continue;
						String rankDisplayColor = translateColor(getConfig().getString("PlayerList.ranks." + displayRank + ".color"));
						
						for (String keys : getConfig().getKeys(true)){
							if (!keys.contains("PlayerList.ranks." + displayRank + ".")) continue;
							
							String actualRank = keys.replace("PlayerList.ranks." + displayRank + ".", "");
							String rankNameColor = translateColor(getConfig().getString("PlayerList.ranks." + displayRank + "." + actualRank));
							
							for (Player onlinePlayer : getServer().getOnlinePlayers()){
								if (permission.getPrimaryGroup(onlinePlayer).equalsIgnoreCase(actualRank)){
									numPlayers ++;
									if (getConfig().getBoolean("PlayerList.use-displayname")){
										players = players + onlinePlayer.getDisplayName() + ChatColor.RESET + ", ";
									}else{
										players = players + rankNameColor + onlinePlayer.getName() + ChatColor.RESET + ", ";
									}
								}
							}
						}

						if (numPlayers == 0) continue;
						
						if (players.endsWith(", ")){
							players = players.substring(0, players.length() - 2);
						}

						if (getConfig().getBoolean("PlayerList.show-on-same-line")){
							sender.sendMessage(rankDisplayColor + displayRank + ChatColor.RESET + " (" + numPlayers + "): " + players);
						}else{
							sender.sendMessage(rankDisplayColor + displayRank + ChatColor.RESET + " (" + numPlayers + "):");
							sender.sendMessage(players);
						}
						setHandled(displayRank);
						
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
