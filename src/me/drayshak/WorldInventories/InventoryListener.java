package me.drayshak.WorldInventories;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class InventoryListener implements Listener
{
    private final WorldInventories plugin;
    
    InventoryListener(final WorldInventories plugin)
    {
       this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryOpen(InventoryOpenEvent event)
    {
        Inventory inventory = event.getInventory();
        
        if(inventory.getType() == InventoryType.ENDER_CHEST)
        {
            String player = event.getPlayer().getName();
            String world = event.getPlayer().getWorld().getName();
            
            Group worldgroup = WorldInventories.findFirstGroupForWorld(world);
            
            WorldInventories.logDebug("Ender Chest opened by " + player + " in world " + world + ", group " + worldgroup);
            
            inventory.setContents(plugin.loadPlayerEnderChest(player, worldgroup).getItems());
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClosed(InventoryCloseEvent event)
    {
        Inventory inventory = event.getInventory();
        
        if(inventory.getType() == InventoryType.ENDER_CHEST)
        {
            String player = event.getPlayer().getName();
            String world = event.getPlayer().getWorld().getName();
            
            Group worldgroup = WorldInventories.findFirstGroupForWorld(world);
            
            WorldInventories.logDebug("Ender Chest closed by " + player + " in world " + world + ", group " + worldgroup);
            
            plugin.savePlayerEnderChest(player, worldgroup, new EnderChestHelper(inventory.getContents()));
        }        
    }
}