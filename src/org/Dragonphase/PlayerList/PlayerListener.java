package org.Dragonphase.PlayerList;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static PlayerList plugin;
	public static YamlConfiguration playerConfig;
	
	public PlayerListener(PlayerList instance){
		plugin = instance;
	}
	
	public String translateColor(String message){
		message = ChatColor.translateAlternateColorCodes('&', message);
		return message;
	}
	
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event){
		Player player = event.getPlayer();
		for (String key : plugin.getConfig().getKeys(true)){
			if (!key.contains("PlayerList.ranks.")) continue;
			
			String displayRank = key.replace("PlayerList.ranks.", "");
			try{
				displayRank = displayRank.substring(0, displayRank.indexOf("."));
				String actualRank = key.replace("PlayerList.ranks." + displayRank + ".", "");
				String rankTabColor = translateColor(plugin.getConfig().getString("PlayerList.ranks." + displayRank + "." + actualRank));

				if (PlayerList.permission.getPrimaryGroup(player).equalsIgnoreCase(actualRank)){
					String tabName = rankTabColor + player.getName();
					player.setPlayerListName(tabName.substring(0, 14) + ChatColor.RESET);
				}
				else{
				}
			}catch (Exception ex){
				logger.info(ex.getMessage());
				continue;
			}
		}
	}
}