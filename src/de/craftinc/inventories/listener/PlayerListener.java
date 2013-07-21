package de.craftinc.inventories.listener;

import java.util.HashMap;

import de.craftinc.inventories.*;

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
        
        if (plugin.isPlayerOnExemptList(player.getName())) {
            InventoriesLogger.logDebug("Ignoring exempt player death: " + player.getName());
            return;
        }
        
        Group group = plugin.findGroup(player.getWorld().getName());
        
        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, new ItemStack[4]);
        toSave.put(InventoryStoredType.INVENTORY, new ItemStack[36]);
            
        plugin.savePlayerInventory(player.getName(), group, InventoryLoadType.INVENTORY, toSave);

        if (plugin.getConfig().getBoolean("dostats")) {
            plugin.savePlayerStats(player.getName(), group, new PlayerStats(20, 20, 0, 0, 0, 0F, null));
        }   

        InventoriesLogger.sendMessage(Language.diedMessageKey,
                                      player,
                                      ChatColor.GREEN + plugin.getLocale().get(Language.diedMessageKey) + group.getName());
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
    {
        Player player = event.getPlayer();
        
        if (plugin.isPlayerOnExemptList(player.getName())) {
            InventoriesLogger.logDebug("Ignoring exempt player world switch: " + player.getName());
            return;
        }

        String fromWorldName = event.getFrom().getName();
        String toWorldName = player.getLocation().getWorld().getName();

        Group fromGroup = plugin.findGroup(fromWorldName);
        Group toGroup = plugin.findGroup(toWorldName);

        InventoriesLogger.logDebug("Player " + player.getName() + " moved from world " + fromWorldName + " to " + toWorldName);

        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
        toSave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());

        plugin.savePlayerInventory(player.getName(), fromGroup, de.craftinc.inventories.InventoryLoadType.INVENTORY, toSave);

        // TODO: global config string class
        if (plugin.getConfig().getBoolean("dostats")) {
            plugin.savePlayerStats(player, fromGroup);
        }

        if (!fromGroup.getName().equals(toGroup.getName())) {
            plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player.getName(), toGroup, de.craftinc.inventories.InventoryLoadType.INVENTORY));

            if (plugin.getConfig().getBoolean("dostats")) {
                plugin.setPlayerStats(player, plugin.loadPlayerStats(player.getName(), toGroup));
            }

            if (plugin.getConfig().getBoolean("dogamemodeswitch")) {
                player.setGameMode(toGroup.getGameMode());
            }

            InventoriesLogger.sendMessage(Language.changedMessageKey,
                                          player,
                                          ChatColor.GREEN + plugin.getLocale().get(Language.changedMessageKey) + toGroup.getName());
        }
        else {
            InventoriesLogger.sendMessage(Language.noChangeMessageKey,
                                          player,
                                          ChatColor.GREEN + plugin.getLocale().get(Language.noChangeMessageKey) + toGroup.getName());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        String world = player.getLocation().getWorld().getName();

        InventoriesLogger.logDebug("Player " + player.getName() + " quit from world: " + world);
        
        if (plugin.isPlayerOnExemptList(player.getName())) {
            InventoriesLogger.logDebug("Ignoring exempt player logout: " + player.getName());
            return;
        }           
        
        Group toGroup = plugin.findGroup(world);

        // Don't save if we don't care where we are (default group)
        //if (tGroup != null)
        //{            
        InventoriesLogger.logDebug("Saving inventory of " + player.getName());
            
            HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
            toSave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
            toSave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());
            
            plugin.savePlayerInventory(player.getName(), toGroup, de.craftinc.inventories.InventoryLoadType.INVENTORY, toSave);
            
            if (plugin.getConfig().getBoolean("dostats")) {
                plugin.savePlayerStats(player, toGroup);
            }
        //}
        
        // Save the Ender Chest contents
        if (player.getOpenInventory().getType() == InventoryType.ENDER_CHEST) {
            toSave.put(InventoryStoredType.ARMOUR, null);
            toSave.put(InventoryStoredType.INVENTORY, player.getOpenInventory().getTopInventory().getContents());

            plugin.savePlayerInventory(player.getName(), toGroup, de.craftinc.inventories.InventoryLoadType.ENDERCHEST, toSave);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        // TODO: global config string class
        if (plugin.getConfig().getBoolean("loadinvonlogin")) {
            Player player = event.getPlayer();
            String world = player.getLocation().getWorld().getName();

            InventoriesLogger.logDebug("Player " + player.getName() + " join world: " + world);
            
            if (plugin.isPlayerOnExemptList(player.getName())) {
                InventoriesLogger.logDebug("Ignoring exempt player join: " + player.getName());
                return;
            }            
            
            Group toGroup = plugin.findGroup(world);
            
            //WorldInventories.logDebug("Loading inventory of " + player.getName());
            plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player.getName(), toGroup, de.craftinc.inventories.InventoryLoadType.INVENTORY));
            
            if (plugin.getConfig().getBoolean("dostats")) {
                plugin.setPlayerStats(player, plugin.loadPlayerStats(player.getName(), toGroup));
            }
            
            if (plugin.getConfig().getBoolean("dogamemodeswitch")) {
                //WorldInventories.logDebug("Should change gamemode to " + tGroup.getGameMode().toString() + " for " + player.getName());
                event.getPlayer().setGameMode(toGroup.getGameMode());
            }

            InventoriesLogger.sendMessage(Language.loadedMessageKey,
                                          player,
                                          ChatColor.GREEN + plugin.getLocale().get(Language.loadedMessageKey) + toGroup.getName());
        }
    }
}
