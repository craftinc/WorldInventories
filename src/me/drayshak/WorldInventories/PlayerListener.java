package me.drayshak.WorldInventories;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{

    private final WorldInventories plugin;
    
    PlayerListener(final WorldInventories plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        
        String fromworld = event.getFrom().getName();
        String toworld = player.getLocation().getWorld().getName();
        
        if (!fromworld.equals(toworld))
        {
            WorldInventories.logDebug("Player " + player.getName() + " moved from world " + fromworld + " to " + toworld);
            
            Group fromgroup = WorldInventories.findFirstGroupForWorld(fromworld);
            Group togroup = WorldInventories.findFirstGroupForWorld(toworld);
            
            plugin.savePlayerInventory(player.getName(), fromgroup, plugin.getPlayerInventory(player));
            if (plugin.getConfig().getBoolean("dostats"))
            {
                plugin.savePlayerStats(player, fromgroup);
            }
            
            String fromgroupname = "default";
            if (fromgroup != null)
            {
                fromgroupname = fromgroup.getName();
            }            
            
            String togroupname = "default";
            if (togroup != null)
            {
                togroupname = togroup.getName();
            }            
            
            if (!fromgroupname.equals(togroupname))
            {
                plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player, togroup));
                if (plugin.getConfig().getBoolean("dostats"))
                {
                    plugin.setPlayerStats(player, plugin.loadPlayerStats(player, togroup));
                }
                
                if (plugin.getConfig().getBoolean("donotifications"))
                {
                    if (plugin.getConfig().getBoolean("dostats"))
                    {
                        player.sendMessage(ChatColor.GREEN + "Changed player set to group: " + togroupname);
                    }
                    else
                    {
                        player.sendMessage(ChatColor.GREEN + "Changed inventory set to group: " + togroupname);
                    }
                }
            }
            else
            {
                if (plugin.getConfig().getBoolean("donotifications"))
                {
                    if (plugin.getConfig().getBoolean("dostats"))
                    {
                        player.sendMessage(ChatColor.GREEN + "No player set change necessary for group: " + togroupname);
                    }
                    else
                    {
                        player.sendMessage(ChatColor.GREEN + "No inventory change necessary for group: " + togroupname);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        
        String world = player.getLocation().getWorld().getName();
        
        WorldInventories.logDebug("Player " + player.getName() + " quit from world: " + world);
        
        Group tGroup = WorldInventories.findFirstGroupForWorld(world);

        // Don't save if we don't care where we are (default group)
        if (tGroup != null)
        {            
            WorldInventories.logDebug("Saving inventory of " + player.getName());
            plugin.savePlayerInventory(player.getName(), tGroup, plugin.getPlayerInventory(player));
            
            if (plugin.getConfig().getBoolean("dostats"))
            {
                plugin.savePlayerStats(player, tGroup);
            }
        }
        
        // Save the Ender Chest contents
        if(player.getOpenInventory().getType() == InventoryType.ENDER_CHEST)
        {
            plugin.savePlayerEnderChest(player.getName(), tGroup, new EnderChestHelper(player.getOpenInventory().getTopInventory().getContents()));
        }
    }
    
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        if (plugin.getConfig().getBoolean("loadinvonlogin"))
        {
            Player player = event.getPlayer();
            String world = player.getLocation().getWorld().getName();
            
            WorldInventories.logDebug("Player " + player.getName() + " logged in to world: " + world);
            
            Group tGroup = WorldInventories.findFirstGroupForWorld(world);
            
            WorldInventories.logDebug("Loading inventory of " + player.getName());
            plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player, tGroup));            
            
            if (plugin.getConfig().getBoolean("dostats"))
            {
                plugin.setPlayerStats(player, plugin.loadPlayerStats(player, tGroup));
            }            
        }
    }
}
