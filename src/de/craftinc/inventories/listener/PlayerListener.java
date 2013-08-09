package de.craftinc.inventories.listener;

import java.util.HashMap;

import de.craftinc.inventories.*;

import de.craftinc.inventories.persistence.InventoryLoadType;
import de.craftinc.inventories.persistence.InventoryPersistenceManager;
import de.craftinc.inventories.persistence.InventoryStoredType;
import de.craftinc.inventories.persistence.StatsPersistenceManager;
import de.craftinc.inventories.utils.ConfigurationKeys;
import de.craftinc.inventories.utils.Language;
import de.craftinc.inventories.utils.Logger;
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
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {
        Plugin plugin = Plugin.getSharedInstance();
        Player player = event.getEntity().getPlayer();
        
        if (plugin.isPlayerOnExemptList(player.getName())) {
            Logger.logDebug("Ignoring exempt player death: " + player.getName());
            return;
        }
        
        Group group = plugin.findGroup(player.getWorld().getName());
        
        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, new ItemStack[4]);
        toSave.put(InventoryStoredType.INVENTORY, new ItemStack[36]);

        InventoryPersistenceManager.savePlayerInventory(player.getName(), group, InventoryLoadType.INVENTORY, toSave);

        if (plugin.getConfig().getBoolean(ConfigurationKeys.doStatisticsKey)) {
            StatsPersistenceManager.savePlayerStats(player.getName(), group, new PlayerStats(20, 20, 0, 0, 0, 0F, null));
        }   

        Logger.sendMessage(Language.diedMessageKey,
                           player,
                           ChatColor.GREEN + plugin.getLocale().get(Language.diedMessageKey) + group.getName());
    }
    
    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event)
    {
        Plugin plugin = Plugin.getSharedInstance();
        Player player = event.getPlayer();
        
        if (plugin.isPlayerOnExemptList(player.getName())) {
            Logger.logDebug("Ignoring exempt player world switch: " + player.getName());
            return;
        }

        String fromWorldName = event.getFrom().getName();
        String toWorldName = player.getLocation().getWorld().getName();

        Group fromGroup = plugin.findGroup(fromWorldName);
        Group toGroup = plugin.findGroup(toWorldName);

        Logger.logDebug("Player " + player.getName() + " moved from world " + fromWorldName + " to " + toWorldName);

        HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
        toSave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
        toSave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());

        InventoryPersistenceManager.savePlayerInventory(player.getName(), fromGroup, InventoryLoadType.INVENTORY, toSave);

        if (plugin.getConfig().getBoolean(ConfigurationKeys.doStatisticsKey)) {
            StatsPersistenceManager.savePlayerStats(player, fromGroup);
        }

        if (!fromGroup.getName().equals(toGroup.getName())) {
            InventoryPersistenceManager.setPlayerInventory(player, InventoryPersistenceManager.loadPlayerInventory(player.getName(), toGroup, InventoryLoadType.INVENTORY));

            if (plugin.getConfig().getBoolean(ConfigurationKeys.doStatisticsKey)) {
                StatsPersistenceManager.setPlayerStats(player, StatsPersistenceManager.loadPlayerStats(player.getName(), toGroup));
            }

            if (plugin.getConfig().getBoolean(ConfigurationKeys.doGameModeSwitchKey)) {
                player.setGameMode(toGroup.getGameMode());
            }

            Logger.sendMessage(ConfigurationKeys.hideChangedInventoryKey,
                               player,
                               ChatColor.GREEN + plugin.getLocale().get(Language.changedMessageKey) + toGroup.getName());
        }
        else {
            Logger.sendMessage(ConfigurationKeys.hideNotChangedInventoryKey,
                               player,
                               ChatColor.GREEN + plugin.getLocale().get(Language.noChangeMessageKey) + toGroup.getName());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Plugin plugin = Plugin.getSharedInstance();
        Player player = event.getPlayer();
        String world = player.getLocation().getWorld().getName();

        Logger.logDebug("Player " + player.getName() + " quit from world: " + world);
        
        if (plugin.isPlayerOnExemptList(player.getName())) {
            Logger.logDebug("Ignoring exempt player logout: " + player.getName());
            return;
        }           
        
        Group toGroup = plugin.findGroup(world);

        // Don't save if we don't care where we are (default group)
        //if (tGroup != null)
        //{            
        Logger.logDebug("Saving inventory of " + player.getName());
            
            HashMap<Integer, ItemStack[]> toSave = new HashMap<Integer, ItemStack[]>();
            toSave.put(InventoryStoredType.ARMOUR, player.getInventory().getArmorContents());
            toSave.put(InventoryStoredType.INVENTORY, player.getInventory().getContents());

        InventoryPersistenceManager.savePlayerInventory(player.getName(), toGroup, InventoryLoadType.INVENTORY, toSave);
            
            if (plugin.getConfig().getBoolean(ConfigurationKeys.doStatisticsKey)) {
                StatsPersistenceManager.savePlayerStats(player, toGroup);
            }
        //}
        
        // Save the Ender Chest contents
        if (player.getOpenInventory().getType() == InventoryType.ENDER_CHEST) {
            toSave.put(InventoryStoredType.ARMOUR, null);
            toSave.put(InventoryStoredType.INVENTORY, player.getOpenInventory().getTopInventory().getContents());

            InventoryPersistenceManager.savePlayerInventory(player.getName(), toGroup, InventoryLoadType.ENDERCHEST, toSave);
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Plugin plugin = Plugin.getSharedInstance();

        if (plugin.getConfig().getBoolean(ConfigurationKeys.loadInventoriesOnLoginKey)) {
            Player player = event.getPlayer();
            String world = player.getLocation().getWorld().getName();

            Logger.logDebug("Player " + player.getName() + " join world: " + world);
            
            if (plugin.isPlayerOnExemptList(player.getName())) {
                Logger.logDebug("Ignoring exempt player join: " + player.getName());
                return;
            }            
            
            Group toGroup = plugin.findGroup(world);
            
            //Plugin.logDebug("Loading inventory of " + player.getName());
            InventoryPersistenceManager.setPlayerInventory(player, InventoryPersistenceManager.loadPlayerInventory(player.getName(), toGroup, InventoryLoadType.INVENTORY));
            
            if (plugin.getConfig().getBoolean(ConfigurationKeys.doStatisticsKey)) {
                StatsPersistenceManager.setPlayerStats(player, StatsPersistenceManager.loadPlayerStats(player.getName(), toGroup));
            }
            
            if (plugin.getConfig().getBoolean(ConfigurationKeys.doGameModeSwitchKey)) {
                //Plugin.logDebug("Should change game mode to " + tGroup.getGameMode().toString() + " for " + player.getName());
                event.getPlayer().setGameMode(toGroup.getGameMode());
            }

            Logger.sendMessage(ConfigurationKeys.hideLoadedInventoryKey,
                               player,
                               ChatColor.GREEN + plugin.getLocale().get(Language.loadedMessageKey) + toGroup.getName());
        }
    }
}
