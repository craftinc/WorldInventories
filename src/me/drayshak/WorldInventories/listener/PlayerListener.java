package me.drayshak.WorldInventories.listener;

import java.util.HashMap;
import me.drayshak.WorldInventories.Group;
import me.drayshak.WorldInventories.InventoryStoredType;
import me.drayshak.WorldInventories.PlayerStats;
import me.drayshak.WorldInventories.WorldInventories;
import me.drayshak.WorldInventories.InventoryLoadType;
import me.drayshak.WorldInventories.api.WorldInventoriesAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener
{
    private final WorldInventories plugin;
    
    public PlayerListener(final WorldInventories plugin)
    {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Player player = event.getEntity().getPlayer();
        
        if (WorldInventories.exempts.contains(player.getName().toLowerCase())) {
            WorldInventories.logDebug("Ignoring exempt player death: " + player.getName());
            return;
        }
        
        Group group = WorldInventoriesAPI.findGroup(player.getWorld().getName());
        
        HashMap<Integer, ItemStack[]> tosave = new HashMap<Integer, ItemStack[]>();
        tosave.put(InventoryStoredType.ARMOUR, new ItemStack[4]);
        tosave.put(InventoryStoredType.INVENTORY, new ItemStack[36]);
            
        plugin.savePlayerInventory(player.getName(), group, InventoryLoadType.INVENTORY, tosave);

        if (plugin.getConfig().getBoolean("dostats")) {
            plugin.savePlayerStats(player.getName(), group, new PlayerStats(20, 20, 0, 0, 0, 0F, null));
        }   

        plugin.sendMessage("died-message", player, ChatColor.GREEN + WorldInventories.locale.get("died-message") + group.getName());
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        
        String fromWorldName = event.getFrom().getName();
        String toWorldName = player.getLocation().getWorld().getName();
        
        if(WorldInventories.exempts.contains(player.getName().toLowerCase()))
        {
            WorldInventories.logDebug("Ignoring exempt player world switch: " + player.getName());
            return;
        }           
        
        if (!fromWorldName.equals(toWorldName)) // FIXME: shouldn't this always be true?
        {
            WorldInventories.logDebug("Player " + player.getName() + " moved from world " + fromWorldName + " to " + toWorldName);
            
            Group fromgroup = WorldInventoriesAPI.findGroup(fromWorldName);
            Group togroup = WorldInventoriesAPI.findGroup(toWorldName);
            
            HashMap<Integer, ItemStack[]> tosave = new HashMap<Integer, ItemStack[]>();
            tosave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
            tosave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());            
            
            plugin.savePlayerInventory(player.getName(), fromgroup, me.drayshak.WorldInventories.InventoryLoadType.INVENTORY, tosave);

            // TODO: global config string class
            if (plugin.getConfig().getBoolean("dostats")) {
                plugin.savePlayerStats(player, fromgroup);
            }    
            
            if (!fromgroup.getName().equals(togroup.getName()))
            {
                plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player.getName(), togroup, me.drayshak.WorldInventories.InventoryLoadType.INVENTORY));
                if (plugin.getConfig().getBoolean("dostats"))
                {
                    plugin.setPlayerStats(player, plugin.loadPlayerStats(player.getName(), togroup));
                }
                
                if(plugin.getConfig().getBoolean("dogamemodeswitch"))
                {
                    player.setGameMode(togroup.getGameMode());
                }
                
                plugin.sendMessage("changed-message", player, ChatColor.GREEN + WorldInventories.locale.get("changed-message") + togroup.getName());
            }
            else
            {
                plugin.sendMessage("nochange-message", player, ChatColor.GREEN + WorldInventories.locale.get("nochange-message") + togroup.getName());
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        
        String world = player.getLocation().getWorld().getName();
        
        WorldInventories.logDebug("Player " + player.getName() + " quit from world: " + world);
        
        if(WorldInventories.exempts.contains(player.getName().toLowerCase()))
        {
            WorldInventories.logDebug("Ignoring exempt player logout: " + player.getName());
            return;
        }           
        
        Group tGroup = WorldInventoriesAPI.findGroup(world);

        // Don't save if we don't care where we are (default group)
        //if (tGroup != null)
        //{            
            WorldInventories.logDebug("Saving inventory of " + player.getName());
            
            HashMap<Integer, ItemStack[]> tosave = new HashMap<Integer, ItemStack[]>();
            tosave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
            tosave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());                      
            
            plugin.savePlayerInventory(player.getName(), tGroup, me.drayshak.WorldInventories.InventoryLoadType.INVENTORY, tosave);
            
            if (plugin.getConfig().getBoolean("dostats"))
            {
                plugin.savePlayerStats(player, tGroup);
            }
        //}
        
        // Save the Ender Chest contents
        if(player.getOpenInventory().getType() == InventoryType.ENDER_CHEST)
        {
            tosave.put(InventoryStoredType.ARMOUR, null);
            tosave.put(InventoryStoredType.INVENTORY, player.getOpenInventory().getTopInventory().getContents());                      

            plugin.savePlayerInventory(player.getName(), tGroup, me.drayshak.WorldInventories.InventoryLoadType.ENDERCHEST, tosave);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        // FIXME: typo? Remove the 'v'?
        // TODO: global config string class
        if (plugin.getConfig().getBoolean("loadinvonlogin"))
        {
            Player player = event.getPlayer();
            String world = player.getLocation().getWorld().getName();
            
            WorldInventories.logDebug("Player " + player.getName() + " join world: " + world);
            
            if(WorldInventories.exempts.contains(player.getName().toLowerCase()))
            {
                WorldInventories.logDebug("Ignoring exempt player join: " + player.getName());
                return;
            }            
            
            Group tGroup = WorldInventoriesAPI.findGroup(world);
            
            //WorldInventories.logDebug("Loading inventory of " + player.getName());
            plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player.getName(), tGroup, me.drayshak.WorldInventories.InventoryLoadType.INVENTORY));            
            
            if (plugin.getConfig().getBoolean("dostats"))
            {
                plugin.setPlayerStats(player, plugin.loadPlayerStats(player.getName(), tGroup));
            }
            
            if(plugin.getConfig().getBoolean("dogamemodeswitch"))
            {
                //WorldInventories.logDebug("Should change gamemode to " + tGroup.getGameMode().toString() + " for " + player.getName());
                event.getPlayer().setGameMode(tGroup.getGameMode());
            }              
            
            plugin.sendMessage("loaded-message", player, ChatColor.GREEN + WorldInventories.locale.get("loaded-message") + tGroup.getName());
        }
    }
}
